package student.inti.librarysystem.ui.roombooking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.FirebaseFirestore;
import student.inti.librarysystem.databinding.FragmentRoomBookingBinding;
import student.inti.librarysystem.util.FirebaseManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RoomBookingFragment extends Fragment {
    private FragmentRoomBookingBinding binding;
    private RoomBookingViewModel viewModel;
    private final Calendar selectedDateTime = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private FirebaseFirestore db;
    private String currentStudentId;
    private RoomBookingAdapter bookingAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRoomBookingBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(RoomBookingViewModel.class);
        db = FirebaseFirestore.getInstance();

        // Get current student ID
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        currentStudentId = firebaseManager.getCurrentUser().getEmail().split("@")[0].toUpperCase();

        setupRoomSpinner();
        setupDateTimePickers();
        setupBookingButton();
        setupRecyclerView();
        loadUserBookings();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        bookingAdapter = new RoomBookingAdapter(booking -> showBookingDetails(booking));
        binding.bookingsRecyclerView.setAdapter(bookingAdapter);
        binding.bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void showBookingDetails(RoomBooking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Booking Details")
                .setMessage(
                        "Room: " + booking.getRoomNumber() + "\n" +
                                "Date: " + dateFormat.format(booking.getStartTime()) + "\n" +
                                "Time: " + timeFormat.format(booking.getStartTime()) + " - " +
                                timeFormat.format(booking.getEndTime()) + "\n" +
                                "Participants: " + booking.getParticipantsNames()
                )
                .setPositiveButton("OK", null)
                .show();
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
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 7);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    binding.dateInput.setText(dateFormat.format(selectedDateTime.getTime()));
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));

        // Set min date to today
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        // Set max date to 7 days from today
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
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
            if (validateBookingInputs()) {
                createBooking();
            }
        });
    }

    private boolean validateBookingInputs() {
        if (binding.dateInput.getText().toString().isEmpty() ||
                binding.startTimeInput.getText().toString().isEmpty() ||
                binding.endTimeInput.getText().toString().isEmpty() ||
                binding.participantNamesInput.getText().toString().isEmpty() ||
                binding.participantIdsInput.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createBooking() {
        String roomNumber = binding.roomSpinner.getSelectedItem().toString();
        String participantsNames = binding.participantNamesInput.getText().toString();
        String participantsIds = binding.participantIdsInput.getText().toString();

        // Create calendar instances for start and end times
        Calendar startCal = (Calendar) selectedDateTime.clone();
        Calendar endCal = (Calendar) selectedDateTime.clone();

        // Parse times
        String[] startTimeParts = binding.startTimeInput.getText().toString().split(":");
        String[] endTimeParts = binding.endTimeInput.getText().toString().split(":");

        startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeParts[0]));
        startCal.set(Calendar.MINUTE, Integer.parseInt(startTimeParts[1]));
        endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeParts[0]));
        endCal.set(Calendar.MINUTE, Integer.parseInt(endTimeParts[1]));

        Map<String, Object> booking = new HashMap<>();
        booking.put("roomNumber", roomNumber);
        booking.put("startTime", startCal.getTime());
        booking.put("endTime", endCal.getTime());
        booking.put("bookingStudentId", currentStudentId);
        booking.put("participantsNames", participantsNames);
        booking.put("participantsIds", participantsIds);
        booking.put("status", "Active");

        db.collection("roomBookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Booking created successfully", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    loadUserBookings();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to create booking", Toast.LENGTH_SHORT).show()
                );
    }

    private void clearInputs() {
        binding.dateInput.setText("");
        binding.startTimeInput.setText("");
        binding.endTimeInput.setText("");
        binding.participantNamesInput.setText("");
        binding.participantIdsInput.setText("");
    }

    private void loadUserBookings() {
        db.collection("roomBookings")
                .whereEqualTo("bookingStudentId", currentStudentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<RoomBooking> bookings = new ArrayList<>();
                    queryDocumentSnapshots.forEach(doc -> {
                        RoomBooking booking = doc.toObject(RoomBooking.class);
                        bookings.add(booking);
                    });
                    bookingAdapter.submitList(bookings);
                    binding.noBookingsText.setVisibility(
                            bookings.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}