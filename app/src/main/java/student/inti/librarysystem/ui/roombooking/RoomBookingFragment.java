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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.FirebaseFirestore;
import student.inti.librarysystem.R;
import student.inti.librarysystem.RoomBooking;
import student.inti.librarysystem.databinding.FragmentRoomBookingBinding;
import student.inti.librarysystem.util.FirebaseManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;
import java.util.Objects;

public class RoomBookingFragment extends Fragment implements RoomBookingAdapter.OnBookingClickListener {
    private FragmentRoomBookingBinding binding;
    private RoomBookingAdapter adapter;
    private final Calendar selectedDateTime = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private FirebaseFirestore db;
    private String currentStudentId;
    private RoomBookingViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRoomBookingBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(RoomBookingViewModel.class);

        // Safely get current student ID
        String email = Objects.requireNonNull(FirebaseManager.getInstance().getCurrentUser()).getEmail();
        currentStudentId = email != null ? email.split("@")[0].toUpperCase() : "";

        setupRoomSpinner();
        setupDateTimePickers();
        setupRecyclerView();
        setupBookingButton();
        loadUserBookings();
        observeViewModel();

        setupClearHistoryButton();
        return binding.getRoot();
    }

    private void observeViewModel() {
        viewModel.getBookings().observe(getViewLifecycleOwner(), roomBookings -> {
            if (roomBookings != null) {
                adapter.submitList(roomBookings);
            }
        });

        viewModel.getBookingResult().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.bookRoomButton.setEnabled(!isLoading);
        });
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
        maxDate.add(Calendar.DAY_OF_MONTH, 7); // Allow bookings up to 7 days in advance

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);

                    // Check if selected date is weekend
                    int dayOfWeek = selected.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                        Toast.makeText(getContext(),
                                "Invalid date/time. Outside of operation hours",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    binding.dateInput.setText(dateFormat.format(selectedDateTime.getTime()));

                    // Clear time inputs when date changes
                    binding.startTimeInput.setText("");
                    binding.endTimeInput.setText("");
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
                    // Check if time is within operation hours (10 AM - 6 PM)
                    if (hourOfDay < 10 || hourOfDay >= 18) {
                        Toast.makeText(getContext(),
                                "Invalid date/time. Outside of operation hours (10 AM - 6 PM)",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, selectedDateTime.get(Calendar.YEAR));
                    cal.set(Calendar.MONTH, selectedDateTime.get(Calendar.MONTH));
                    cal.set(Calendar.DAY_OF_MONTH, selectedDateTime.get(Calendar.DAY_OF_MONTH));
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal.set(Calendar.MINUTE, minute);

                    // Check if selected time is in the past
                    if (cal.before(Calendar.getInstance())) {
                        Toast.makeText(getContext(), "Cannot select past time", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isStartTime) {
                        binding.startTimeInput.setText(timeFormat.format(cal.getTime()));
                        // Set end time to 2 hours after start time
                        cal.add(Calendar.HOUR_OF_DAY, 2);
                        if (cal.get(Calendar.HOUR_OF_DAY) > 18) {
                            cal.set(Calendar.HOUR_OF_DAY, 18);
                            cal.set(Calendar.MINUTE, 0);
                        }
                        binding.endTimeInput.setText(timeFormat.format(cal.getTime()));
                    } else {
                        // Validate end time is after start time
                        String startTimeStr = binding.startTimeInput.getText().toString();
                        if (!startTimeStr.isEmpty()) {
                            Date startTime = parseTime(startTimeStr);
                            if (startTime != null && cal.getTime().before(startTime)) {
                                Toast.makeText(getContext(),
                                        "End time must be after start time",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        binding.endTimeInput.setText(timeFormat.format(cal.getTime()));
                    }
                }, selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true);

        timePickerDialog.show();
    }

    private void setupClearHistoryButton() {
        binding.clearHistoryButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Clear Booking History")
                    .setMessage("Are you sure you want to clear your booking history? This will remove all cancelled and completed bookings. Active bookings will not be affected.")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        viewModel.clearBookingHistory(currentStudentId);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private Date parseTime(String timeStr) {
        try {
            return timeFormat.parse(timeStr);
        } catch (Exception e) {
            return null;
        }
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
        String roomNumberStr = binding.roomSpinner.getSelectedItem().toString();
        long roomNumber = Long.parseLong(roomNumberStr.replaceAll("\\D+", ""));
        String participantsNames = binding.participantNamesInput.getText().toString();
        String participantsIds = binding.participantIdsInput.getText().toString();

        Calendar startCal = (Calendar) selectedDateTime.clone();
        Calendar endCal = (Calendar) selectedDateTime.clone();

        String[] startTimeParts = binding.startTimeInput.getText().toString().split(":");
        String[] endTimeParts = binding.endTimeInput.getText().toString().split(":");

        startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeParts[0]));
        startCal.set(Calendar.MINUTE, Integer.parseInt(startTimeParts[1]));

        endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeParts[0]));
        endCal.set(Calendar.MINUTE, Integer.parseInt(endTimeParts[1]));

        viewModel.createBooking(
                currentStudentId,
                roomNumber,
                startCal.getTime(),
                endCal.getTime(),
                participantsIds,
                participantsNames
        );

        // Clear inputs after booking
        clearInputs();
    }

    private void clearInputs() {
        binding.dateInput.setText("");
        binding.startTimeInput.setText("");
        binding.endTimeInput.setText("");
        binding.participantNamesInput.setText("");
        binding.participantIdsInput.setText("");
    }

    private void loadUserBookings() {
        viewModel.loadUserBookings(currentStudentId);
    }

    @Override
    public void onBookingClick(RoomBooking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Booking Details")
                .setMessage(String.format(Locale.getDefault(),
                        "Room: Discussion Room %d\n" +
                                "Date: %s\n" +
                                "Time: %s - %s\n" +
                                "Participants:\n%s\n" +
                                "Status: %s",
                        booking.getRoomNumber(),
                        dateFormat.format(booking.getStartTime()),
                        timeFormat.format(booking.getStartTime()),
                        timeFormat.format(booking.getEndTime()),
                        formatParticipantDetails(booking.getParticipantsNames(), booking.getParticipantsIds()),
                        booking.getStatus()))
                .setPositiveButton("OK", null)
                .show();
    }

    private String formatParticipantDetails(String names, String ids) {
        if (names == null || ids == null) return "";

        String[] nameList = names.split(",");
        String[] idList = ids.split(",");

        StringBuilder details = new StringBuilder();
        int length = Math.min(nameList.length, idList.length);

        for (int i = 0; i < length; i++) {
            String name = nameList[i].trim();
            String id = idList[i].trim();

            if (i > 0) details.append("\n");
            details.append(String.format("%s (%s)", name, id));
        }

        return details.toString();
    }

    @Override
    public void onCancelBooking(RoomBooking booking) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    viewModel.cancelBooking(booking);
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}