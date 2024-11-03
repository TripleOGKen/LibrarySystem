package student.inti.librarysystem.repository;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import student.inti.librarysystem.data.dao.BookLoanDao;
import student.inti.librarysystem.data.dao.ExamPaperDao;
import student.inti.librarysystem.data.dao.RoomBookingDao;
import student.inti.librarysystem.data.dao.StudentDao;
import student.inti.librarysystem.data.entity.BookLoan;
import student.inti.librarysystem.data.entity.ExamPaper;
import student.inti.librarysystem.data.entity.RoomBooking;
import student.inti.librarysystem.data.entity.Student;
import student.inti.librarysystem.util.DateConverter;

@Database(entities = {
        Student.class,
        RoomBooking.class,
        BookLoan.class,
        ExamPaper.class
}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class LibraryDatabase extends RoomDatabase {
    public abstract StudentDao studentDao();
    public abstract RoomBookingDao roomBookingDao();
    public abstract BookLoanDao bookLoanDao();
    public abstract ExamPaperDao examPaperDao();

    private static volatile LibraryDatabase INSTANCE;

    public static LibraryDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LibraryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            LibraryDatabase.class,
                            "library_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}