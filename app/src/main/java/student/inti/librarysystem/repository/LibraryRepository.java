package student.inti.librarysystem.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import student.inti.librarysystem.data.LibraryDatabase;
import student.inti.librarysystem.data.dao.StudentDao;
import student.inti.librarysystem.data.dao.RoomBookingDao;
import student.inti.librarysystem.data.dao.BookLoanDao;
import student.inti.librarysystem.data.dao.ExamPaperDao;
import student.inti.librarysystem.data.LibraryDatabase;
import student.inti.librarysystem.data.dao.*;
import student.inti.librarysystem.data.entity.*;
import student.inti.librarysystem.util.FirebaseManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;



public class LibraryRepository {
    private final StudentDao studentDao;
    private final RoomBookingDao roomBookingDao;
    private final BookLoanDao bookLoanDao;
    private final ExamPaperDao examPaperDao;
    private final ExecutorService executorService;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;

    public LibraryRepository(Application application) {
        // Get reference to the database from data package
        LibraryDatabase db = LibraryDatabase.getDatabase(application);
        // Initialize executor service
        executorService = Executors.newFixedThreadPool(4);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize DAOs
        studentDao = db.studentDao();
        roomBookingDao = db.roomBookingDao();
        bookLoanDao = db.bookLoanDao();
        examPaperDao = db.examPaperDao();


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

    public Task<Void> updateFirebasePassword(String studentId, String oldPassword, String newPassword) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            return Tasks.forException(new Exception("User not logged in"));
        }

        String email = studentId.toLowerCase() + "@student.newinti.edu.my";
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

        return user.reauthenticate(credential)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return user.updatePassword(newPassword)
                                .continueWithTask(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        // Update Firestore
                                        return firestore.collection("students")
                                                .document(studentId.toUpperCase())
                                                .update("hashedPassword", FirebaseManager.hashPassword(newPassword,
                                                        // You'll need to get the salt from the document first
                                                        // This is a simplified version
                                                        FirebaseManager.generateSalt()));
                                    }
                                    throw updateTask.getException();
                                });
                    }
                    throw task.getException();
                });
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

    public LiveData<List<RoomBooking>> getRoomBookings(int roomNumber, Date today) {
        return roomBookingDao.getRoomBookings(roomNumber, today);
    }

    public List<RoomBooking> getConflictingBookings(int roomNumber, Date startTime, Date endTime) {
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