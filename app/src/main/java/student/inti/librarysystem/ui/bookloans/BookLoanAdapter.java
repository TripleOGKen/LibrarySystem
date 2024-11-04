package student.inti.librarysystem.ui.bookloans;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import student.inti.librarysystem.data.entity.BookLoan;
import student.inti.librarysystem.databinding.ItemBookLoanBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookLoanAdapter extends RecyclerView.Adapter<BookLoanAdapter.BookLoanViewHolder> {
    private List<BookLoan> loans = new ArrayList<>();
    private boolean isCurrentLoans;
    private final OnLoanActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface OnLoanActionListener {
        void onExtendClick(BookLoan loan);
    }

    public BookLoanAdapter(OnLoanActionListener listener) {
        this.listener = listener;
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
        BookLoan loan = loans.get(position);
        holder.bind(loan, isCurrentLoans);
    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

    public void updateLoans(List<BookLoan> newLoans, boolean isCurrentLoans) {
        this.loans = newLoans != null ? newLoans : new ArrayList<>();
        this.isCurrentLoans = isCurrentLoans;
        notifyDataSetChanged();
    }

    class BookLoanViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookLoanBinding binding;

        BookLoanViewHolder(ItemBookLoanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BookLoan loan, boolean isCurrentLoan) {
            binding.bookNameText.setText(loan.getBookName());
            binding.bookCodeText.setText(loan.getBookCode());
            binding.borrowDateText.setText("Borrowed: " + dateFormat.format(loan.getBorrowDate()));
            binding.returnDateText.setText("Return by: " + dateFormat.format(loan.getReturnDate()));

            // Set color based on book code
            String colorCode = getColorForBookCode(loan.getBookCode());
            binding.bookCodeText.setBackgroundColor(Color.parseColor(colorCode));
            binding.bookCodeText.setTextColor(Color.WHITE);

            // Show/hide extend button based on whether it's a current loan
            binding.extendButton.setVisibility(isCurrentLoan ? View.VISIBLE : View.GONE);

            if (isCurrentLoan) {
                binding.extendButton.setOnClickListener(v -> listener.onExtendClick(loan));
                // Disable extend button if maximum extensions reached
                binding.extendButton.setEnabled(loan.getExtensionWeeks() < 3);

                // Show extension count if any
                if (loan.getExtensionWeeks() > 0) {
                    binding.extensionText.setVisibility(View.VISIBLE);
                    binding.extensionText.setText("Extended: " + loan.getExtensionWeeks() + " week(s)");
                } else {
                    binding.extensionText.setVisibility(View.GONE);
                }
            } else {
                binding.extensionText.setVisibility(loan.getExtensionWeeks() > 0 ? View.VISIBLE : View.GONE);
                if (loan.getExtensionWeeks() > 0) {
                    binding.extensionText.setText("Extended: " + loan.getExtensionWeeks() + " week(s)");
                }
            }
        }

        private String getColorForBookCode(String bookCode) {
            if (bookCode.startsWith("Yellow")) {
                return "#FFA000"; // Dark Yellow
            } else if (bookCode.startsWith("Green")) {
                return "#4CAF50"; // Green
            } else if (bookCode.startsWith("Blue")) {
                return "#2196F3"; // Blue
            }
            return "#757575"; // Grey (default)
        }
    }
}