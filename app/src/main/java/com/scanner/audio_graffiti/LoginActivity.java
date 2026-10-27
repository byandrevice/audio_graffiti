package com.scanner.audio_graffiti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Handles the Login Screen (login_page.xml).
 * Connects user navigation to Registration and Main Campus Map screens.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Exit button -> Closes screen
        View exitButton = findViewById(R.id.exit_button);
        if (exitButton != null) {
            exitButton.setOnClickListener(v -> finish());
        }

        // Log In button -> Opens Main Campus Map screen
        View loginButton = findViewById(R.id.createAccountButton);
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, CampusMapActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // "Create an account" link -> Navigates to Register screen
        View createAccountLink = findViewById(R.id.login_textView);
        if (createAccountLink != null) {
            createAccountLink.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
    }
}
