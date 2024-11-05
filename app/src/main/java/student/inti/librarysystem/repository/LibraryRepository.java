package student.inti.librarysystem.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import student.inti.librarysystem.repository.LibraryDatabase;
import student.inti.librarysystem.data.dao.*;
import student.inti.librarysystem.data.entity.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LibraryRepository {
    private final StudentDao studentDao;
    private final RoomBookingDao roomBookingDao;
    private final BookLoanDao bookLoanDao;
    private final ExamPaperDao examPaperDao;
    private final ExecutorService executorService;

    public LibraryRepository(Application application) {
        LibraryDatabase db = LibraryDatabase.getDatabase(application);
        studentDao = db.studentDao();
        roomBookingDao = db.roomBookingDao();
        bookLoanDao = db.bookLoanDao();
        examPaperDao = db.examPaperDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    // Student operations
    public Student loginStudent(String studentId, String hashedPassword) {
        return studentDao.login(studentId, hashedPassword);
    }

    public void insertStudent(Student student) {
        executorService.execute(() -> studentDao.insert(student));
    }

    public LiveData<Student> getStudent(String studentId) {
        return studentDao.getStudent(studentId);
    }

    public void updateStudent(Student student) {
        executorService.execute(() -> studentDao.update(student));
    }

    public void updatePassword(String studentId, String oldPassword, String newPassword) {
        executorService.execute(() -> studentDao.updatePassword(studentId, oldPassword, newPassword));
    }

    public void updateProfilePicture(String studentId, String picturePath) {
        executorService.execute(() -> studentDao.updateProfilePicture(studentId, picturePath));
    }

    // Room Booking operations
    public void insertBooking(RoomBooking booking) {
        executorService.execute(() -> roomBookingDao.insert(booking));
    }

    public LiveData<List<RoomBooking>> getStudentBookings(String studentId) {
        return roomBookingDao.getStudentBookings(studentId);
    }

    public LiveData<List<RoomBooking>> getRoomBookings(String roomNumber, Date today) {
        return roomBookingDao.getRoomBookings(roomNumber, today);
    }

    public List<RoomBooking> getConflictingBookings(String roomNumber, Date startTime, Date endTime) {
        return roomBookingDao.getConflictingBookings(roomNumber, startTime, endTime);
    }

    // Book Loan operations
    public void insertLoan(BookLoan loan) {
        executorService.execute(() -> bookLoanDao.insert(loan));
    }

    public LiveData<List<BookLoan>> getStudentLoans(String studentId) {
        return bookLoanDao.getStudentLoans(studentId);
    }

    public void extendLoan(String loanId, Date newDueDate, int extensionWeeks) {
        executorService.execute(() -> bookLoanDao.extendLoan(loanId, newDueDate, extensionWeeks));
    }

    public LiveData<List<BookLoan>> getOverdueLoans(String studentId, Date currentDate) {
        return bookLoanDao.getOverdueLoans(studentId, currentDate);
    }

    // Exam Paper operations
    public LiveData<List<ExamPaper>> getAllExamPapers() {
        return examPaperDao.getAllExamPapers();
    }

    public LiveData<List<ExamPaper>> getExamPapersByYear(int year) {
        return examPaperDao.getExamPapersByYear(year);
    }

    public LiveData<List<Integer>> getAllYears() {
        return examPaperDao.getAllYears();
    }

    public void insertExamPaper(ExamPaper examPaper) {
        executorService.execute(() -> examPaperDao.insert(examPaper));
    }

    public void cleanUp() {
        executorService.shutdown();
    }
}