package student.inti.librarysystem.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "book_loans",
        foreignKeys = @ForeignKey(entity = Student.class,
                parentColumns = "studentId",
                childColumns = "studentId",
                onDelete = ForeignKey.CASCADE))
public class BookLoan {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String studentId;
    private String bookId;
    private Date borrowDate;
    private Date dueDate;
    private int extensionWeeks; // Track how many weeks the loan has been extended

    // Constructor
    public BookLoan(String studentId, String bookId, Date borrowDate, Date dueDate) {
        this.studentId = studentId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.extensionWeeks = 0;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getExtensionWeeks() {
        return extensionWeeks;
    }

    public void setExtensionWeeks(int extensionWeeks) {
        this.extensionWeeks = extensionWeeks;
    }
}