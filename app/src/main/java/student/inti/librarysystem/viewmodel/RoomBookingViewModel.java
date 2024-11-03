package student.inti.librarysystem.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import student.inti.librarysystem.data.entity.RoomBooking;
import student.inti.librarysystem.repository.LibraryRepository;
import java.util.Date;
import java.util.List;

public class RoomBookingViewModel extends AndroidViewModel {
    private final LibraryRepository repository;
    private final MutableLiveData<String> bookingResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public RoomBookingViewModel(Application application) {
        super(application);
        repository = new LibraryRepository(application);
    }

    public void createBooking(RoomBooking booking) {
        isLoading.setValue(true);
        // Check for conflicting bookings
        List<RoomBooking> conflicts = repository.getConflictingBookings(
                booking.getRoomNumber(),
                booking.getStartTime(),
                booking.getEndTime()
        );

        if (conflicts.isEmpty()) {
            repository.insertBooking(booking);
            bookingResult.setValue("Room booked successfully");
        } else {
            bookingResult.setValue("Room is already booked for the selected time slot");
        }
        isLoading.setValue(false);
    }

    public LiveData<List<RoomBooking>> getStudentBookings(String studentId) {
        return repository.getStudentBookings(studentId);
    }

    public LiveData<List<RoomBooking>> getRoomBookings(int roomNumber) {
        return repository.getRoomBookings(roomNumber, new Date());
    }

    public LiveData<String> getBookingResult() {
        return bookingResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}