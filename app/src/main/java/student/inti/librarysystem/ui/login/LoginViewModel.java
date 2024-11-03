package student.inti.librarysystem.ui.login;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import student.inti.librarysystem.data.LibraryDatabase;
import student.inti.librarysystem.data.entity.Student;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends AndroidViewModel {
    private final LibraryDatabase database;
    private final ExecutorService executorService;
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private static final String TAG = "LoginViewModel";

    public LoginViewModel(Application application) {
        super(application);
        database = LibraryDatabase.getDatabase(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public void login(String studentId, String password) {
        Log.d(TAG, "Attempting login with ID: " + studentId);
        isLoading.setValue(true);

        executorService.execute(() -> {
            try {
                Student student = database.studentDao().login(studentId, password);
                Log.d(TAG, "Login result: " + (student != null ? "Success" : "Failed"));

                if (student != null) {
                    loginResult.postValue(new LoginResult(true, null, student));
                } else {
                    loginResult.postValue(new LoginResult(false, "Invalid credentials", null));
                }
            } catch (Exception e) {
                Log.e(TAG, "Login error: " + e.getMessage());
                loginResult.postValue(new LoginResult(false, "Login failed: " + e.getMessage(), null));
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}