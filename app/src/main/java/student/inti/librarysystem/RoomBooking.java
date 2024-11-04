package student.inti.librarysystem;

import com.google.firebase.firestore.DocumentId;
import java.util.Date;
import java.util.Objects;

public class BookLoan {
    @DocumentId
    private String loanId;
    private String bookCode;
    private String bookId;
    private String bookName;
    private Date borrowDate;
    private Date returnDate;
    private String studentId;
    private int extensionWeeks;
    private boolean isReturned;

    // Required empty constructor for Firestore
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookLoan bookLoan = (BookLoan) o;
        return extensionWeeks == bookLoan.extensionWeeks &&
                isReturned == bookLoan.isReturned &&
                Objects.equals(loanId, bookLoan.loanId) &&
                Objects.equals(bookCode, bookLoan.bookCode) &&
                Objects.equals(bookId, bookLoan.bookId) &&
                Objects.equals(bookName, bookLoan.bookName) &&
                Objects.equals(studentId, bookLoan.studentId) &&
                Objects.equals(borrowDate, bookLoan.borrowDate) &&
                Objects.equals(returnDate, bookLoan.returnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loanId, bookCode, bookId, bookName, studentId,
                borrowDate, returnDate, extensionWeeks, isReturned);
    }

    @Override
    public String toString() {
        return "BookLoan{" +
                "loanId='" + loanId + '\'' +
                ", bookCode='" + bookCode + '\'' +
                ", bookId='" + bookId + '\'' +
                ", bookName='" + bookName + '\'' +
                ", studentId='" + studentId + '\'' +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                ", extensionWeeks=" + extensionWeeks +
                ", isReturned=" + isReturned +
                '}';
    }
}