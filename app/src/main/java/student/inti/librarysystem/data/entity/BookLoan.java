package student.inti.librarysystem.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import student.inti.librarysystem.util.DateConverter;
import java.util.Date;

@Entity(tableName = "book_loans")
@TypeConverters(DateConverter.class)
public class BookLoan {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String loanId; // Firebase document ID
    private String bookCode;
    private String bookId;
    private String bookName;
    private String studentId;
    private Date borrowDate;
    private Date returnDate;
    private int extensionWeeks;
    private boolean isReturned;

    // Constructors
    public BookLoan() {}

    public BookLoan(String bookId, String bookCode, String bookName, String studentId,
                    Date borrowDate, Date returnDate) {
        this.bookId = bookId;
        this.bookCode = bookCode;
        this.bookName = bookName;
        this.studentId = studentId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.extensionWeeks = 0;
        this.isReturned = false;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    // Convert from Firebase model to Room entity
    public static BookLoan fromFirebaseModel(student.inti.librarysystem.BookLoan firebaseLoan) {
        BookLoan bookLoan = new BookLoan(
                firebaseLoan.getBookId(),
                firebaseLoan.getBookCode(),
                firebaseLoan.getBookName(),
                firebaseLoan.getStudentId(),
                firebaseLoan.getBorrowDate(),
                firebaseLoan.getReturnDate()
        );
        bookLoan.setLoanId(firebaseLoan.getLoanId());
        bookLoan.setExtensionWeeks(firebaseLoan.getExtensionWeeks());
        bookLoan.setReturned(firebaseLoan.isReturned());
        return bookLoan;
    }

    // Convert to Firebase model
    public student.inti.librarysystem.BookLoan toFirebaseModel() {
        student.inti.librarysystem.BookLoan firebaseLoan = new student.inti.librarysystem.BookLoan(
                bookId,
                bookCode,
                bookName,
                studentId,
                borrowDate,
                returnDate
        );
        firebaseLoan.setLoanId(loanId);
        firebaseLoan.setExtensionWeeks(extensionWeeks);
        firebaseLoan.setReturned(isReturned);
        return firebaseLoan;
    }
}}