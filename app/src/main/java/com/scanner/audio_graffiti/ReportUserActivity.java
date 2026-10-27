package com.scanner.audio_graffiti;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Handles the Report User Screen (report_user_page.xml).
 * Connects back, cancel, and submit report actions.
 */
public class ReportUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_user_page);

        // Back arrow button -> Closes report screen
        View backButton = findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        // Cancel button -> Closes report screen
        View cancelButton = findViewById(R.id.cancel_button);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(v -> finish());
        }

        // Submit Report button -> Closes report screen
        View submitButton = findViewById(R.id.submitReport_button);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> finish());
        }
    }
}
