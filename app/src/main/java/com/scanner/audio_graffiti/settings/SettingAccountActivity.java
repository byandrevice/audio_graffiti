package com.scanner.audio_graffiti.settings;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.scanner.audio_graffiti.R;

/**
 * Handles Account Sub-Settings Screen (setting_account_page.xml).
 */
public class SettingAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account_page);

        // Circular Back Button -> Returns to Settings main screen
        View backButton = findViewById(R.id.back_button_card);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }
}
