package student.inti.librarysystem.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import student.inti.librarysystem.data.LibraryDatabase;
import student.inti.librarysystem.data.dao.ExamPaperDao;
import student.inti.librarysystem.data.entity.ExamPaper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ExamPaperViewModel extends AndroidViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ExamPaperDao examPaperDao;
    private final MutableLiveData<List<ExamPaper>> examPapers = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ExamPaperViewModel(Application application) {
        super(application);
        // Use the existing database instance
        examPaperDao = LibraryDatabase.getDatabase(application).examPaperDao();
    }

    public LiveData<List<ExamPaper>> getExamPapers() {
        return examPapers;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Firebase operations with local caching
    public void loadExamPapers(String subjectCode, Integer year, Integer semester) {
        isLoading.setValue(true);
        Query query = db.collection("examPapers");

        try {
            // Apply filters only if they are provided and valid
            if (subjectCode != null && !subjectCode.trim().isEmpty()) {
                // Make case-insensitive by converting both to uppercase
                query = query.whereEqualTo("subjectCode", subjectCode.trim());
            }
            if (year != null && year > 0) {
                query = query.whereEqualTo("year", year);
            }
            if (semester != null && semester > 0) {
                query = query.whereEqualTo("semester", semester);
            }

            // Add some logging to debug
            System.out.println("Query filters: subjectCode=" + subjectCode +
                    ", year=" + year +
                    ", semester=" + semester);

            query.get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<ExamPaper> papersList = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            System.out.println("Document data: " + document.getData());
                            ExamPaper paper = document.toObject(ExamPaper.class);
                            if (paper != null) {
                                paper.setPaperId(document.getId());
                                papersList.add(paper);
                            }
                        }

                        if (papersList.isEmpty()) {
                            errorMessage.setValue("No exam papers found matching the filters");
                        }

                        examPapers.setValue(papersList);
                        isLoading.setValue(false);
                    })
                    .addOnFailureListener(e -> {
                        errorMessage.setValue("Failed to load exam papers: " + e.getMessage());
                        isLoading.setValue(false);
                    });

        } catch (Exception e) {
            errorMessage.setValue("Error building query: " + e.getMessage());
            isLoading.setValue(false);
        }
    }

    // Local database operations
    private void loadFromLocalDatabase(String subjectCode, Integer year, Integer semester) {
        new Thread(() -> {
            LiveData<List<ExamPaper>> localPapers;

            if (subjectCode != null && !subjectCode.isEmpty()) {
                localPapers = examPaperDao.getExamPapersByCourse(subjectCode);
            } else if (year != null) {
                localPapers = examPaperDao.getExamPapersByYear(year);
            } else {
                localPapers = examPaperDao.getAllExamPapers();
            }

            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                isLoading.setValue(false);
                localPapers.observeForever(papers -> {
                    if (papers != null && !papers.isEmpty()) {
                        examPapers.setValue(papers);
                    } else {
                        errorMessage.setValue("No offline data available");
                    }
                });
            });
        }).start();
    }

    private void insertExamPaper(ExamPaper paper) {
        new Thread(() -> {
            try {
                examPaperDao.insert(paper);
            } catch (Exception e) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                        errorMessage.setValue("Failed to cache exam paper: " + e.getMessage())
                );
            }
        }).start();
    }

    // Public methods for direct database access
    public LiveData<ExamPaper> getExamPaperById(String paperId) {
        return examPaperDao.getExamPaper(paperId);
    }

    public LiveData<List<ExamPaper>> getExamPapersBySubjectCode(String subjectCode) {
        return examPaperDao.getExamPapersByCourse(subjectCode);
    }

    public LiveData<List<ExamPaper>> getAllExamPapersFromDb() {
        return examPaperDao.getAllExamPapers();
    }

    public void searchExamPapers(String searchTerm) {
        isLoading.setValue(true);

        db.collection("examPapers")
                .orderBy("subjectCode")
                .startAt(searchTerm.toUpperCase())
                .endAt(searchTerm.toUpperCase() + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ExamPaper> papersList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            ExamPaper paper = document.toObject(ExamPaper.class);
                            if (paper != null) {
                                paper.setPaperId(document.getId());
                                papersList.add(paper);
                                insertExamPaper(paper); // Cache search results
                            }
                        }
                        examPapers.setValue(papersList);
                    } else {
                        errorMessage.setValue("Search failed: " + task.getException().getMessage());
                        // Fallback to local search
                        loadFromLocalDatabase(searchTerm, null, null);
                    }
                    isLoading.setValue(false);
                });
    }
}