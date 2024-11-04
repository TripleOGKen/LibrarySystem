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
import student.inti.librarysystem.R;
import student.inti.librarysystem.data.entity.BookLoan;
import student.inti.librarysystem.databinding.FragmentBookLoansBinding;
import student.inti.librarysystem.databinding.DialogLoanExtensionBinding;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookLoansBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(BookLoanViewModel.class);

        setupRecyclerView();
        setupTabLayout();
        loadSampleData(); // For demonstration

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
                isCurrentLoansTab = tab.getPosition() == 0;
                updateLoansDisplay();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadSampleData() {
        // Sample current loans
        List<BookLoan> currentLoans = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Current Loan 1
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        currentLoans.add(new BookLoan(
                "Introduction to Programming",
                "Yellow-7D-001",
                new Date(),
                calendar.getTime(),
                0
        ));

        // Current Loan 2
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        currentLoans.add(new BookLoan(
                "Database Management Systems",
                "Green-14D-002",
                new Date(),
                calendar.getTime(),
                0
        ));

        // Current Loan 3
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 21);
        currentLoans.add(new BookLoan(
                "Software Engineering Principles",
                "Blue-21D-003",
                new Date(),
                calendar.getTime(),
                0
        ));

        // Sample loan history
        List<BookLoan> loanHistory = new ArrayList<>();
        calendar.setTime(new Date());

        // History 1
        calendar.add(Calendar.MONTH, -1);
        Date oldBorrowDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        loanHistory.add(new BookLoan(
                "Web Development Basics",
                "Yellow-7D-004",
                oldBorrowDate,
                calendar.getTime(),
                0,
                true
        ));

        // History 2
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -2);
        oldBorrowDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        loanHistory.add(new BookLoan(
                "Data Structures and Algorithms",
                "Green-14D-005",
                oldBorrowDate,
                calendar.getTime(),
                1,
                true
        ));

        // History 3
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -3);
        oldBorrowDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 21);
        loanHistory.add(new BookLoan(
                "Mobile App Development",
                "Blue-21D-006",
                oldBorrowDate,
                calendar.getTime(),
                2,
                true
        ));

        // History 4
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -4);
        oldBorrowDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        loanHistory.add(new BookLoan(
                "Computer Networks",
                "Green-14D-007",
                oldBorrowDate,
                calendar.getTime(),
                1,
                true
        ));

        viewModel.setCurrentLoans(currentLoans);
        viewModel.setLoanHistory(loanHistory);
        updateLoansDisplay();
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
                    int selectedWeeks = 1; // Default to 1 week
                    if (dialogBinding.twoWeeks.isChecked()) selectedWeeks = 2;
                    else if (dialogBinding.threeWeeks.isChecked()) selectedWeeks = 3;

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(loan.getReturnDate());
                    calendar.add(Calendar.WEEK_OF_YEAR, selectedWeeks);
                    loan.setReturnDate(calendar.getTime());
                    loan.setExtensionWeeks(loan.getExtensionWeeks() + selectedWeeks);

                    viewModel.updateLoan(loan);
                    Toast.makeText(getContext(),
                            "Loan extended by " + selectedWeeks + " week(s)",
                            Toast.LENGTH_SHORT).show();
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