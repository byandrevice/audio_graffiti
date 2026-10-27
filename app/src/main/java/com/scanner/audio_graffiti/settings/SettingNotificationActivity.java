package com.scanner.audio_graffiti.settings;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.scanner.audio_graffiti.R;

/**
 * Handles Notifications Sub-Settings Screen (setting_notification_page.xml).
 */
public class SettingNotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_notification_page);

        // Circular Back Button -> Returns to Settings main screen
        View backButton = findViewById(R.id.back_button_card);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }
}
