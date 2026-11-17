package com.scanner.audio_graffiti.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.scanner.audio_graffiti.R;
import com.scanner.audio_graffiti.ui.ThemeUtils;

/**
 * Handles Privacy & Safety Sub-Settings Screen (setting_privacy_page.xml).
 * Configures anonymous voice drops, UCF student filter, and blocked accounts.
 */
public class SettingPrivacyActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "privacy_prefs";

    private MaterialSwitch switchAnonymous;
    private MaterialSwitch switchStudentsOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_privacy_page);

        ThemeUtils.applyTheme(this);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Circular Back Button -> Returns to Settings main screen
        View backButton = findViewById(R.id.back_button_card);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        switchAnonymous = findViewById(R.id.switch_anonymous);
        switchStudentsOnly = findViewById(R.id.switch_students_only);

        if (switchAnonymous != null) {
            switchAnonymous.setChecked(prefs.getBoolean("anonymous_memos", false));
            switchAnonymous.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("anonymous_memos", isChecked).apply();
                Toast.makeText(this, isChecked ? "Anonymous voice drops enabled" : "Username visible on drops", Toast.LENGTH_SHORT).show();
            });
        }

        if (switchStudentsOnly != null) {
            switchStudentsOnly.setChecked(prefs.getBoolean("students_only", true));
            switchStudentsOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("students_only", isChecked).apply();
                Toast.makeText(this, isChecked ? "UCF verified students filter enabled" : "Public voice drop visibility enabled", Toast.LENGTH_SHORT).show();
            });
        }

        // Blocked Accounts Card
        View cardBlockedUsers = findViewById(R.id.card_blocked_users);
        if (cardBlockedUsers != null) {
            cardBlockedUsers.setOnClickListener(v -> showBlockedUsersDialog());
        }
    }

    private void showBlockedUsersDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Blocked Accounts")
                .setMessage("No accounts currently blocked.\nTo block or mute a user, tap 'Report User' on their campus voice drop.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
    }
}
