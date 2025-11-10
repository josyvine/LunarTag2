package com.safevoice.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.safevoice.app.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The main screen of the application, which hosts the bottom navigation and various fragments.
 * It is responsible for requesting all necessary runtime permissions upon creation.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    // Launcher for handling multiple permission requests.
    private ActivityResultLauncher<String[]> permissionLauncher;

    // List of all permissions required by the app.
    private final String[] requiredPermissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CAMERA,
            // POST_NOTIFICATIONS is required for Android 13 (API 33) and above
            // for the foreground service notification.
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? Manifest.permission.POST_NOTIFICATIONS : null
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup the navigation controller and bottom navigation view.
        // This connects the tabs in the bottom bar to the navigation graph.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.navView, navController);
        }

        // Initialize the permission launcher.
        // This defines what to do after the user responds to the permission dialog.
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> results) {
                        // After the user responds, check if any permissions were denied.
                        boolean allGranted = true;
                        for (Boolean granted : results.values()) {
                            if (!granted) {
                                allGranted = false;
                                break;
                            }
                        }

                        if (allGranted) {
                            Toast.makeText(MainActivity.this, "All permissions granted. Safe Voice is ready.", Toast.LENGTH_SHORT).show();
                        } else {
                            // If permissions are denied, inform the user that the app may not function correctly.
                            Toast.makeText(MainActivity.this, "Some permissions were denied. Core features may not work.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // Check for and request any permissions that have not yet been granted.
        checkAndRequestPermissions();
    }

    /**
     * Checks which of the required permissions are not yet granted and requests them.
     */
    private void checkAndRequestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : requiredPermissions) {
            // The POST_NOTIFICATIONS permission can be null on older Android versions, so we check for that.
            if (permission != null && ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        // If there are permissions that need to be requested, launch the permission dialog.
        if (!permissionsToRequest.isEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        }
    }
}