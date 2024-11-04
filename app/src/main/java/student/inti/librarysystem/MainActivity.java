package student.inti.librarysystem;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import student.inti.librarysystem.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("LibrarySystemPrefs", MODE_PRIVATE);

        // Set up the toolbar
        setSupportActionBar(binding.toolbar);

        // Set up Navigation Controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Set up Navigation Drawer
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.loginFragment,
                    R.id.profileFragment,
                    R.id.roomBookingFragment,
                    R.id.bookLoansFragment,
                    R.id.examPapersFragment
            ).setOpenableLayout(binding.drawerLayout).build();

            // Set up ActionBar with NavController
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Set up NavigationView
            binding.navView.setNavigationItemSelectedListener(this);
            NavigationUI.setupWithNavController(binding.navView, navController);

            // Update navigation header with user info
            updateNavigationHeader();
        }

        // Add drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1);
            }
        }

        // Handle back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    if (isEnabled()) {
                        setEnabled(false);
                        getOnBackPressedDispatcher().onBackPressed();
                    }
                }
            }
        });
    }

    private void updateNavigationHeader() {
        View headerView = binding.navView.getHeaderView(0);
        TextView nameTextView = headerView.findViewById(R.id.nav_header_name);
        TextView emailTextView = headerView.findViewById(R.id.nav_header_email);

        // Get user info from SharedPreferences
        String userName = sharedPreferences.getString("user_name", "Student Name");
        String userEmail = sharedPreferences.getString("user_email", "student@example.com");

        nameTextView.setText(userName);
        emailTextView.setText(userEmail);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            // Clear user session
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Restart app from login
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        // Handle navigation for other menu items
        boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
        if (handled) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        return handled;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}