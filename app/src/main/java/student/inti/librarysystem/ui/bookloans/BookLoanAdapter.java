package student.inti.librarysystem.ui.bookloans;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import student.inti.librarysystem.data.entity.BookLoan;
import student.inti.librarysystem.databinding.ItemBookLoanBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookLoanAdapter extends RecyclerView.Adapter<BookLoanAdapter.BookLoanViewHolder> {
    private List<BookLoan> loans = new ArrayList<>();
    private final OnLoanActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private boolean isCurrentLoans = true;

    public interface OnLoanActionListener {
        void onExtendClick(BookLoan loan);
    }

    public BookLoanAdapter(OnLoanActionListener listener) {
        this.listener = listener;
    }

    public void updateLoans(List<BookLoan> newLoans, boolean isCurrentLoans) {
        final List<BookLoan> oldList = new ArrayList<>(this.loans);
        this.loans = newLoans != null ? new ArrayList<>(newLoans) : new ArrayList<>();
        this.isCurrentLoans = isCurrentLoans;

        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() { return oldList.size(); }

            @Override
            public int getNewListSize() { return loans.size(); }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).getId() == loans.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                BookLoan oldLoan = oldList.get(oldItemPosition);
                BookLoan newLoan = loans.get(newItemPosition);
                return oldLoan.getReturnDate().equals(newLoan.getReturnDate()) &&
                        oldLoan.getExtensionWeeks() == newLoan.getExtensionWeeks();
            }
        }).dispatchUpdatesTo(this);
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
        holder.bind(loans.get(position), isCurrentLoans);
    }

    @Override
    public int getItemCount() {
        return loans != null ? loans.size() : 0;
    }

    protected static class BookLoanViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookLoanBinding binding;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        public BookLoanViewHolder(ItemBookLoanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BookLoan loan, boolean isCurrentLoan) {
            binding.bookTitle.setText(loan.getBookName());
            binding.bookCode.setText(loan.getBookCode());
            binding.borrowDate.setText(dateFormat.format(loan.getBorrowDate()));
            binding.dueDate.setText(dateFormat.format(loan.getReturnDate()));

            String extensionsText = "Extensions remaining: " + (3 - loan.getExtensionWeeks());
            binding.extensionsLeft.setText(extensionsText);

            // Show extend button only for current loans with available extensions
            binding.extendButton.setVisibility(
                    isCurrentLoan && loan.getExtensionWeeks() < 3 ? View.VISIBLE : View.GONE);

            // Set card background color based on loan duration
            int colorResId;
            if (loan.getBookCode().startsWith("Yellow")) {
                colorResId = android.R.color.holo_orange_light;
            } else if (loan.getBookCode().startsWith("Green")) {
                colorResId = android.R.color.holo_green_light;
            } else {
                colorResId = android.R.color.holo_blue_light;
            }
            binding.getRoot().setCardBackgroundColor(
                    binding.getRoot().getContext().getResources().getColor(colorResId, null));
        }
    }
}