package student.inti.librarysystem.util;

import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class FirebaseManager {
    private static FirebaseManager instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private static final String TAG = "FirebaseManager";

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public Task<DocumentSnapshot> loginWithStudentId(String studentId, String password) {
        // Convert to lowercase and proper email format
        String email = studentId.toLowerCase() + "@student.newinti.edu.my";
        Log.d(TAG, "Attempting login with email: " + email);

        return auth.signInWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "Firebase Auth successful, getting user data");
                        // Use uppercase student ID for document lookup
                        return db.collection("students")
                                .document(studentId.toUpperCase())
                                .get()
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Error fetching student data: " + e.getMessage()));
                    }
                    Log.e(TAG, "Firebase Auth failed", task.getException());
                    throw task.getException();
                });
    }

    public void signOut() {
        auth.signOut();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public Task<DocumentSnapshot> getStudentProfile(String studentId) {
        return db.collection("students")
                .document(studentId.toUpperCase())
                .get();
    }
}