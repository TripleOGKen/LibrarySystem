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
import student.inti.librarysystem.R;
import student.inti.librarysystem.data.entity.BookLoan;
import student.inti.librarysystem.databinding.FragmentBookLoansBinding;
import student.inti.librarysystem.databinding.DialogLoanExtensionBinding;

public class BookLoansFragment extends Fragment implements BookLoanAdapter.OnLoanActionListener {
    private FragmentBookLoansBinding binding;
    private BookLoanViewModel viewModel;
    private BookLoanAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookLoansBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(BookLoanViewModel.class);

        setupRecyclerView();
        observeViewModel();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new BookLoanAdapter(this);
        binding.loansRecyclerView.setAdapter(adapter);
        binding.loansRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observeViewModel() {
        viewModel.getActiveLoans().observe(getViewLifecycleOwner(), loans -> {
            adapter.updateLoans(loans);
            binding.emptyView.setVisibility(loans.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onExtendClick(BookLoan loan) {
        showExtensionDialog(loan);
    }

    private void showExtensionDialog(BookLoan loan) {
        DialogLoanExtensionBinding dialogBinding = DialogLoanExtensionBinding.inflate(getLayoutInflater());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.extend_loan)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    int selectedWeeks = 1;
                    int checkedId = dialogBinding.extensionPeriodGroup.getCheckedRadioButtonId();
                    if (checkedId == R.id.two_weeks) selectedWeeks = 2;
                    else if (checkedId == R.id.three_weeks) selectedWeeks = 3;

                    viewModel.extendLoan(loan.getId(), selectedWeeks);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialogBinding.remainingExtensions.setText(getString(
                R.string.remaining_extensions,
                3 - loan.getExtensionWeeks()
        ));

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}