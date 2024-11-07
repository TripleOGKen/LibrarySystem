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
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private static final String TAG = "LoginViewModel";

    public LoginViewModel(Application application) {
        super(application);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public void login(String studentId, String password) {
        Log.d(TAG, "Attempting login with ID: " + studentId);
        isLoading.setValue(true);

        // Convert studentId to email format
        String email = studentId.toLowerCase() + "@student.newinti.edu.my";

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get additional student data from Firestore
                        String uid = firebaseAuth.getCurrentUser().getUid();
                        firestore.collection("students")
                                .document(studentId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    Student student = documentSnapshot.toObject(Student.class);
                                    if (student != null) {
                                        loginResult.setValue(new LoginResult(true, null, student));
                                    } else {
                                        loginResult.setValue(new LoginResult(false, "Student data not found", null));
                                    }
                                    isLoading.setValue(false);
                                })
                                .addOnFailureListener(e -> {
                                    loginResult.setValue(new LoginResult(false, "Failed to fetch student data", null));
                                    isLoading.setValue(false);
                                });
                    } else {
                        loginResult.setValue(new LoginResult(false,
                                task.getException() != null ? task.getException().getMessage() : "Login failed",
                                null));
                        isLoading.setValue(false);
                    }
                });
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}