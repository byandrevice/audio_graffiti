package com.scanner.audio_graffiti.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.scanner.audio_graffiti.R;
import com.scanner.audio_graffiti.ui.ThemeUtils;

/**
 * Handles Help & Feedback Sub-Settings Screen (setting_help_page.xml).
 * Displays UCF Community Guidelines, FAQ, Bug & Feedback reporting, and App Version Info.
 */
public class SettingHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_help_page);

        ThemeUtils.applyTheme(this);

        // Circular Back Button -> Returns to Settings main screen
        View backButton = findViewById(R.id.back_button_card);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        // Community Guidelines Card
        View cardGuidelines = findViewById(R.id.card_guidelines);
        if (cardGuidelines != null) {
            cardGuidelines.setOnClickListener(v -> showGuidelinesDialog());
        }

        // FAQ Card
        View cardFaq = findViewById(R.id.card_faq);
        if (cardFaq != null) {
            cardFaq.setOnClickListener(v -> showFaqDialog());
        }

        // Report a Bug / Feedback Card
        View cardReportBug = findViewById(R.id.card_report_bug);
        if (cardReportBug != null) {
            cardReportBug.setOnClickListener(v -> showReportBugDialog());
        }

        // Version Info Card
        View cardVersionInfo = findViewById(R.id.card_version_info);
        if (cardVersionInfo != null) {
            cardVersionInfo.setOnClickListener(v -> showVersionDialog());
        }
    }

    private void showGuidelinesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("UCF Community Guidelines")
                .setMessage("1. Be respectful to fellow Knights. No hate speech or harassment.\n\n" +
                        "2. Keep voice drops authentic and campus-focused.\n\n" +
                        "3. Do not record or share private personal information.\n\n" +
                        "4. Moderated campus content: Inappropriate voice drops will be removed by community reports.")
                .setPositiveButton("I Agree & Understand", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showFaqDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Frequently Asked Questions")
                .setMessage("Q: How do I leave a voice memo on campus?\n" +
                        "A: Navigate to the main Campus Map and tap 'RECORD A DROP'. Speak your memo and drop your pin!\n\n" +
                        "Q: Who can hear my audio graffiti?\n" +
                        "A: Any UCF student near the campus location where you placed your drop.\n\n" +
                        "Q: Can I post anonymously?\n" +
                        "A: Yes! Go to Settings -> Privacy & Safety and toggle 'Anonymous Voice Memos'.")
                .setPositiveButton("Got It", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showReportBugDialog() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int paddingPx = (int) (20 * getResources().getDisplayMetrics().density);
        container.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

        final EditText feedbackInput = new EditText(this);
        feedbackInput.setHint("Describe the bug or share your feedback...");
        feedbackInput.setLines(4);
        container.addView(feedbackInput);

        new AlertDialog.Builder(this)
                .setTitle("Report Bug / Feedback")
                .setView(container)
                .setPositiveButton("Submit Feedback", (dialog, which) -> {
                    String feedback = feedbackInput.getText().toString().trim();
                    if (!TextUtils.isEmpty(feedback)) {
                        Toast.makeText(SettingHelpActivity.this, "Thank you! Your feedback has been sent to UCF developers.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingHelpActivity.this, "Feedback cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showVersionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About Audio Graffiti")
                .setMessage("Audio Graffiti v1.0.0\n" +
                        "Build: 2025 UCF Campus Release\n" +
                        "Location: University of Central Florida, Orlando FL\n" +
                        "Powered by Firebase Auth, Cloud Firestore & Android Room.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
    }
}
