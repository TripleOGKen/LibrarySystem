package student.inti.librarysystem.ui.roombooking;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import student.inti.librarysystem.RoomBooking;
import student.inti.librarysystem.repository.LibraryRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import com.google.firebase.firestore.DocumentSnapshot;


public class RoomBookingViewModel extends AndroidViewModel {
    private final LibraryRepository repository;
    private final MutableLiveData<String> bookingResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<List<RoomBooking>> bookings = new MutableLiveData<>(new ArrayList<>());
    private final FirebaseFirestore db;
    private static final String TAG = "RoomBookingViewModel";

    public RoomBookingViewModel(Application application) {
        super(application);
        repository = new LibraryRepository(application);
        db = FirebaseFirestore.getInstance();
    }

    public void createBooking(String bookingStudentId, long roomNumber,
                              Date startTime, Date endTime,
                              String participantsIds, String participantsNames) {
        isLoading.setValue(true);

        checkBookingConflicts(roomNumber, startTime, endTime, conflicts -> {
            if (conflicts) {
                bookingResult.setValue("Room is already booked for the selected time slot");
                isLoading.setValue(false);
                return;
            }

            RoomBooking firebaseBooking = new RoomBooking(
                    bookingStudentId,
                    endTime,
                    participantsIds,
                    participantsNames,
                    roomNumber,  // now passing long
                    startTime,
                    "Active"
            );

            db.collection("roomBookings")
                    .add(firebaseBooking)
                    .addOnSuccessListener(documentReference -> {
                        String bookingId = documentReference.getId();
                        firebaseBooking.setId(bookingId);

                        // Create Room entity version
                        student.inti.librarysystem.data.entity.RoomBooking roomBooking =
                                new student.inti.librarysystem.data.entity.RoomBooking(
                                        bookingStudentId,
                                        endTime,
                                        participantsIds,
                                        participantsNames,
                                        roomNumber,
                                        startTime,
                                        "Active"
                                );
                        roomBooking.setId(bookingId);
                        repository.insertBooking(roomBooking);

                        bookingResult.setValue("Room booked successfully");
                        loadUserBookings(bookingStudentId);
                        isLoading.setValue(false);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating booking", e);
                        bookingResult.setValue("Failed to create booking: " + e.getMessage());
                        isLoading.setValue(false);
                    });
        });
    }
    private void checkBookingConflicts(long roomNumber, Date startTime, Date endTime,
                                       ConflictCheckCallback callback) {
        db.collection("roomBookings")
                .whereEqualTo("roomNumber", roomNumber)
                .whereEqualTo("status", "Active")  // Only check active bookings
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean hasConflicts = false;
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        RoomBooking existingBooking = document.toObject(RoomBooking.class);
                        if (existingBooking != null) {
                            // Check if the time slots overlap
                            boolean overlaps = (startTime.before(existingBooking.getEndTime()) &&
                                    endTime.after(existingBooking.getStartTime()));
                            if (overlaps) {
                                hasConflicts = true;
                                break;
                            }
                        }
                    }
                    callback.onCheckComplete(hasConflicts);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking conflicts", e);
                    callback.onCheckComplete(true);
                });
    }

    public void loadUserBookings(String studentId) {
        isLoading.setValue(true);
        db.collection("roomBookings")
                .whereEqualTo("bookingStudentId", studentId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<RoomBooking> userBookings = querySnapshot.toObjects(RoomBooking.class);
                    bookings.setValue(userBookings);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user bookings", e);
                    bookingResult.setValue("Failed to load bookings");
                    isLoading.setValue(false);
                });
    }

    public boolean isValidBookingTime(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) return false;

        // Check if start time is in the future
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, -1); // Allow bookings for the current minute
        if (startTime.before(now.getTime())) return false;

        // Check if end time is after start time
        if (endTime.before(startTime)) return false;

        // Calculate duration in minutes
        long durationInMillis = endTime.getTime() - startTime.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis);

        // Allow bookings between 1 and 4 hours (60 to 240 minutes)
        return minutes >= 60 && minutes <= 240;
    }

    public boolean isWithinBookingHours(Date startTime) {
        if (startTime == null) return false;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);

        // Get day of week (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Check if it's a weekday (Monday to Friday)
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return false;
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // Allow bookings between 8:00 AM and 8:00 PM (20:00)
        return hour >= 8 && hour < 20;
    }

    public LiveData<List<RoomBooking>> getBookings() {
        return bookings;
    }

    public LiveData<String> getBookingResult() {
        return bookingResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    private interface ConflictCheckCallback {
        void onCheckComplete(boolean hasConflicts);
    }
}