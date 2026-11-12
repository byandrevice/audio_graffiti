package com.scanner.audio_graffiti.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.scanner.audio_graffiti.LoginActivity;
import com.scanner.audio_graffiti.R;
import com.scanner.audio_graffiti.firebase.FirestoreHelper;

/**
 * Handles Account Profile Screen (setting_account_page.xml).
 * Connects Firebase Auth & Cloud Firestore for profile updates, in-app password changes, account deletion, and log out.
 */
public class SettingAccountActivity extends AppCompatActivity {

    private static final String TAG = "SettingAccountActivity";
    private EditText userNameEdit;
    private EditText emailEdit;
    private TextView usernameTextView;
    private TextView emailTextView;

    private FirebaseAuth mAuth;
    private FirestoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account_page);

        mAuth = FirebaseAuth.getInstance();
        firestoreHelper = new FirestoreHelper();

        userNameEdit = findViewById(R.id.userName_edit);
        emailEdit = findViewById(R.id.email_edit);
        usernameTextView = findViewById(R.id.username_textView);
        emailTextView = findViewById(R.id.email_textView);

        // Load Current Firebase User Details
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail() != null ? currentUser.getEmail() : "";
            String displayName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User";

            if (emailEdit != null) emailEdit.setText(email);
            if (emailTextView != null) emailTextView.setText(email);
            if (userNameEdit != null) userNameEdit.setText(displayName);
            if (usernameTextView != null) usernameTextView.setText(displayName);

            // Fetch latest user details from Firestore
            firestoreHelper.getUserFromFirestore(currentUser.getUid(), documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String firestoreName = documentSnapshot.getString("user_name");
                    String firestoreEmail = documentSnapshot.getString("email");

                    if (!TextUtils.isEmpty(firestoreName)) {
                        if (userNameEdit != null) userNameEdit.setText(firestoreName);
                        if (usernameTextView != null) usernameTextView.setText(firestoreName);
                    }
                    if (!TextUtils.isEmpty(firestoreEmail)) {
                        if (emailEdit != null) emailEdit.setText(firestoreEmail);
                        if (emailTextView != null) emailTextView.setText(firestoreEmail);
                    }
                }
            });
        }

        // Circular Back Button -> Returns to Settings main screen
        View backButton = findViewById(R.id.back_button_card);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        // Edit Avatar Badge -> Action prompt
        View editAvatarBadge = findViewById(R.id.edit_avatar_badge);
        if (editAvatarBadge != null) {
            editAvatarBadge.setOnClickListener(v -> 
                Toast.makeText(SettingAccountActivity.this, "Avatar customization coming soon", Toast.LENGTH_SHORT).show());
        }

        // Delete Account Link -> Confirmation Dialog & Deletion
        TextView deleteAccountLink = findViewById(R.id.deleteAccount_textLink);
        if (deleteAccountLink != null) {
            deleteAccountLink.setOnClickListener(v -> showDeleteAccountDialog());
        }

        // Update Button -> Saves profile changes to Firebase Auth & Cloud Firestore
        Button updateButton = findViewById(R.id.update_button);
        if (updateButton != null) {
            updateButton.setOnClickListener(v -> handleProfileUpdate());
        }

        // Edit Password Button -> Opens In-App Password Change Dialog
        Button editPasswordButton = findViewById(R.id.editPassword_button);
        if (editPasswordButton != null) {
            editPasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        }

        // Log Out Button -> Signs out and clears backstack
        Button logoutButton = findViewById(R.id.logout_button_account);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> handleLogout());
        }
    }

    private void handleProfileUpdate() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String updatedName = userNameEdit != null ? userNameEdit.getText().toString().trim() : "";
        String updatedEmail = emailEdit != null ? emailEdit.getText().toString().trim() : "";

        if (TextUtils.isEmpty(updatedName)) {
            if (userNameEdit != null) userNameEdit.setError("Name cannot be empty");
            return;
        }

        if (TextUtils.isEmpty(updatedEmail)) {
            if (emailEdit != null) emailEdit.setError("Email cannot be empty");
            return;
        }

        // Update Firebase Auth Display Name
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(updatedName)
                .build();

        currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User profile display name updated.");
            }
        });

        // Save updates to Firestore
        firestoreHelper.updateUserInFirestore(currentUser.getUid(), updatedName, updatedEmail);

        if (usernameTextView != null) usernameTextView.setText(updatedName);
        if (emailTextView != null) emailTextView.setText(updatedEmail);

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
    }

    private void showChangePasswordDialog() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Container layout
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int paddingPx = (int) (20 * getResources().getDisplayMetrics().density);
        container.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

        // New Password Field
        final EditText newPasswordInput = new EditText(this);
        newPasswordInput.setHint("New Password (min 6 chars)");
        newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(newPasswordInput);

        // Confirm Password Field
        final EditText confirmPasswordInput = new EditText(this);
        confirmPasswordInput.setHint("Confirm New Password");
        confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) (12 * getResources().getDisplayMetrics().density);
        confirmPasswordInput.setLayoutParams(params);
        container.addView(confirmPasswordInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Password")
                .setView(container)
                .setPositiveButton("Save Password", null)
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();

        dialog.show();

        // Custom listener so dialog stays open on validation error
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (TextUtils.isEmpty(newPassword)) {
                newPasswordInput.setError("Please enter a new password");
                return;
            }

            if (newPassword.length() < 6) {
                newPasswordInput.setError("Password must be at least 6 characters");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                return;
            }

            currentUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SettingAccountActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Exception e = task.getException();
                    String errorMsg = e != null ? e.getMessage() : "Password update failed";
                    Log.e(TAG, "Update password failed", e);
                    Toast.makeText(SettingAccountActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account and all data? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        firestoreHelper.deleteUserFromFirestore(uid);

                        currentUser.delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingAccountActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                handleLogout();
                            } else {
                                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Account deletion failed";
                                Toast.makeText(SettingAccountActivity.this, "Failed to delete account: " + errorMsg, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleLogout() {
        mAuth.signOut();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingAccountActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
