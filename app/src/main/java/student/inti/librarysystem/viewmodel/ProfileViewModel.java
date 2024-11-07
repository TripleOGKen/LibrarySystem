package student.inti.librarysystem.viewmodel;

import android.app.Application;
import android.net.Uri;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import student.inti.librarysystem.data.entity.Student;
import student.inti.librarysystem.repository.LibraryRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;
import student.inti.librarysystem.util.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileViewModel extends AndroidViewModel {
    private final LibraryRepository repository;
    private final MutableLiveData<String> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final FirebaseAuth firebaseAuth;

    public ProfileViewModel(Application application) {
        super(application);
        repository = new LibraryRepository(application);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Student> getStudentProfile(String studentId) {
        return repository.getStudent(studentId);
    }

    public void updatePassword(String studentId, String oldPassword, String newPassword) {
        isLoading.setValue(true);

        // Get current Firebase user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            updateResult.setValue("User not logged in");
            isLoading.setValue(false);
            return;
        }

        // Create credential for reauthentication
        String email = studentId.toLowerCase() + "@student.newinti.edu.my";
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

        // First get the student data from Firestore directly
        FirebaseFirestore.getInstance()
                .collection("students")
                .document(studentId.toUpperCase())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        updateResult.setValue("Student data not found");
                        isLoading.setValue(false);
                        return;
                    }

                    String salt = documentSnapshot.getString("salt");
                    if (salt == null) {
                        // Generate new salt if not exists
                        salt = FirebaseManager.generateSalt();
                    }

                    // Store the salt for later use
                    String finalSalt = salt;


                    // Reauthenticate and update password
                    user.reauthenticate(credential)
                            .addOnSuccessListener(aVoid -> {
                                // Reauthentication successful, now update password
                                user.updatePassword(newPassword)
                                        .addOnSuccessListener(aVoid2 -> {
                                            // Also update the hashed password in Firestore
                                            String newHashedPassword = FirebaseManager.hashPassword(newPassword, finalSalt);
                                            FirebaseFirestore.getInstance()
                                                    .collection("students")
                                                    .document(studentId.toUpperCase())
                                                    .update(
                                                            "hashedPassword", newHashedPassword,
                                                            "salt", finalSalt
                                                    )
                                                    .addOnSuccessListener(aVoid3 -> {
                                                        updateResult.setValue("Password updated successfully");
                                                        isLoading.setValue(false);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        updateResult.setValue("Failed to update password in database");
                                                        isLoading.setValue(false);
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            updateResult.setValue("Failed to update password: " + e.getMessage());
                                            isLoading.setValue(false);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                updateResult.setValue("Current password is incorrect");
                                isLoading.setValue(false);
                            });
                })
                .addOnFailureListener(e -> {
                    updateResult.setValue("Failed to fetch student data");
                    isLoading.setValue(false);
                });
    }


    public void updateProfilePicture(String studentId, Uri imageUri) {
        isLoading.setValue(true);
        try {
            // Create a file in the app's private directory
            File outputDir = getApplication().getFilesDir();
            File outputFile = new File(outputDir, "profile_" + UUID.randomUUID().toString() + ".jpg");

            // Copy the contents of the imageUri to the output file
            InputStream inputStream = getApplication().getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();

            // Update the profile picture in the repository
            repository.updateProfilePicture(studentId, outputFile.getAbsolutePath());
            updateResult.setValue("Profile picture updated successfully");
        } catch (Exception e) {
            updateResult.setValue("Failed to update profile picture");
        } finally {
            isLoading.setValue(false);
        }
    }

    public LiveData<String> getUpdateResult() {
        return updateResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}