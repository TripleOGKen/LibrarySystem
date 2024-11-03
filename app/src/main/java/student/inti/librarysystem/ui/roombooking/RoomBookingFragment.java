package student.inti.librarysystem.ui.roombooking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import student.inti.librarysystem.databinding.FragmentRoomBookingBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import student.inti.librarysystem.data.entity.RoomBooking;
import java.util.List;

public class RoomBookingFragment extends Fragment {
    private FragmentRoomBookingBinding binding;
    private RoomBookingViewModel viewModel;
    private final Calendar selectedDateTime = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRoomBookingBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(RoomBookingViewModel.class);

        setupRoomSpinner();
        setupDateTimePickers();
        setupBookingButton();
        observeBookings();

        return binding.getRoot();
    }

    private void setupRoomSpinner() {
        String[] rooms = {
                "Discussion Room 1",
                "Discussion Room 2",
                "Discussion Room 3",
                "Discussion Room 4",
                "Discussion Room 5"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                rooms
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.roomSpinner.setAdapter(adapter);
    }

    private void setupDateTimePickers() {
        binding.dateInput.setOnClickListener(v -> showDatePicker());
        binding.startTimeInput.setOnClickListener(v -> showTimePicker(true));
        binding.endTimeInput.setOnClickListener(v -> showTimePicker(false));
    }

    private void showDatePicker() {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            binding.dateInput.setText(dateFormat.format(selectedDateTime.getTime()));
        }, selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void showTimePicker(boolean isStartTime) {
        new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);

            if (isStartTime) {
                binding.startTimeInput.setText(timeFormat.format(cal.getTime()));
                // Set end time to 2 hours after start time
                cal.add(Calendar.HOUR_OF_DAY, 2);
                binding.endTimeInput.setText(timeFormat.format(cal.getTime()));
            } else {
                binding.endTimeInput.setText(timeFormat.format(cal.getTime()));
            }
        }, selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE), true)
                .show();
    }

    private void setupBookingButton() {
        binding.bookRoomButton.setOnClickListener(v -> {
            // Get booking details
            String roomNumber = binding.roomSpinner.getSelectedItem().toString();
            String date = binding.dateInput.getText().toString();
            String startTime = binding.startTimeInput.getText().toString();
            String participants = binding.participantNamesInput.getText().toString();

            // Create booking
            viewModel.createBooking(roomNumber, date, startTime, participants);

            // Schedule notifications
            scheduleBookingNotifications();
        });
    }

    private void scheduleBookingNotifications() {
        // Schedule 2-hour reminder
        Data notificationData = new Data.Builder()
                .putString("title", "Booking Session Reminder")
                .putString("message", "2 hours remaining. Please return the key to front desk once time is up.")
                .build();

        OneTimeWorkRequest twoHourReminder = new OneTimeWorkRequest.Builder(BookingNotificationWorker.class)
                .setInitialDelay(1, TimeUnit.HOURS) // Set to 1 hour for testing, change to 2 for production
                .setInputData(notificationData)
                .build();

        // Schedule end of session notification
        Data endSessionData = new Data.Builder()
                .putString("title", "Booking Session Ended")
                .putString("message", "Booking Session: 0 hours left remaining")
                .build();

        OneTimeWorkRequest endSessionNotification = new OneTimeWorkRequest.Builder(BookingNotificationWorker.class)
                .setInitialDelay(2, TimeUnit.HOURS)
                .setInputData(endSessionData)
                .build();

        WorkManager.getInstance(requireContext())
                .beginWith(twoHourReminder)
                .then(endSessionNotification)
                .enqueue();
    }

    private void observeBookings() {
        viewModel.getBookings().observe(getViewLifecycleOwner(), bookings -> {
            // Update booking history display
            // You'll need to implement a RecyclerView adapter for this
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}