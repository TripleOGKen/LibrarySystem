package student.inti.librarysystem.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import student.inti.librarysystem.data.entity.Student;

@Dao
public interface StudentDao {
    @Query("SELECT * FROM students WHERE studentId = :studentId AND hashedPassword = :hashedPassword")
    Student login(String studentId, String hashedPassword);

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    LiveData<Student> getStudent(String studentId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Student student);

    @Update
    void update(Student student);

    @Query("UPDATE students SET hashedPassword = :newHashedPassword WHERE studentId = :studentId AND hashedPassword = :oldHashedPassword")
    void updatePassword(String studentId, String oldHashedPassword, String newHashedPassword);

    @Query("UPDATE students SET profileImageUrl = :picturePath WHERE studentId = :studentId")
    void updateProfilePicture(String studentId, String picturePath);
}