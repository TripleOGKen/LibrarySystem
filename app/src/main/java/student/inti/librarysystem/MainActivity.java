package student.inti.librarysystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import student.inti.librarysystem.util.FirebaseManager;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private FirebaseFirestore db;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Setup toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        // Configure the navigation drawer
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.profileFragment,
                R.id.roomBookingFragment,
                R.id.bookLoansFragment,
                R.id.examPapersFragment)
                .setOpenableLayout(drawer)
                .build();

        // Setup Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            // Add sign out handler
            navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(item -> {
                FirebaseManager.getInstance().signOut();
                navController.navigate(R.id.loginFragment);
                drawer.closeDrawers();
                return true;
            });

            // Check authentication state and load user data
            checkAuthAndLoadUserData(navigationView);
        }
    }

    private void checkAuthAndLoadUserData(NavigationView navigationView) {
        FirebaseUser currentUser = FirebaseManager.getInstance().getCurrentUser();

        // Get header view for navigation drawer
        View headerView = navigationView.getHeaderView(0);
        TextView nameTextView = headerView.findViewById(R.id.nav_header_student_name);
        TextView idTextView = headerView.findViewById(R.id.nav_header_student_id);
        ImageView profileImageView = headerView.findViewById(R.id.nav_header_profile_image);

        if (currentUser != null && currentUser.getEmail() != null) {
            String email = currentUser.getEmail();
            String currentUserId = email.split("@")[0].toUpperCase();

            db.collection("students")
                    .document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                            nameTextView.setText(name);
                            idTextView.setText(currentUserId);

                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.default_profile)
                                        .error(R.drawable.default_profile)
                                        .into(profileImageView);
                            } else {
                                profileImageView.setImageResource(R.drawable.default_profile);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to load profile
                        nameTextView.setText("");
                        idTextView.setText("");
                        profileImageView.setImageResource(R.drawable.default_profile);
                    });
        } else {
            // User not logged in, navigate to login
            if (navController != null) {
                navController.navigate(R.id.loginFragment);
            }
            // Clear user info
            nameTextView.setText("");
            idTextView.setText("");
            profileImageView.setImageResource(R.drawable.default_profile);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}