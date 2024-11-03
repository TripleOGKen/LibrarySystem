package student.inti.librarysystem.data;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import student.inti.librarysystem.data.entity.Student;
import java.util.concurrent.Executors;

public class LibraryDatabaseCallback extends RoomDatabase.Callback {
    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);
        Log.d("DatabaseCallback", "Database creation started");

        // Insert default user directly using SQL
        String sql = "INSERT INTO students (studentId, password, name, email) " +
                "VALUES ('ST10001', 'password123', 'John Doe', 'john@example.com')";

        try {
            db.execSQL(sql);
            Log.d("DatabaseCallback", "Default user inserted successfully");
        } catch (Exception e) {
            Log.e("DatabaseCallback", "Error inserting default user: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(@NonNull SupportSQLiteDatabase db) {
        super.onOpen(db);
        // Verify if user exists
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                android.database.Cursor cursor = db.query("SELECT * FROM students WHERE studentId = 'ST10001'");
                Log.d("DatabaseCallback", "User count: " + cursor.getCount());
                cursor.close();
            } catch (Exception e) {
                Log.e("DatabaseCallback", "Error checking users: " + e.getMessage());
            }
        });
    }
}