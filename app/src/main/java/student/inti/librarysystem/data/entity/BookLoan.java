package student.inti.librarysystem.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(
        tableName = "book_loans",
        foreignKeys = @ForeignKey(
                entity = Student.class,
                parentColumns = "studentId",
                childColumns = "studentId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("studentId")} // Add index for foreign key
)
public class BookLoan {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String studentId;
    private String bookId;
    private String bookName;
    private String bookCode;
    private Date borrowDate;
    private Date dueDate;
    private int extensionWeeks;
    private boolean isReturned;

    // Primary constructor for Room
    public BookLoan(String studentId, String bookId, Date borrowDate, Date dueDate) {
        this.studentId = studentId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.extensionWeeks = 0;
        this.isReturned = false;
    }

    // Secondary constructors marked with @Ignore
    @Ignore
    public BookLoan(String bookName, String bookCode, Date borrowDate, Date dueDate, int extensionWeeks) {
        this.bookName = bookName;
        this.bookCode = bookCode;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.extensionWeeks = extensionWeeks;
        this.isReturned = false;
    }

    @Ignore
    public BookLoan(String bookName, String bookCode, Date borrowDate, Date dueDate, int extensionWeeks, boolean isReturned) {
        this(bookName, bookCode, borrowDate, dueDate, extensionWeeks);
        this.isReturned = isReturned;
    }

    // All getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public Date getBorrowDate() { return borrowDate; }
    public void setBorrowDate(Date borrowDate) { this.borrowDate = borrowDate; }

    public Date getReturnDate() { return dueDate; }
    public void setReturnDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public int getExtensionWeeks() { return extensionWeeks; }
    public void setExtensionWeeks(int extensionWeeks) { this.extensionWeeks = extensionWeeks; }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

    public String getBookCode() { return bookCode; }
    public void setBookCode(String bookCode) { this.bookCode = bookCode; }

    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean returned) { isReturned = returned; }
}