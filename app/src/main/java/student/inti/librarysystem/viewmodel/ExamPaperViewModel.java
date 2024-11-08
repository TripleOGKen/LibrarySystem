// ExamPaperViewModel.java
package student.inti.librarysystem.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import student.inti.librarysystem.data.entity.ExamPaper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;



public class ExamPaperViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<ExamPaper>> examPapers = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<ExamPaper>> getExamPapers() {
        return examPapers;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadExamPapers(String subjectCode, Integer year, Integer semester) {
        isLoading.setValue(true);

        Query query = db.collection("examPapers");

        // Apply filters if they are not null or empty
        if (subjectCode != null && !subjectCode.trim().isEmpty()) {
            query = query.whereEqualTo("subjectCode", subjectCode.trim());
        }
        if (year != null) {
            query = query.whereEqualTo("year", year);
        }
        if (semester != null) {
            query = query.whereEqualTo("semester", semester);
        }

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ExamPaper> papersList = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {                        ExamPaper paper = document.toObject(ExamPaper.class);
                        if (paper != null) {
                            paper.setPaperId(document.getId());
                            papersList.add(paper);
                        }
                    }
                    examPapers.setValue(papersList);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to load exam papers: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }
}