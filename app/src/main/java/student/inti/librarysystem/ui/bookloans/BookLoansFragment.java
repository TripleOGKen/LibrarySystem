package student.inti.librarysystem.ui.bookloans;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import student.inti.librarysystem.R;
import student.inti.librarysystem.data.entity.BookLoan;
import student.inti.librarysystem.databinding.FragmentBookLoansBinding;
import student.inti.librarysystem.databinding.DialogLoanExtensionBinding;
import student.inti.librarysystem.util.FirebaseManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookLoansFragment extends Fragment implements BookLoanAdapter.OnLoanActionListener {
    private FragmentBookLoansBinding binding;
    private BookLoanViewModel viewModel;
    private BookLoanAdapter adapter;
    private boolean isCurrentLoansTab = true;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private FirebaseFirestore db;
    private String currentStudentId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookLoansBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(BookLoanViewModel.class);
        db = FirebaseFirestore.getInstance();

        // Get current student ID from Firebase Auth
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        currentStudentId = firebaseManager.getCurrentUser().getEmail().split("@")[0].toUpperCase();

        setupRecyclerView();
        setupTabLayout();
        loadBookLoans();

        return binding.getRoot();
    }

    private void loadBookLoans() {
        // Query for current loans
        db.collection("bookLoans")
                .whereEqualTo("studentId", currentStudentId)
                .whereEqualTo("isReturned", false)
                .get()
                .addOnSuccessListener(currentLoansSnapshot -> {
                    List<BookLoan> currentLoans = new ArrayList<>();
                    currentLoansSnapshot.forEach(doc -> {
                        BookLoan loan = doc.toObject(BookLoan.class);
                        currentLoans.add(loan);
                    });
                    viewModel.setCurrentLoans(currentLoans);
                    if (isCurrentLoansTab) {
                        updateLoansDisplay();
                    }
                });

        // Query for loan history
        db.collection("bookLoans")
                .whereEqualTo("studentId", currentStudentId)
                .whereEqualTo("isReturned", true)
                .get()
                .addOnSuccessListener(historySnapshot -> {
                    List<BookLoan> loanHistory = new ArrayList<>();
                    historySnapshot.forEach(doc -> {
                        BookLoan loan = doc.toObject(BookLoan.class);
                        loanHistory.add(loan);
                    });
                    viewModel.setLoanHistory(loanHistory);
                    if (!isCurrentLoansTab) {
                        updateLoansDisplay();
                    }
                });
    }

    private void setupRecyclerView() {
        adapter = new BookLoanAdapter(this);
        binding.loansRecyclerView.setAdapter(adapter);
        binding.loansRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isCurrentLoansTab = tab.getPosition() == 0;
                updateLoansDisplay();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void updateLoansDisplay() {
        if (isCurrentLoansTab) {
            adapter.updateLoans(viewModel.getCurrentLoans().getValue(), true);
        } else {
            adapter.updateLoans(viewModel.getLoanHistory().getValue(), false);
        }
        binding.emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onExtendClick(BookLoan loan) {
        // Only allow extension if remaining extension weeks is less than 3
        if (loan.getExtensionWeeks() >= 3) {
            Toast.makeText(getContext(), "Maximum extension limit reached", Toast.LENGTH_SHORT).show();
            return;
        }

        DialogLoanExtensionBinding dialogBinding = DialogLoanExtensionBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.extend_loan)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    int selectedWeeks = 1;
                    if (dialogBinding.twoWeeks.isChecked()) selectedWeeks = 2;
                    else if (dialogBinding.threeWeeks.isChecked()) selectedWeeks = 3;

                    // Calculate new return date
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(loan.getReturnDate());
                    calendar.add(Calendar.WEEK_OF_YEAR, selectedWeeks);
                    Date newReturnDate = calendar.getTime();

                    // Update in Firestore
                    db.collection("bookLoans")
                            .whereEqualTo("bookId", loan.getBookId())
                            .whereEqualTo("studentId", currentStudentId)
                            .whereEqualTo("isReturned", false)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    String docId = querySnapshot.getDocuments().get(0).getId();
                                    db.collection("bookLoans").document(docId)
                                            .update(
                                                    "returnDate", newReturnDate,
                                                    "extensionWeeks", loan.getExtensionWeeks() + selectedWeeks
                                            )
                                            .addOnSuccessListener(aVoid -> {
                                                loan.setReturnDate(newReturnDate);
                                                loan.setExtensionWeeks(loan.getExtensionWeeks() + selectedWeeks);
                                                viewModel.updateLoan(loan);
                                                Toast.makeText(getContext(),
                                                        "Loan extended by " + selectedWeeks + " week(s)",
                                                        Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(getContext(),
                                                    "Failed to extend loan",
                                                    Toast.LENGTH_SHORT).show());
                                }
                            });
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}