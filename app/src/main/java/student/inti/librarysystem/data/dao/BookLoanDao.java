package student.inti.librarysystem.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import student.inti.librarysystem.data.entity.BookLoan;
import java.util.Date;
import java.util.List;

@Dao
public interface BookLoanDao {
    @Insert
    void insert(BookLoan loan);

    @Update
    void update(BookLoan loan);

    @Delete
    void delete(BookLoan loan);

    @Query("SELECT * FROM book_loans WHERE id = :loanId")
    LiveData<BookLoan> getLoan(long loanId);

    @Query("SELECT * FROM book_loans WHERE id = :loanId")
    BookLoan getLoanById(long loanId);

    @Query("SELECT * FROM book_loans WHERE studentId = :studentId")
    LiveData<List<BookLoan>> getStudentLoans(String studentId);

    @Query("SELECT * FROM book_loans WHERE studentId = :studentId AND dueDate >= date('now')")
    List<BookLoan> getActiveLoans(String studentId);

    @Query("SELECT * FROM book_loans WHERE studentId = :studentId AND dueDate < date('now')")
    LiveData<List<BookLoan>> getLoanHistory(String studentId);

    @Query("UPDATE book_loans SET dueDate = :newDueDate, extensionWeeks = :extensionWeeks WHERE id = :loanId")
    void extendLoan(long loanId, Date newDueDate, int extensionWeeks);

    @Query("SELECT * FROM book_loans WHERE dueDate < :currentDate AND studentId = :studentId")
    LiveData<List<BookLoan>> getOverdueLoans(String studentId, Date currentDate);

    @Query("SELECT * FROM book_loans WHERE studentId = :studentId AND bookId = :bookId AND dueDate >= :currentDate LIMIT 1")
    BookLoan getCurrentLoan(String studentId, String bookId, Date currentDate);
}