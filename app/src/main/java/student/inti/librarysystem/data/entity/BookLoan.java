package student.inti.librarysystem.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.room.TypeConverters;
import student.inti.librarysystem.util.DateConverter;
import java.util.Date;

@Entity(tableName = "book_loans")
@TypeConverters(DateConverter.class)
public class BookLoan {
    @PrimaryKey
    @NonNull
    private String id;
    private String bookCode;
    private String bookId;
    private String bookName;
    private Date borrowDate;
    private Date returnDate;
    private String studentId;
    private int extensionWeeks;
    private boolean isReturned;

    // Default constructor for Room
    public BookLoan() {}

    // Constructor for normal use - mark with @Ignore
    @Ignore
    public BookLoan(String bookCode, String bookId, String bookName,
                    Date borrowDate, Date returnDate, String studentId) {
        this.bookCode = bookCode;
        this.bookId = bookId;
        this.bookName = bookName;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.studentId = studentId;
        this.extensionWeeks = 0;
        this.isReturned = false;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getExtensionWeeks() {
        return extensionWeeks;
    }

    public void setExtensionWeeks(int extensionWeeks) {
        this.extensionWeeks = extensionWeeks;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }
}