package com.scanner.audio_graffiti;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.scanner.audio_graffiti.database.entity.User;
import com.scanner.audio_graffiti.firebase.FirestoreHelper;
import com.scanner.audio_graffiti.ui.ThemeUtils;

/**
 * Handles the Registration Screen (register_page.xml).
 * Registers users with Firebase Authentication and saves user data to Cloud Firestore.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText emailInput;
    private EditText displayNameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private CheckBox termsCheckbox;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        ThemeUtils.applyTheme(this);

        mAuth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.mail_hint);
        displayNameInput = findViewById(R.id.display_hint);
        passwordInput = findViewById(R.id.pass_hint);
        confirmPasswordInput = findViewById(R.id.cPass_hint);
        termsCheckbox = findViewById(R.id.terms_checkbox);

        // Exit button -> Closes current screen
        View exitButton = findViewById(R.id.exit_button);
        if (exitButton != null) {
            exitButton.setOnClickListener(v -> finish());
        }

        // "CREATE ACCOUNT" button -> Registers with Firebase Auth & saves to Firestore
        Button createAccountButton = findViewById(R.id.createAccountButton);
        if (createAccountButton != null) {
            createAccountButton.setOnClickListener(v -> {
                String email = emailInput != null ? emailInput.getText().toString().trim() : "";
                String displayName = displayNameInput != null ? displayNameInput.getText().toString().trim() : "";
                String password = passwordInput != null ? passwordInput.getText().toString().trim() : "";
                String confirmPassword = confirmPasswordInput != null ? confirmPasswordInput.getText().toString().trim() : "";

                if (TextUtils.isEmpty(email)) {
                    if (emailInput != null) emailInput.setError("Please enter your UCF email");
                    Toast.makeText(RegisterActivity.this, "Please enter your UCF email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(displayName)) {
                    if (displayNameInput != null) displayNameInput.setError("Please enter a display name");
                    Toast.makeText(RegisterActivity.this, "Please enter a display name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    if (passwordInput != null) passwordInput.setError("Please enter a password");
                    Toast.makeText(RegisterActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    if (passwordInput != null) passwordInput.setError("Password must be at least 6 characters");
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    if (confirmPasswordInput != null) confirmPasswordInput.setError("Passwords do not match");
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (termsCheckbox != null && !termsCheckbox.isChecked()) {
                    Toast.makeText(RegisterActivity.this, "Please agree to the community guidelines", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Register with Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String uid = firebaseUser != null ? firebaseUser.getUid() : "";

                            // Create User entity object
                            String timestamp = String.valueOf(System.currentTimeMillis());
                            User newUser = new User(
                                displayName,    // userName
                                "",             // fname
                                "",             // lname
                                "",             // bio
                                "",             // avatarUrl
                                email,          // email
                                "",             // password
                                false,          // isBanned
                                false,          // isAnonymous
                                timestamp       // createAt
                            );

                            // Save User to Cloud Firestore 'users' collection
                            try {
                                FirestoreHelper firestoreHelper = new FirestoreHelper();
                                firestoreHelper.saveUserToFirestoreWithUid(uid, newUser);
                            } catch (Exception e) {
                                Log.e(TAG, "Error saving user to Firestore: " + e.getMessage(), e);
                            }

                            Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, CampusMapActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Exception e = task.getException();
                            Log.e(TAG, "Sign Up Failed", e);
                            String errorMsg = e != null ? e.getMessage() : "Registration failed";
                            Toast.makeText(RegisterActivity.this, "Sign Up Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
            });
        }

        // "Log in" link -> Opens Login screen
        View loginLink = findViewById(R.id.login_textView);
        if (loginLink != null) {
            loginLink.setOnClickListener(v -> {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
    }
}
