package com.scanner.audio_graffiti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.scanner.audio_graffiti.settings.SettingAccountActivity;
import com.scanner.audio_graffiti.settings.SettingHelpActivity;
import com.scanner.audio_graffiti.settings.SettingLocationActivity;
import com.scanner.audio_graffiti.settings.SettingNotificationActivity;
import com.scanner.audio_graffiti.settings.SettingPrivacyActivity;

/**
 * Handles the Main Settings Hub Screen (setting_page.xml).
 * Connects user navigation to all 5 sub-settings pages, profile edit, and Logout.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_page);

        // Edit Profile Button -> Opens Account Settings page
        View editProfileButton = findViewById(R.id.editProfile_button);
        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(v -> 
                startActivity(new Intent(SettingsActivity.this, SettingAccountActivity.class)));
        }

        // 1. Account Option
        View optionAccount = findViewById(R.id.option_account);
        if (optionAccount != null) {
            optionAccount.setOnClickListener(v -> 
                startActivity(new Intent(SettingsActivity.this, SettingAccountActivity.class)));
        }

        // 2. Location Settings Option
        View optionLocation = findViewById(R.id.option_location);
        if (optionLocation != null) {
            optionLocation.setOnClickListener(v -> 
                startActivity(new Intent(SettingsActivity.this, SettingLocationActivity.class)));
        }

        // 3. Notifications Option
        View optionNotifications = findViewById(R.id.option_notifications);
        if (optionNotifications != null) {
            optionNotifications.setOnClickListener(v -> 
                startActivity(new Intent(SettingsActivity.this, SettingNotificationActivity.class)));
        }

        // 4. Privacy & Safety Option
        View optionPrivacy = findViewById(R.id.option_privacy);
        if (optionPrivacy != null) {
            optionPrivacy.setOnClickListener(v -> 
                startActivity(new Intent(SettingsActivity.this, SettingPrivacyActivity.class)));
        }

        // 5. Help & Feedback Option
        View optionHelp = findViewById(R.id.option_help);
        if (optionHelp != null) {
            optionHelp.setOnClickListener(v -> 
                startActivity(new Intent(SettingsActivity.this, SettingHelpActivity.class)));
        }

        // LOG OUT button -> Returns to LoginActivity and clears backstack
        View logoutButton = findViewById(R.id.logout_button);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                Toast.makeText(SettingsActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}
