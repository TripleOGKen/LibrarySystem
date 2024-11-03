package student.inti.librarysystem.ui.roombooking;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;
import student.inti.librarysystem.data.LibraryDatabase;
import student.inti.librarysystem.data.entity.RoomBooking;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoomBookingViewModel extends AndroidViewModel {
    private static final String TAG = "RoomBookingViewModel";
    private final LibraryDatabase database;
    private final ExecutorService executorService;
    private final MutableLiveData<List<RoomBooking>> bookings = new MutableLiveData<>(new ArrayList<>());
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public RoomBookingViewModel(Application application) {
        super(application);
        database = LibraryDatabase.getDatabase(application);
        executorService = Executors.newSingleThreadExecutor();
        loadBookings();
    }

    private void loadBookings() {
        executorService.execute(() -> {
            try {
                List<RoomBooking> currentBookings = database.roomBookingDao().getAllBookings();
                bookings.postValue(currentBookings);
            } catch (Exception e) {
                Log.e(TAG, "Error loading bookings: " + e.getMessage(), e);
                errorMessage.postValue("Failed to load bookings");
            }
        });
    }

    public void createBooking(String roomNumber, String date, String startTime, String participants) {
        if (date == null || startTime == null) {
            Log.e(TAG, "Date or time is null");
            errorMessage.postValue("Please select date and time");
            return;
        }

        executorService.execute(() -> {
            try {
                // Parse room number to get just the number
                int roomNum = Integer.parseInt(roomNumber.replaceAll("\\D+", ""));

                // Combine date and time
                Date startDateTime = dateTimeFormat.parse(date + " " + startTime);
                if (startDateTime == null) {
                    throw new ParseException("Failed to parse date/time", 0);
                }

                // Create end time (2 hours after start)
                Date endDateTime = new Date(startDateTime.getTime() + (2 * 60 * 60 * 1000));

                RoomBooking booking = new RoomBooking(
                        "ST10001", // Replace with actual logged-in user ID
                        roomNum,
                        startDateTime,
                        endDateTime,
                        participants,
                        "" // Participant IDs can be added later
                );

                database.roomBookingDao().insert(booking);
                loadBookings(); // Reload the bookings list
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date/time: " + e.getMessage(), e);
                errorMessage.postValue("Invalid date or time format");
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing room number: " + e.getMessage(), e);
                errorMessage.postValue("Invalid room number format");
            } catch (Exception e) {
                Log.e(TAG, "Error creating booking: " + e.getMessage(), e);
                errorMessage.postValue("Failed to create booking");
            }
        });
    }

    public LiveData<List<RoomBooking>> getBookings() {
        return bookings;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}