package student.inti.librarysystem.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import student.inti.librarysystem.R;
import student.inti.librarysystem.databinding.FragmentProfileBinding;
import student.inti.librarysystem.util.ProfileManager;
import java.io.File;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupImagePicker();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        setupViews();
        loadProfilePicture();
        return binding.getRoot();
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        handleImageResult(result.getData().getData());
                    }
                }
        );
    }

    private void setupViews() {
        binding.changePhotoButton.setOnClickListener(v -> openImagePicker());
        binding.changePasswordButton.setOnClickListener(v -> attemptPasswordChange());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleImageResult(Uri imageUri) {
        try {
            String imagePath = ProfileManager.saveProfilePicture(requireContext(), imageUri);
            if (imagePath != null) {
                loadProfilePicture();
                Toast.makeText(getContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfilePicture() {
        File profilePic = ProfileManager.getProfilePicture(requireContext(), "P23014788");
        if (profilePic != null) {
            Glide.with(this)
                    .load(profilePic)
                    .centerCrop()
                    .into(binding.profileImage);
        } else {
            binding.profileImage.setImageResource(R.drawable.default_profile);
        }
    }

    private void attemptPasswordChange() {
        String currentPassword = binding.currentPasswordInput.getText().toString();
        String newPassword = binding.newPasswordInput.getText().toString();

        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!currentPassword.equals("test123")) {
            Toast.makeText(getContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update password and navigate to login
        Toast.makeText(getContext(), "Password updated successfully. Please login again",
                Toast.LENGTH_LONG).show();

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.loginFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}