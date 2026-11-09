package com.scanner.audio_graffiti.settings;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.scanner.audio_graffiti.R;

/**
 * Handles Personalization Sub-Settings Screen (setting_personalization.xml).
 * Customizes app theme, dark mode, colors, and display preferences.
 */
public class SettingPersonalizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_personalization);

        // Circular Back Button -> Returns to Settings main screen
        View backButton = findViewById(R.id.back_button_card);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }
}
