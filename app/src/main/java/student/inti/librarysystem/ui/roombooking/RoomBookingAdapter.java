package student.inti.librarysystem.ui.roombooking;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import student.inti.librarysystem.data.entity.RoomBooking;
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
                    return oldItem.getBookingId().equals(newItem.getBookingId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull RoomBooking oldItem, @NonNull RoomBooking newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRoomBookingBinding binding = ItemRoomBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BookingViewHolder(binding);
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
            binding.roomNumberText.setText(booking.getRoomNumber());
            binding.dateText.setText(dateFormat.format(booking.getStartTime()));
            binding.timeText.setText(String.format("%s - %s",
                    timeFormat.format(booking.getStartTime()),
                    timeFormat.format(booking.getEndTime())));
            binding.statusText.setText(booking.getStatus());
        }
    }
}