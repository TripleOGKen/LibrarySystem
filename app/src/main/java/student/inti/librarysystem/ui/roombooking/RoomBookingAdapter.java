package student.inti.librarysystem.ui.roombooking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import student.inti.librarysystem.R;
import student.inti.librarysystem.RoomBooking;
import student.inti.librarysystem.databinding.ItemRoomBookingBinding;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;


public class RoomBookingAdapter extends ListAdapter<RoomBooking, RoomBookingAdapter.BookingViewHolder> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(RoomBooking booking);
        void onCancelBooking(RoomBooking booking);  // New method for cancel action
    }

    public RoomBookingAdapter(OnBookingClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<RoomBooking> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull RoomBooking oldItem, @NonNull RoomBooking newItem) {
                    return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull RoomBooking oldItem, @NonNull RoomBooking newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookingViewHolder(
                ItemRoomBookingBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                ),
                listener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ItemRoomBookingBinding binding;
        private final OnBookingClickListener listener;

        BookingViewHolder(ItemRoomBookingBinding binding, OnBookingClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    RoomBooking booking = getItem(position);
                    listener.onBookingClick(booking);
                }
            });

            // Set up cancel button click listener
            binding.cancelBookingButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    RoomBooking booking = getItem(position);
                    listener.onCancelBooking(booking);
                }
            });
        }

        void bind(RoomBooking booking) {
            if (booking != null) {
                binding.roomNumber.setText(String.format(Locale.getDefault(),
                        "Discussion Room %d", booking.getRoomNumber()));

                if (booking.getStartTime() != null) {
                    binding.bookingDate.setText(dateFormat.format(booking.getStartTime()));
                    binding.bookingTime.setText(String.format(Locale.getDefault(),
                            "%s - %s",
                            timeFormat.format(booking.getStartTime()),
                            booking.getEndTime() != null ?
                                    timeFormat.format(booking.getEndTime()) : ""));
                }

                // Format and set participant details
                String participantDetails = formatParticipantDetails(
                        booking.getParticipantsNames(),
                        booking.getParticipantsIds()
                );
                binding.participantDetails.setText(participantDetails);

                binding.bookingStatus.setText(booking.getStatus());

                if ("Active".equals(booking.getStatus())) {
                    binding.bookingStatus.setBackgroundResource(R.drawable.status_active_background);
                    binding.cancelBookingButton.setVisibility(View.VISIBLE);
                } else {
                    binding.bookingStatus.setBackgroundResource(R.drawable.status_pending_background);
                    binding.cancelBookingButton.setVisibility(View.GONE);
                }
            }
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
    }
}