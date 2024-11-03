package student.inti.librarysystem.ui.bookloans;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import student.inti.librarysystem.data.entity.Book;
import student.inti.librarysystem.data.entity.BookLoan;
import student.inti.librarysystem.databinding.ItemBookLoanBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookLoanAdapter extends RecyclerView.Adapter<BookLoanAdapter.BookLoanViewHolder> {
    private final List<BookLoan> loans = new ArrayList<>();
    private final OnLoanActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

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
        holder.bind(loan);
    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

    public void updateLoans(List<BookLoan> newLoans) {
        loans.clear();
        loans.addAll(newLoans);
        notifyDataSetChanged();
    }

    class BookLoanViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookLoanBinding binding;

        BookLoanViewHolder(ItemBookLoanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BookLoan loan) {
            binding.bookTitle.setText(loan.getBookId()); // Should be replaced with actual book title
            binding.dueDate.setText("Due: " + dateFormat.format(loan.getDueDate()));
            binding.extensionsLeft.setText("Extensions left: " + (3 - loan.getExtensionWeeks()));

            binding.extendButton.setVisibility(loan.getExtensionWeeks() < 3 ? View.VISIBLE : View.GONE);
            binding.extendButton.setOnClickListener(v -> listener.onExtendClick(loan));
        }
    }
}