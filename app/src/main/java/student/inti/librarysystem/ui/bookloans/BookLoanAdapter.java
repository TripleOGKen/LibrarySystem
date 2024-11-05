package student.inti.librarysystem.ui.bookloans;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import student.inti.librarysystem.BookLoan;
import student.inti.librarysystem.databinding.ItemBookLoanBinding;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookLoanAdapter extends ListAdapter<BookLoan, BookLoanAdapter.BookLoanViewHolder> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private boolean isCurrentLoans;
    private final OnLoanActionListener listener;

    public interface OnLoanActionListener {
        void onExtendClick(BookLoan loan);
    }

    private static final DiffUtil.ItemCallback<BookLoan> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<BookLoan>() {
                @Override
                public boolean areItemsTheSame(@NonNull BookLoan oldItem, @NonNull BookLoan newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull BookLoan oldItem, @NonNull BookLoan newItem) {
                    return oldItem.getReturnDate().equals(newItem.getReturnDate()) &&
                            oldItem.getExtensionWeeks() == newItem.getExtensionWeeks();
                }
            };

    public BookLoanAdapter(OnLoanActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    public void setCurrentLoans(boolean isCurrentLoans) {
        this.isCurrentLoans = isCurrentLoans;
    }

    @NonNull
    @Override
    public BookLoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookLoanBinding binding = ItemBookLoanBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BookLoanViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookLoanViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class BookLoanViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookLoanBinding binding;

        BookLoanViewHolder(ItemBookLoanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final BookLoan loan) {
            // Match the IDs from item_book_loan.xml
            binding.bookTitle.setText(loan.getBookName());
            binding.bookCode.setText(loan.getBookCode());
            binding.borrowDate.setText(dateFormat.format(loan.getBorrowDate()));
            binding.dueDate.setText(dateFormat.format(loan.getReturnDate()));

            // Set color based on book code
            binding.bookCode.setBackgroundColor(getColorForBookCode(loan.getBookCode()));
            binding.bookCode.setTextColor(Color.WHITE);

            // Configure extend button visibility and extensions text
            binding.extendButton.setVisibility(isCurrentLoans ? View.VISIBLE : View.GONE);

            if (isCurrentLoans) {
                binding.extendButton.setEnabled(loan.getExtensionWeeks() < 3);
                binding.extendButton.setOnClickListener(v -> listener.onExtendClick(loan));
            }

            // Show extensions if any
            if (loan.getExtensionWeeks() > 0) {
                binding.extensionsLeft.setVisibility(View.VISIBLE);
                binding.extensionsLeft.setText(
                        String.format(Locale.getDefault(),
                                "Extended: %d week(s)",
                                loan.getExtensionWeeks()));
            } else {
                binding.extensionsLeft.setVisibility(View.GONE);
            }
        }

        private int getColorForBookCode(String bookCode) {
            if (bookCode.startsWith("Yellow")) {
                return Color.parseColor("#FFA000"); // Yellow
            } else if (bookCode.startsWith("Green")) {
                return Color.parseColor("#4CAF50"); // Green
            } else if (bookCode.startsWith("Blue")) {
                return Color.parseColor("#2196F3"); // Blue
            }
            return Color.parseColor("#757575"); // Default gray
        }
    }
}