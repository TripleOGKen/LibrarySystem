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

public class ProfileViewModel extends AndroidViewModel {
    private final LibraryRepository repository;
    private final MutableLiveData<String> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel(Application application) {
        super(application);
        repository = new LibraryRepository(application);
    }

    public LiveData<Student> getStudentProfile(String studentId) {
        return repository.getStudent(studentId);
    }

    public void updatePassword(String studentId, String oldPassword, String newPassword) {
        isLoading.setValue(true);
        repository.getStudent(studentId).observeForever(student -> {
            if (student != null && student.getPassword().equals(oldPassword)) {
                repository.updatePassword(studentId, newPassword);
                updateResult.setValue("Password updated successfully");
            } else {
                updateResult.setValue("Current password is incorrect");
            }
            isLoading.setValue(false);
        });
    }

    public void updateProfilePicture(String studentId, Uri imageUri) {
        isLoading.setValue(true);
        try {
            // Create a file in the app's private directory
            File outputDir = getApplication().getFilesDir();
            File outputFile = new File(outputDir, "profile_" + UUID.randomUUID().toString() + ".jpg");

            // Copy the selected image to the app's private directory
            InputStream inputStream = getApplication().getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            // Update the profile picture path in the database
            repository.updateProfilePicture(studentId, outputFile.getAbsolutePath());
            updateResult.setValue("Profile picture updated successfully");
        } catch (Exception e) {
            updateResult.setValue("Failed to update profile picture: " + e.getMessage());
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