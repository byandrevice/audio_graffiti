package com.scanner.audio_graffiti.settings;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.scanner.audio_graffiti.R;
import com.scanner.audio_graffiti.ui.ThemeUtils;

/**
 * Handles Location Sub-Settings Screen (setting_location_page.xml).
 * Configures Precise GPS Location and UCF Campus Radius filtering.
 */
public class SettingLocationActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "location_prefs";
    private MaterialSwitch switchGps;
    private MaterialSwitch switchRadius;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                if (fineGranted) {
                    Toast.makeText(this, "Precise Location permission granted!", Toast.LENGTH_SHORT).show();
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putBoolean("precise_gps", true).apply();
                    if (switchGps != null) switchGps.setChecked(true);
                } else {
                    Toast.makeText(this, "Location permission is required for Precise GPS", Toast.LENGTH_LONG).show();
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putBoolean("precise_gps", false).apply();
                    if (switchGps != null) switchGps.setChecked(false);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_location_page);

        ThemeUtils.applyTheme(this);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Circular Back Button -> Returns to Settings main screen
        View backButton = findViewById(R.id.back_button_card);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        switchGps = findViewById(R.id.switch_gps);
        switchRadius = findViewById(R.id.switch_radius);

        boolean hasLocationPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean defaultGps = prefs.getBoolean("precise_gps", hasLocationPerm);

        if (switchGps != null) {
            switchGps.setChecked(defaultGps);
            switchGps.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        locationPermissionLauncher.launch(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        });
                    } else {
                        prefs.edit().putBoolean("precise_gps", true).apply();
                        Toast.makeText(this, "Precise GPS Location enabled", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    prefs.edit().putBoolean("precise_gps", false).apply();
                    Toast.makeText(this, "Precise GPS Location disabled", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (switchRadius != null) {
            switchRadius.setChecked(prefs.getBoolean("campus_radius", true));
            switchRadius.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("campus_radius", isChecked).apply();
                Toast.makeText(this, isChecked ? "UCF Campus 5-mile radius filter enabled" : "Campus radius filter disabled", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
    }
}
