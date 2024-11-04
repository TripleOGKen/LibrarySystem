package student.inti.librarysystem.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import student.inti.librarysystem.R;
import student.inti.librarysystem.databinding.FragmentProfileBinding;
import student.inti.librarysystem.util.FirebaseManager;
import student.inti.librarysystem.model.Student;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String currentStudentId;
    private Student currentStudent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupImagePicker();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Get current student ID from Firebase Auth
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        currentStudentId = firebaseManager.getCurrentUser().getEmail().split("@")[0].toUpperCase();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        setupViews();
        loadUserProfile();
        return binding.getRoot();
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            handleImageResult(imageUri);
                        }
                    }
                }
        );
    }

    private void setupViews() {
        binding.changePhotoButton.setOnClickListener(v -> openImagePicker());
        binding.changePasswordButton.setOnClickListener(v -> attemptPasswordChange());
    }

    private void loadUserProfile() {
        db.collection("students")
                .document(currentStudentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentStudent = documentSnapshot.toObject(Student.class);
                    if (currentStudent != null) {
                        updateUIWithStudentData();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateUIWithStudentData() {
        binding.studentIdText.setText(currentStudent.getStudentId());
        binding.nameText.setText(currentStudent.getName());
        binding.emailText.setText(currentStudent.getEmail());

        if (currentStudent.getProfileImageUrl() != null && !currentStudent.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentStudent.getProfileImageUrl())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(binding.profileImage);
        } else {
            binding.profileImage.setImageResource(R.drawable.default_profile);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleImageResult(Uri imageUri) {
        if (getContext() == null) return;

        // Show loading indicator
        binding.progressBar.setVisibility(View.VISIBLE);

        StorageReference storageRef = storage.getReference()
                .child("profile_pictures")
                .child(currentStudentId + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String imageUrl = downloadUri.toString();
                            db.collection("students")
                                    .document(currentStudentId)
                                    .update("profileImageUrl", imageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        currentStudent.setProfileImageUrl(imageUrl);
                                        updateUIWithStudentData();
                                        binding.progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(),
                                                "Profile picture updated",
                                                Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        binding.progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(),
                                                "Failed to update profile picture",
                                                Toast.LENGTH_SHORT).show();
                                    });
                        })
                )
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(),
                            "Failed to upload profile picture",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void attemptPasswordChange() {
        String currentPassword = binding.currentPasswordInput.getText().toString();
        String newPassword = binding.newPasswordInput.getText().toString();
        String confirmPassword = binding.confirmPasswordInput.getText().toString();

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) ||
                TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getContext(), "Please fill in all password fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify current password
        String currentSalt = currentStudent.getSalt();
        String hashedCurrentPassword = hashPassword(currentPassword, currentSalt);

        if (!hashedCurrentPassword.equals(currentStudent.getHashedPassword())) {
            Toast.makeText(getContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate new salt and hash for new password
        String newSalt = generateSalt();
        String hashedNewPassword = hashPassword(newPassword, newSalt);

        // Update password in Firestore
        db.collection("students")
                .document(currentStudentId)
                .update(
                        "hashedPassword", hashedNewPassword,
                        "salt", newSalt
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),
                            "Password updated successfully. Please login again.",
                            Toast.LENGTH_LONG).show();

                    // Sign out and navigate to login
                    FirebaseManager.getInstance().signOut();
                    if (getView() != null) {
                        NavController navController = Navigation.findNavController(getView());
                        navController.navigate(R.id.loginFragment);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to update password",
                                Toast.LENGTH_SHORT).show()
                );
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}