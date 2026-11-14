package com.scanner.audio_graffiti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.card.MaterialCardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.scanner.audio_graffiti.firebase.FirestoreHelper;
import com.scanner.audio_graffiti.settings.SettingAccountActivity;
import com.scanner.audio_graffiti.settings.SettingHelpActivity;
import com.scanner.audio_graffiti.settings.SettingLocationActivity;
import com.scanner.audio_graffiti.settings.SettingNotificationActivity;
import com.scanner.audio_graffiti.settings.SettingPersonalizationActivity;
import com.scanner.audio_graffiti.settings.SettingPrivacyActivity;

/**
 * Handles the Main Settings Hub Screen (setting_page.xml).
 * Connects user navigation to Account, Personalization, Location, Notifications, Privacy, and Help.
 * Applies user personalization themes dynamically.
 */
public class SettingsActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView emailTextView;
    private FirebaseAuth mAuth;
    private FirestoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_page);

        mAuth = FirebaseAuth.getInstance();
        firestoreHelper = new FirestoreHelper();

        usernameTextView = findViewById(R.id.username_textView);
        emailTextView = findViewById(R.id.email_textView);

        loadUserData();
        applyPersonalizationTheme();

        // Edit Profile Button -> Opens Account Settings page (setting_account_page.xml)
        View editProfileButton = findViewById(R.id.editProfile_button);
        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(v -> 
                startActivity(new Intent(SettingsActivity.this, SettingAccountActivity.class)));
        }

        // 1. Personalization Option (setting_personalization.xml)
        View optionPersonalization = findViewById(R.id.option_personalization);
        if (optionPersonalization != null) {
            optionPersonalization.setOnClickListener(v -> 
                startActivity(new Intent(SettingsActivity.this, SettingPersonalizationActivity.class)));
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
                mAuth.signOut();
                Toast.makeText(SettingsActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        applyPersonalizationTheme();
    }

    private void applyPersonalizationTheme() {
        SharedPreferences prefs = getSharedPreferences("personalization_prefs", MODE_PRIVATE);

        int darkMode = prefs.getInt("dark_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        boolean isAmoled = prefs.getBoolean("amoled_mode", false);
        boolean isEink = prefs.getBoolean("eink_mode", false);
        String hexColor = prefs.getString("color_hex", "#FFE082");

        boolean isDarkSystem = (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        boolean isDark = darkMode == AppCompatDelegate.MODE_NIGHT_YES || (darkMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM && isDarkSystem);

        int bgColor;
        int cardColor;
        int textColor;
        int subtextColor;

        if (isEink) {
            bgColor = Color.WHITE;
            cardColor = Color.parseColor("#E0E0E0");
            textColor = Color.BLACK;
            subtextColor = Color.DKGRAY;
        } else if (isAmoled && isDark) {
            bgColor = Color.BLACK; // Pure Pitch Black
            cardColor = Color.parseColor("#121212");
            textColor = Color.WHITE;
            subtextColor = Color.parseColor("#B0B0B0");
        } else if (isDark) {
            bgColor = Color.parseColor("#121212"); // Dark Charcoal
            cardColor = Color.parseColor("#1E1E24");
            textColor = Color.WHITE;
            subtextColor = Color.parseColor("#A0A0A0");
        } else {
            bgColor = Color.parseColor("#FAFAFC"); // Light
            cardColor = Color.parseColor(hexColor);
            textColor = Color.parseColor("#121212");
            subtextColor = Color.parseColor("#555555");
        }

        View rootView = findViewById(R.id.root_layout);
        View scrollView = findViewById(R.id.scroll_view);
        if (rootView != null) rootView.setBackgroundColor(bgColor);
        if (scrollView != null) scrollView.setBackgroundColor(bgColor);

        TextView title = findViewById(R.id.settings_title);
        TextView sectionHeader = findViewById(R.id.account_section_header);
        if (title != null) title.setTextColor(textColor);
        if (sectionHeader != null) sectionHeader.setTextColor(subtextColor);

        if (usernameTextView != null) usernameTextView.setTextColor(textColor);
        if (emailTextView != null) emailTextView.setTextColor(subtextColor);

        // Edit Profile Button & Logout Button Text Colors for Dark Mode visibility
        Button editProfileBtn = findViewById(R.id.editProfile_button);
        Button logoutBtn = findViewById(R.id.logout_button);
        if (editProfileBtn != null) editProfileBtn.setTextColor(textColor);
        if (logoutBtn != null) logoutBtn.setTextColor(textColor);

        // Apply Option Card Colors
        int[] cardIds = {
            R.id.option_personalization,
            R.id.option_location,
            R.id.option_notifications,
            R.id.option_privacy,
            R.id.option_help
        };

        for (int id : cardIds) {
            MaterialCardView card = findViewById(id);
            if (card != null) card.setCardBackgroundColor(cardColor);
        }

        // Option Text Colors
        int[] titleIds = {
            R.id.title_personalization, R.id.title_location,
            R.id.title_notifications, R.id.title_privacy, R.id.title_help
        };
        for (int id : titleIds) {
            TextView tv = findViewById(id);
            if (tv != null) tv.setTextColor(textColor);
        }

        int[] subIds = {
            R.id.sub_personalization, R.id.sub_location,
            R.id.sub_notifications, R.id.sub_privacy, R.id.sub_help
        };
        for (int id : subIds) {
            TextView tv = findViewById(id);
            if (tv != null) tv.setTextColor(subtextColor);
        }
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth != null ? mAuth.getCurrentUser() : null;
        if (currentUser != null) {
            String email = currentUser.getEmail() != null ? currentUser.getEmail() : "";
            String displayName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User";

            if (emailTextView != null && !TextUtils.isEmpty(email)) {
                emailTextView.setText(email);
            }
            if (usernameTextView != null && !TextUtils.isEmpty(displayName) && !"User".equals(displayName)) {
                usernameTextView.setText(displayName);
            }

            // Fetch latest user details from Cloud Firestore
            if (firestoreHelper != null) {
                firestoreHelper.getUserFromFirestore(currentUser.getUid(), documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firestoreName = documentSnapshot.getString("user_name");
                        String firestoreEmail = documentSnapshot.getString("email");

                        if (usernameTextView != null && !TextUtils.isEmpty(firestoreName)) {
                            usernameTextView.setText(firestoreName);
                        }
                        if (emailTextView != null && !TextUtils.isEmpty(firestoreEmail)) {
                            emailTextView.setText(firestoreEmail);
                        }
                    }
                });
            }
        }
    }
}
