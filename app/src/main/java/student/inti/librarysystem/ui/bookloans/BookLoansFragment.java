package student.inti.librarysystem.ui.bookloans;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import student.inti.librarysystem.BookLoan;
import student.inti.librarysystem.R;
import student.inti.librarysystem.databinding.FragmentBookLoansBinding;
import student.inti.librarysystem.databinding.DialogLoanExtensionBinding;
import student.inti.librarysystem.util.FirebaseManager;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class BookLoansFragment extends Fragment implements BookLoanAdapter.OnLoanActionListener {
    private FragmentBookLoansBinding binding;
    private BookLoanAdapter adapter;
    private boolean isCurrentLoans = true;
    private FirebaseFirestore db;
    private String currentStudentId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookLoansBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();

        // Safely get current student ID
        String email = Objects.requireNonNull(
                FirebaseManager.getInstance().getCurrentUser()).getEmail();
        currentStudentId = email != null ? email.split("@")[0].toUpperCase() : "";

        setupRecyclerView();
        setupTabLayout();
        loadBookLoans();

        return binding.getRoot();
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
                isCurrentLoans = tab.getPosition() == 0;
                adapter.setCurrentLoans(isCurrentLoans);
                loadBookLoans();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadBookLoans() {
        Query query = db.collection("bookLoans")
                .whereEqualTo("studentId", currentStudentId)
                .whereEqualTo("isReturned", !isCurrentLoans);

        query.get().addOnSuccessListener(querySnapshot -> {
            adapter.submitList(querySnapshot.toObjects(BookLoan.class));
            binding.emptyView.setVisibility(
                    querySnapshot.isEmpty() ? View.VISIBLE : View.GONE);
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to load loans", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onExtendClick(BookLoan loan) {
        if (loan.getExtensionWeeks() >= 3) {
            Toast.makeText(getContext(),
                    "Maximum extension limit reached",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DialogLoanExtensionBinding dialogBinding = DialogLoanExtensionBinding.inflate(getLayoutInflater());

        // Set the remaining extensions text
        int remainingExtensions = 3 - loan.getExtensionWeeks();
        dialogBinding.remainingExtensions.setText(String.format("Remaining extensions: %d", remainingExtensions));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.extend_loan)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    final int selectedWeeks;
                    if (dialogBinding.twoWeeks.isChecked()) selectedWeeks = 2;
                    else if (dialogBinding.threeWeeks.isChecked()) selectedWeeks = 3;
                    else selectedWeeks = 1; // oneWeek is default

                    extendLoan(loan, selectedWeeks);
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void extendLoan(BookLoan loan, int weeks) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(loan.getReturnDate());
        calendar.add(Calendar.WEEK_OF_YEAR, weeks);
        Date newReturnDate = calendar.getTime();

        db.collection("bookLoans")
                .document(loan.getId())
                .update(
                        "returnDate", newReturnDate,
                        "extensionWeeks", loan.getExtensionWeeks() + weeks
                )
                .addOnSuccessListener(aVoid -> {
                    loadBookLoans(); // Reload to reflect changes
                    Toast.makeText(getContext(),
                            "Loan extended by " + weeks + " week(s)",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to extend loan",
                                Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}