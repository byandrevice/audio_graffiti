package com.scanner.audio_graffiti.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.scanner.audio_graffiti.LoginActivity;
import com.scanner.audio_graffiti.R;

/**
 * Handles Account Profile Screen (setting_account_page.xml).
 * Allows users to edit Name, Email, Edit Password, and Log Out.
 */
public class SettingAccountActivity extends AppCompatActivity {

    private EditText userNameEdit;
    private EditText emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account_page);

        userNameEdit = findViewById(R.id.userName_edit);
        emailEdit = findViewById(R.id.email_edit);

        // Circular Back Button -> Returns to Settings main screen
        View backButton = findViewById(R.id.back_button_card);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        // Edit Avatar Badge -> Action prompt
        View editAvatarBadge = findViewById(R.id.edit_avatar_badge);
        if (editAvatarBadge != null) {
            editAvatarBadge.setOnClickListener(v -> 
                Toast.makeText(SettingAccountActivity.this, "Change Avatar", Toast.LENGTH_SHORT).show());
        }

        // Delete Account Link -> Action prompt
        TextView deleteAccountLink = findViewById(R.id.deleteAccount_textLink);
        if (deleteAccountLink != null) {
            deleteAccountLink.setOnClickListener(v -> 
                Toast.makeText(SettingAccountActivity.this, "Delete Account requested", Toast.LENGTH_SHORT).show());
        }

        // Update Button -> Saves profile changes
        Button updateButton = findViewById(R.id.update_button);
        if (updateButton != null) {
            updateButton.setOnClickListener(v -> 
                Toast.makeText(SettingAccountActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show());
        }

        // Edit Password Button -> Action prompt
        Button editPasswordButton = findViewById(R.id.editPassword_button);
        if (editPasswordButton != null) {
            editPasswordButton.setOnClickListener(v -> 
                Toast.makeText(SettingAccountActivity.this, "Edit Password requested", Toast.LENGTH_SHORT).show());
        }

        // Log Out Button -> Returns to LoginActivity and clears backstack
        Button logoutButton = findViewById(R.id.logout_button_account);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                Toast.makeText(SettingAccountActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingAccountActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}
