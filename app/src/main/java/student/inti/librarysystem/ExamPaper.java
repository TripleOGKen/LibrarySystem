package student.inti.librarysystem;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exam_papers")
public class ExamPaper {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String courseCode;
    private String courseName;
    private int year;
    private String semester;
    private String filePath;

    public ExamPaper(String courseCode, String courseName, int year,
                     String semester, String filePath) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.year = year;
        this.semester = semester;
        this.filePath = filePath;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}