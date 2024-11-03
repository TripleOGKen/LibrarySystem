package student.inti.librarysystem.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import student.inti.librarysystem.data.entity.Student;
import java.util.List;
import androidx.room.Dao;
import androidx.room.Query;


@Dao
public interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Student student);

    @Update
    void update(Student student);

    @Delete
    void delete(Student student);

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    LiveData<Student> getStudent(String studentId);

    @Query("SELECT * FROM students WHERE studentId = :studentId AND password = :password LIMIT 1")
    Student login(String studentId, String password);

    @Query("SELECT * FROM students")
    LiveData<List<Student>> getAllStudents();

    @Query("UPDATE students SET password = :newPassword WHERE studentId = :studentId")
    void updatePassword(String studentId, String newPassword);

    @Query("UPDATE students SET profilePicturePath = :picturePath WHERE studentId = :studentId")
    void updateProfilePicture(String studentId, String picturePath);

}