package student.inti.librarysystem.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import student.inti.librarysystem.data.entity.ExamPaper;
import student.inti.librarysystem.repository.LibraryRepository;
import java.util.List;

public class ExamPaperViewModel extends AndroidViewModel {
    private final LibraryRepository repository;

    public ExamPaperViewModel(Application application) {
        super(application);
        repository = new LibraryRepository(application);
    }

    public LiveData<List<ExamPaper>> getAllExamPapers() {
        return repository.getAllExamPapers();
    }

    public LiveData<List<ExamPaper>> getExamPapersByYear(int year) {
        return repository.getExamPapersByYear(year);
    }

    public LiveData<List<Integer>> getAllYears() {
        return repository.getAllYears();
    }
}