package student.inti.librarysystem.ui.bookloans;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import student.inti.librarysystem.data.entity.BookLoan;
import java.util.List;

public class BookLoanViewModel extends ViewModel {
    private final MutableLiveData<List<BookLoan>> currentLoans = new MutableLiveData<>();
    private final MutableLiveData<List<BookLoan>> loanHistory = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public void setCurrentLoans(List<BookLoan> loans) {
        currentLoans.setValue(loans);
    }

    public void setLoanHistory(List<BookLoan> loans) {
        loanHistory.setValue(loans);
    }

    public LiveData<List<BookLoan>> getCurrentLoans() {
        return currentLoans;
    }

    public LiveData<List<BookLoan>> getLoanHistory() {
        return loanHistory;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void updateLoan(BookLoan loan) {
        List<BookLoan> loans = currentLoans.getValue();
        if (loans != null) {
            for (int i = 0; i < loans.size(); i++) {
                if (loans.get(i).getId() == loan.getId()) {
                    loans.set(i, loan);
                    break;
                }
            }
            currentLoans.setValue(loans);
        }
    }
}