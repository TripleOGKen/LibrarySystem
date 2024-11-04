package student.inti.librarysystem.ui.login;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import student.inti.librarysystem.model.Student;
import student.inti.librarysystem.util.FirebaseManager;

public class LoginViewModel extends AndroidViewModel {
    private final FirebaseManager firebaseManager;
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private static final String TAG = "LoginViewModel";

    public LoginViewModel(Application application) {
        super(application);
        firebaseManager = FirebaseManager.getInstance();
    }

    public void login(String studentId, String password) {
        Log.d(TAG, "Attempting login with ID: " + studentId);
        isLoading.setValue(true);

        firebaseManager.loginWithStudentId(studentId, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Student student = task.getResult().toObject(Student.class);
                        if (student != null) {
                            loginResult.setValue(new LoginResult(true, null, student));
                        } else {
                            loginResult.setValue(new LoginResult(false, "User data not found", null));
                        }
                    } else {
                        loginResult.setValue(new LoginResult(false,
                                task.getException() != null ? task.getException().getMessage() : "Login failed",
                                null));
                    }
                    isLoading.setValue(false);
                });
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}