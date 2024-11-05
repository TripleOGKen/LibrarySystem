package student.inti.librarysystem.ui.roombooking;

import android.view.LayoutInflater;
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

public class RoomBookingAdapter extends ListAdapter<RoomBooking, RoomBookingAdapter.BookingViewHolder> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(RoomBooking booking);
    }

    public RoomBookingAdapter(OnBookingClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<RoomBooking> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RoomBooking>() {
                @Override
                public boolean areItemsTheSame(@NonNull RoomBooking oldItem, @NonNull RoomBooking newItem) {
                    // Using getId() as that's the name in your RoomBooking class
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
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ItemRoomBookingBinding binding;

        BookingViewHolder(ItemRoomBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onBookingClick(getItem(position));
                }
            });
        }

        void bind(RoomBooking booking) {
            // Using null checks to prevent NPEs
            if (booking != null) {
                binding.roomNumber.setText(booking.getRoomNumber());

                if (booking.getStartTime() != null) {
                    binding.bookingDate.setText(dateFormat.format(booking.getStartTime()));
                    binding.bookingTime.setText(String.format("%s - %s",
                            timeFormat.format(booking.getStartTime()),
                            booking.getEndTime() != null ? timeFormat.format(booking.getEndTime()) : ""));
                }

                binding.bookingStatus.setText(booking.getStatus());

                // Set status background
                if ("Active".equals(booking.getStatus())) {
                    binding.bookingStatus.setBackgroundResource(R.drawable.status_active_background);
                } else {
                    binding.bookingStatus.setBackgroundResource(R.drawable.status_pending_background);
                }
            }
        }
    }
}