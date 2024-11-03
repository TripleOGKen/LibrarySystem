package student.inti.librarysystem.ui.bookloans;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import student.inti.librarysystem.data.LibraryDatabase;
import student.inti.librarysystem.data.entity.BookLoan;

public class BookLoanViewModel extends AndroidViewModel {
    private final LibraryDatabase database;
    private final ExecutorService executorService;
    private final MutableLiveData<List<BookLoan>> activeLoans = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private static final String TAG = "BookLoanViewModel";
    private final String currentUserId = "ST10001"; // Replace with actual logged-in user ID

    public BookLoanViewModel(Application application) {
        super(application);
        database = LibraryDatabase.getDatabase(application);
        executorService = Executors.newSingleThreadExecutor();
        loadActiveLoans();
    }

    private void loadActiveLoans() {
        executorService.execute(() -> {
            try {
                List<BookLoan> loans = database.bookLoanDao().getStudentLoans(currentUserId).getValue();
                if (loans != null) {
                    List<BookLoan> activeList = new ArrayList<>();
                    Date currentDate = new Date();
                    for (BookLoan loan : loans) {
                        if (loan.getDueDate().after(currentDate)) {
                            activeList.add(loan);
                        }
                    }
                    activeLoans.postValue(activeList);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading loans: " + e.getMessage());
                message.postValue("Failed to load loans");
            }
        });
    }

    public LiveData<List<BookLoan>> getActiveLoans() {
        return database.bookLoanDao().getStudentLoans(currentUserId);
    }

    public LiveData<List<BookLoan>> getLoanHistory() {
        return database.bookLoanDao().getLoanHistory(currentUserId);
    }

    public void extendLoan(long loanId, int weeks) {
        executorService.execute(() -> {
            try {
                BookLoan loan = database.bookLoanDao().getLoanById(loanId);
                if (loan != null) {
                    if (loan.getExtensionWeeks() + weeks > 3) {
                        message.postValue("Maximum extension period (3 weeks) exceeded");
                        return;
                    }

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(loan.getDueDate());
                    cal.add(Calendar.WEEK_OF_YEAR, weeks);

                    database.bookLoanDao().extendLoan(
                            loanId,
                            cal.getTime(),
                            loan.getExtensionWeeks() + weeks
                    );

                    message.postValue("Loan extended successfully");
                    loadActiveLoans();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error extending loan: " + e.getMessage());
                message.postValue("Failed to extend loan");
            }
        });
    }

    public LiveData<String> getMessage() {
        return message;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}