package student.inti.librarysystem.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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
import android.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
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

        // Safely get current student ID
        String email = Objects.requireNonNull(
                FirebaseManager.getInstance().getCurrentUser()).getEmail();
        currentStudentId = email != null ? email.split("@")[0].toUpperCase() : "";
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
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading profile", e);
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUIWithStudentData() {
        binding.studentIdInput.setText(currentStudent.getStudentId());
        binding.emailInput.setText(currentStudent.getEmail());

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
                                        Toast.makeText(getContext(),
                                                "Profile picture updated",
                                                Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to update profile URL", e);
                                        Toast.makeText(getContext(),
                                                "Failed to update profile picture",
                                                Toast.LENGTH_SHORT).show();
                                    });
                        })
                )
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload profile picture", e);
                    Toast.makeText(getContext(),
                            "Failed to upload profile picture",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void attemptPasswordChange() {
        if (!validatePasswordInputs()) {
            return;
        }

        String currentPassword = Objects.requireNonNull(binding.currentPasswordInput.getText()).toString();
        String newPassword = Objects.requireNonNull(binding.newPasswordInput.getText()).toString();

        // Verify current password
        String currentSalt = currentStudent.getSalt();
        String hashedCurrentPassword = hashPassword(currentPassword, currentSalt);

        if (!Objects.equals(hashedCurrentPassword, currentStudent.getHashedPassword())) {
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
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.loginFragment);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update password", e);
                    Toast.makeText(getContext(),
                            "Failed to update password",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validatePasswordInputs() {
        String currentPassword = binding.currentPasswordInput.getText() != null ?
                binding.currentPasswordInput.getText().toString() : "";
        String newPassword = binding.newPasswordInput.getText() != null ?
                binding.newPasswordInput.getText().toString() : "";

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(getContext(), "Please fill in all password fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return android.util.Base64.encodeToString(salt, android.util.Base64.NO_WRAP);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(android.util.Base64.decode(salt, android.util.Base64.NO_WRAP));
            byte[] hashedPassword = md.digest(password.getBytes());
            return android.util.Base64.encodeToString(hashedPassword, android.util.Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to hash password", e);
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}