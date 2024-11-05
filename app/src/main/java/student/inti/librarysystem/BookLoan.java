package student.inti.librarysystem;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;
import java.util.Date;
import java.util.Objects;

public class BookLoan {
    @DocumentId
    private String id;

    @PropertyName("bookCode")
    private String bookCode;

    @PropertyName("bookId")
    private String bookId;

    @PropertyName("bookName")
    private String bookName;

    @PropertyName("borrowDate")
    private Date borrowDate;

    @PropertyName("extensionWeeks")
    private int extensionWeeks;

    @PropertyName("isReturned")
    private boolean isReturned;

    @PropertyName("returnDate")
    private Date returnDate;

    @PropertyName("studentId")
    private String studentId;

    // Required empty constructor for Firestore
    public BookLoan() {}

    // Constructor
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

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("bookCode")
    public String getBookCode() {
        return bookCode;
    }

    @PropertyName("bookCode")
    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    @PropertyName("bookId")
    public String getBookId() {
        return bookId;
    }

    @PropertyName("bookId")
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @PropertyName("bookName")
    public String getBookName() {
        return bookName;
    }

    @PropertyName("bookName")
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @PropertyName("borrowDate")
    public Date getBorrowDate() {
        return borrowDate;
    }

    @PropertyName("borrowDate")
    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    @PropertyName("extensionWeeks")
    public int getExtensionWeeks() {
        return extensionWeeks;
    }

    @PropertyName("extensionWeeks")
    public void setExtensionWeeks(int extensionWeeks) {
        this.extensionWeeks = extensionWeeks;
    }

    @PropertyName("isReturned")
    public boolean isReturned() {
        return isReturned;
    }

    @PropertyName("isReturned")
    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    @PropertyName("returnDate")
    public Date getReturnDate() {
        return returnDate;
    }

    @PropertyName("returnDate")
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    @PropertyName("studentId")
    public String getStudentId() {
        return studentId;
    }

    @PropertyName("studentId")
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookLoan bookLoan = (BookLoan) o;
        return extensionWeeks == bookLoan.extensionWeeks &&
                isReturned == bookLoan.isReturned &&
                Objects.equals(id, bookLoan.id) &&
                Objects.equals(bookCode, bookLoan.bookCode) &&
                Objects.equals(bookId, bookLoan.bookId) &&
                Objects.equals(bookName, bookLoan.bookName) &&
                Objects.equals(borrowDate, bookLoan.borrowDate) &&
                Objects.equals(returnDate, bookLoan.returnDate) &&
                Objects.equals(studentId, bookLoan.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookCode, bookId, bookName, borrowDate,
                extensionWeeks, isReturned, returnDate, studentId);
    }
}