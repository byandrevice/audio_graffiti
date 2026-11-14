package com.scanner.audio_graffiti;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.scanner.audio_graffiti.ui.ThemeUtils;

/**
 * Handles the Login Screen (login_page.xml).
 * Performs Firebase Authentication and navigates to the Campus Map screen.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        ThemeUtils.applyTheme(this);

        mAuth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.mail_hint);
        passwordInput = findViewById(R.id.pass_hint);

        // Exit button -> Closes screen
        View exitButton = findViewById(R.id.exit_button);
        if (exitButton != null) {
            exitButton.setOnClickListener(v -> finish());
        }

        // "LOG IN" button -> Authenticates with Firebase Auth
        Button loginButton = findViewById(R.id.loginButton);
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                String email = emailInput != null ? emailInput.getText().toString().trim() : "";
                String password = passwordInput != null ? passwordInput.getText().toString().trim() : "";

                if (TextUtils.isEmpty(email)) {
                    if (emailInput != null) emailInput.setError("Please enter your UCF email");
                    Toast.makeText(LoginActivity.this, "Please enter your UCF email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    if (passwordInput != null) passwordInput.setError("Please enter your password");
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Authenticate with Firebase Auth
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, CampusMapActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMsg = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                            Toast.makeText(LoginActivity.this, "Login Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
            });
        }

        // "Continue with Google" button -> Opens Campus Map screen
        View googleButton = findViewById(R.id.googleLogin_button);
        if (googleButton != null) {
            googleButton.setOnClickListener(v -> {
                Toast.makeText(LoginActivity.this, "Signed in with Google", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, CampusMapActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // "Create an account" link -> Opens Register screen
        View createAccountLink = findViewById(R.id.login_textView);
        if (createAccountLink != null) {
            createAccountLink.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
    }
}
