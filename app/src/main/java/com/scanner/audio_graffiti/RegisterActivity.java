package com.scanner.audio_graffiti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Handles the Registration Screen (register_page.xml).
 * Connects user navigation to Login and Main Campus Map screens.
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        // Exit button -> Closes screen
        View exitButton = findViewById(R.id.exit_button);
        if (exitButton != null) {
            exitButton.setOnClickListener(v -> finish());
        }

        // "CREATE ACCOUNT" button -> Opens Main Campus Map screen
        View createAccountButton = findViewById(R.id.createAccountButton);
        if (createAccountButton != null) {
            createAccountButton.setOnClickListener(v -> {
                Intent intent = new Intent(RegisterActivity.this, CampusMapActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // "Log in" link -> Navigates to Login screen
        View loginLink = findViewById(R.id.login_textView);
        if (loginLink != null) {
            loginLink.setOnClickListener(v -> {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}
