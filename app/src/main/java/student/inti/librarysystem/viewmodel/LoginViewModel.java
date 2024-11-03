package student.inti.librarysystem.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import student.inti.librarysystem.data.entity.Student;
import student.inti.librarysystem.repository.LibraryRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends AndroidViewModel {
    private final LibraryRepository repository;
    private final ExecutorService executorService;
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public static class LoginResult {
        public final boolean success;
        public final String errorMessage;
        public final Student student;

        private LoginResult(boolean success, String errorMessage, Student student) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.student = student;
        }

        static LoginResult success(Student student) {
            return new LoginResult(true, null, student);
        }

        static LoginResult error(String message) {
            return new LoginResult(false, message, null);
        }
    }

    public LoginViewModel(Application application) {
        super(application);
        repository = new LibraryRepository(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public void login(String studentId, String password) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                // Simulated network delay
                Thread.sleep(1000);

                Student student = repository.loginStudent(studentId, password);
                if (student != null) {
                    loginResult.postValue(LoginResult.success(student));
                } else {
                    loginResult.postValue(LoginResult.error("Invalid student ID or password"));
                }
            } catch (Exception e) {
                loginResult.postValue(LoginResult.error("Login failed: " + e.getMessage()));
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