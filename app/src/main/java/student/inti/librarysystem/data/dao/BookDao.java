package student.inti.librarysystem.data.dao;

import androidx.room.*;
import student.inti.librarysystem.data.entity.Book;
import java.util.List;

@Dao
public interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    @Query("SELECT * FROM books WHERE bookId = :bookId")
    Book getBook(String bookId);

    @Query("SELECT * FROM books WHERE isAvailable = 1")
    List<Book> getAvailableBooks();

    @Query("SELECT * FROM books")
    List<Book> getAllBooks();
}