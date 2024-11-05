package student.inti.librarysystem.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import student.inti.librarysystem.data.entity.BookLoan;
import student.inti.librarysystem.repository.LibraryRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookLoanViewModel extends AndroidViewModel {
    private final LibraryRepository repository;
    private final MutableLiveData<String> extensionResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public BookLoanViewModel(Application application) {
        super(application);
        repository = new LibraryRepository(application);
    }

    public LiveData<List<BookLoan>> getStudentLoans(String studentId) {
        return repository.getStudentLoans(studentId);
    }

    public void extendLoan(BookLoan loan, int weeks) {
        isLoading.setValue(true);

        if (loan.getExtensionWeeks() >= 3) {
            extensionResult.setValue("Maximum extension limit reached");
            isLoading.setValue(false);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(loan.getReturnDate());
        calendar.add(Calendar.WEEK_OF_YEAR, weeks);
        Date newDueDate = calendar.getTime();

        repository.extendLoan(
                loan.getId(),
                newDueDate,
                loan.getExtensionWeeks() + weeks
        );

        extensionResult.setValue("Loan extended successfully");
        isLoading.setValue(false);
    }

    public LiveData<List<BookLoan>> getOverdueLoans(String studentId) {
        return repository.getOverdueLoans(studentId, new Date());
    }

    public LiveData<String> getExtensionResult() {
        return extensionResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}