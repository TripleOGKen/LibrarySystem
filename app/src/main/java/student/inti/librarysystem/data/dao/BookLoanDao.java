package student.inti.librarysystem.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import student.inti.librarysystem.data.entity.BookLoan;
import java.util.List;
import java.util.Date;

@Dao
public interface BookLoanDao {
    @Query("SELECT * FROM book_loans")
    List<BookLoan> getAllBookLoans();

    @Query("SELECT * FROM book_loans WHERE studentId = :studentId AND isReturned = 0 ORDER BY returnDate ASC")
    LiveData<List<BookLoan>> getCurrentLoans(String studentId);

    @Query("SELECT * FROM book_loans WHERE studentId = :studentId AND isReturned = 1 ORDER BY returnDate DESC")
    LiveData<List<BookLoan>> getLoanHistory(String studentId);

    @Query("SELECT COUNT(*) FROM book_loans WHERE studentId = :studentId AND isReturned = 0")
    int getCurrentLoanCount(String studentId);

    @Query("SELECT * FROM book_loans WHERE bookId = :bookId AND isReturned = 0")
    BookLoan getActiveLoanForBook(String bookId);

    @Query("SELECT * FROM book_loans WHERE studentId = :studentId")
    LiveData<List<BookLoan>> getStudentLoans(String studentId);

    @Query("SELECT * FROM book_loans WHERE studentId = :studentId AND returnDate < :currentDate AND isReturned = 0")
    LiveData<List<BookLoan>> getOverdueLoans(String studentId, Date currentDate);

    @Query("UPDATE book_loans SET returnDate = :newDueDate, extensionWeeks = :extensionWeeks WHERE id = :loanId")
    void extendLoan(String loanId, Date newDueDate, int extensionWeeks);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BookLoan bookLoan);

    @Update
    void update(BookLoan bookLoan);

    @Delete
    void delete(BookLoan bookLoan);
}