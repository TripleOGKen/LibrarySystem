package student.inti.librarysystem.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;



@Entity(tableName = "exam_papers")
public class ExamPaper {
    @PrimaryKey
    @NonNull
    private String paperId = ""; // Initialize with empty string

    private String subjectCode;
    private String subjectName;
    private int year;
    private int semester;
    private String fileUrl;
    private long fileSize;
    private com.google.firebase.Timestamp uploadDate;


    // Default constructor for Room
    public ExamPaper() {
        this.paperId = ""; // Initialize in constructor as well
    }

    // Constructor for manual creation
    @Ignore
    public ExamPaper(@NonNull String paperId,
                     String subjectCode,
                     String subjectName,
                     int year,
                     int semester,
                     String fileUrl,
                     long fileSize) {
        this.paperId = paperId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.year = year;
        this.semester = semester;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    @NonNull
    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(@NonNull String paperId) {
        this.paperId = paperId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public com.google.firebase.Timestamp getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(com.google.firebase.Timestamp uploadDate) {
        this.uploadDate = uploadDate;
    }

}