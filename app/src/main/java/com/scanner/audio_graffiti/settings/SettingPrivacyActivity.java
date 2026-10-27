package com.scanner.audio_graffiti.settings;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.scanner.audio_graffiti.R;

/**
 * Handles Privacy & Safety Sub-Settings Screen (setting_privacy_page.xml).
 */
public class SettingPrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_privacy_page);

        // Circular Back Button -> Returns to Settings main screen
        View backButton = findViewById(R.id.back_button_card);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }
}
