package student.inti.librarysystem.ui.roombooking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.FirebaseFirestore;
import student.inti.librarysystem.R;
import student.inti.librarysystem.RoomBooking;
import student.inti.librarysystem.databinding.FragmentRoomBookingBinding;
import student.inti.librarysystem.util.FirebaseManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RoomBookingFragment extends Fragment implements RoomBookingAdapter.OnBookingClickListener {
    private FragmentRoomBookingBinding binding;
    private RoomBookingAdapter adapter;
    private final Calendar selectedDateTime = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private FirebaseFirestore db;
    private String currentStudentId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRoomBookingBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();

        // Safely get current student ID
        String email = Objects.requireNonNull(
                FirebaseManager.getInstance().getCurrentUser()).getEmail();
        currentStudentId = email != null ? email.split("@")[0].toUpperCase() : "";

        setupRoomSpinner();
        setupDateTimePickers();
        setupRecyclerView();
        setupBookingButton();
        loadUserBookings();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new RoomBookingAdapter(this);
        binding.bookingsRecyclerView.setAdapter(adapter);
        binding.bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                }, selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePicker(boolean isStartTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, selectedDateTime.get(Calendar.YEAR));
                    cal.set(Calendar.MONTH, selectedDateTime.get(Calendar.MONTH));
                    cal.set(Calendar.DAY_OF_MONTH, selectedDateTime.get(Calendar.DAY_OF_MONTH));
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
                selectedDateTime.get(Calendar.MINUTE),
                true);

        timePickerDialog.show();
    }

    private void setupBookingButton() {
        binding.bookRoomButton.setOnClickListener(v -> {
            if (validateBookingInputs()) {
                createBooking();
            }
        });
    }

    private boolean validateBookingInputs() {
        String date = binding.dateInput.getText().toString();
        String startTime = binding.startTimeInput.getText().toString();
        String endTime = binding.endTimeInput.getText().toString();
        String participantNames = binding.participantNamesInput.getText().toString();
        String participantIds = binding.participantIdsInput.getText().toString();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(startTime) ||
                TextUtils.isEmpty(endTime) || TextUtils.isEmpty(participantNames) ||
                TextUtils.isEmpty(participantIds)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createBooking() {
        String roomNumber = binding.roomSpinner.getSelectedItem().toString();
        String participantsNames = binding.participantNamesInput.getText().toString();
        String participantsIds = binding.participantIdsInput.getText().toString();

        // Parse date and times
        Calendar startCal = (Calendar) selectedDateTime.clone();
        Calendar endCal = (Calendar) selectedDateTime.clone();

        String[] startTimeParts = binding.startTimeInput.getText().toString().split(":");
        String[] endTimeParts = binding.endTimeInput.getText().toString().split(":");

        startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeParts[0]));
        startCal.set(Calendar.MINUTE, Integer.parseInt(startTimeParts[1]));

        endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeParts[0]));
        endCal.set(Calendar.MINUTE, Integer.parseInt(endTimeParts[1]));

        // Create booking data matching Firebase fields exactly
        Map<String, Object> booking = new HashMap<>();
        booking.put("bookingStudentId", currentStudentId);
        booking.put("endTime", endCal.getTime());
        booking.put("participantsIds", participantsIds);
        booking.put("participantsNames", participantsNames);
        booking.put("roomNumber", roomNumber);
        booking.put("startTime", startCal.getTime());
        booking.put("status", "Active");

        // Save to Firebase
        db.collection("roomBookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Booking created successfully", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    loadUserBookings();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to create booking", Toast.LENGTH_SHORT).show();
                });
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
                .addOnSuccessListener(querySnapshot -> {
                    adapter.submitList(querySnapshot.toObjects(RoomBooking.class));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load bookings", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBookingClick(RoomBooking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Booking Details")
                .setMessage(String.format(Locale.getDefault(),
                        "Room: %s\nDate: %s\nTime: %s - %s\nParticipants: %s\nStatus: %s",
                        booking.getRoomNumber(),
                        dateFormat.format(booking.getStartTime()),
                        timeFormat.format(booking.getStartTime()),
                        timeFormat.format(booking.getEndTime()),
                        booking.getParticipantsNames(),
                        booking.getStatus()))
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}