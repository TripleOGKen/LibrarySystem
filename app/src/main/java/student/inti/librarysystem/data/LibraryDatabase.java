package student.inti.librarysystem.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import student.inti.librarysystem.data.dao.*;
import student.inti.librarysystem.data.dao.ExamPaperDao;
import student.inti.librarysystem.data.entity.*;
import student.inti.librarysystem.data.entity.ExamPaper;
import student.inti.librarysystem.util.DateConverter;
import student.inti.librarysystem.util.TimestampConverter;

@Database(entities = {
        student.inti.librarysystem.data.entity.Student.class,
        student.inti.librarysystem.data.entity.RoomBooking.class,
        student.inti.librarysystem.data.entity.BookLoan.class,
        student.inti.librarysystem.data.entity.ExamPaper.class,
        student.inti.librarysystem.data.entity.Book.class
}, version = 2,
        exportSchema = false)
@TypeConverters({
        DateConverter.class,
        TimestampConverter.class  // Combined in single annotation
})
public abstract class LibraryDatabase extends RoomDatabase {
    private static volatile LibraryDatabase INSTANCE;

    public abstract StudentDao studentDao();
    public abstract RoomBookingDao roomBookingDao();
    public abstract BookLoanDao bookLoanDao();
    public abstract ExamPaperDao examPaperDao();
    public abstract BookDao bookDao();

    public static LibraryDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LibraryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    LibraryDatabase.class,
                                    "library_database"
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(new LibraryDatabaseCallback())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static LibraryDatabase getInstance() {
        return INSTANCE;
    }
}