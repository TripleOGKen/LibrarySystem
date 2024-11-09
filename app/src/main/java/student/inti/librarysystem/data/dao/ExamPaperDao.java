package student.inti.librarysystem.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import student.inti.librarysystem.data.entity.ExamPaper;
import java.util.List;

@Dao
public interface ExamPaperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ExamPaper examPaper);

    @Update
    void update(ExamPaper examPaper);

    @Delete
    void delete(ExamPaper examPaper);

    @Query("SELECT * FROM exam_papers WHERE paperId = :paperId")
    LiveData<ExamPaper> getExamPaper(String paperId);

    @Query("SELECT * FROM exam_papers ORDER BY year DESC, semester DESC")
    LiveData<List<ExamPaper>> getAllExamPapers();

    @Query("SELECT * FROM exam_papers WHERE subjectCode = :subjectCode ORDER BY year DESC, semester DESC")
    LiveData<List<ExamPaper>> getExamPapersByCourse(String subjectCode);

    @Query("SELECT * FROM exam_papers WHERE year = :year ORDER BY semester DESC")
    LiveData<List<ExamPaper>> getExamPapersByYear(int year);

    @Query("SELECT DISTINCT year FROM exam_papers ORDER BY year DESC")
    LiveData<List<Integer>> getAllYears();
}