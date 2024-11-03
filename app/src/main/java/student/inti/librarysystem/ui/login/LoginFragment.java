package student.inti.librarysystem.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import student.inti.librarysystem.R;
import student.inti.librarysystem.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Hide action bar during login
        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().hide();
        }

        if (binding != null) {
            binding.loginButton.setOnClickListener(v -> attemptLogin());
        }

        // Observe login result
        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (binding != null) {
                binding.loadingIndicator.setVisibility(View.GONE);
            }

            if (result.success) {
                // Navigate to profile fragment on successful login
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_loginFragment_to_profileFragment);
            } else {
                Toast.makeText(getContext(),
                        result.errorMessage != null ? result.errorMessage : "Login failed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Observe loading state
        loginViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (binding != null) {
                binding.loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void attemptLogin() {
        if (binding == null) return;

        String studentId = binding.studentIdInput.getText() != null ?
                binding.studentIdInput.getText().toString() : "";
        String password = binding.passwordInput.getText() != null ?
                binding.passwordInput.getText().toString() : "";

        if (studentId.isEmpty()) {
            binding.studentIdLayout.setError("Student ID is required");
            return;
        }

        if (password.isEmpty()) {
            binding.passwordLayout.setError("Password is required");
            return;
        }

        // Clear any previous errors
        binding.studentIdLayout.setError(null);
        binding.passwordLayout.setError(null);

        // Attempt login
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        loginViewModel.login(studentId, password);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}