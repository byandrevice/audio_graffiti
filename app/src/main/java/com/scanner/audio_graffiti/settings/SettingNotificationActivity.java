package com.scanner.audio_graffiti.settings;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.scanner.audio_graffiti.R;
import com.scanner.audio_graffiti.ui.ThemeUtils;

/**
 * Handles Notifications Sub-Settings Screen (setting_notification_page.xml).
 * Configures push notifications for chat, updates, nearby alerts, replies, and weekly digests.
 * Features live testing with real Android system notifications.
 */
public class SettingNotificationActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "notification_prefs";
    private static final String CHANNEL_ID = "audio_graffiti_notifications";

    private MaterialSwitch switchChat;
    private MaterialSwitch switchUpdates;
    private MaterialSwitch switchNearby;
    private MaterialSwitch switchReplies;
    private MaterialSwitch switchDigest;

    private final ActivityResultLauncher<String> notificationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (Boolean.TRUE.equals(isGranted)) {
                    Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
                    sendLiveNotification("Audio Graffiti Alerts Enabled", "You will now receive live campus voice drop updates!");
                } else {
                    Toast.makeText(this, "Notification permission denied. Enable in Android Settings.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_notification_page);

        ThemeUtils.applyTheme(this);

        createNotificationChannel();

        // Circular Back Button -> Returns to Settings main screen
        View backButton = findViewById(R.id.back_button_card);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        switchChat = findViewById(R.id.switch_chat);
        switchUpdates = findViewById(R.id.switch_updates);
        switchNearby = findViewById(R.id.switch_nearby);
        switchReplies = findViewById(R.id.switch_replies);
        switchDigest = findViewById(R.id.switch_digest);

        setupSwitch(switchChat, "notify_chat", true, "Chat & Comments");
        setupSwitch(switchUpdates, "notify_updates", true, "App Updates");
        setupSwitch(switchNearby, "notify_nearby", true, "Campus Nearby Alerts");
        setupSwitch(switchReplies, "notify_replies", true, "Replies & Mentions");
        setupSwitch(switchDigest, "notify_digest", false, "Weekly Campus Digest");

        // "SEND TEST NOTIFICATION" Button -> Fires live system notification
        Button testNotificationBtn = findViewById(R.id.btn_test_notification);
        if (testNotificationBtn != null) {
            testNotificationBtn.setOnClickListener(v -> checkPermissionAndSendTestNotification());
        }
    }

    private void setupSwitch(MaterialSwitch matSwitch, String prefKey, boolean defaultValue, String label) {
        if (matSwitch == null) return;
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        matSwitch.setChecked(prefs.getBoolean(prefKey, defaultValue));
        matSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(prefKey, isChecked).apply();
            Toast.makeText(this, isChecked ? label + " notifications enabled" : label + " notifications disabled", Toast.LENGTH_SHORT).show();
            
            if (isChecked) {
                checkPermissionAndSendNotification(label + " Activated", "You will now receive " + label + " alerts on your device.");
            }
        });
    }

    private void checkPermissionAndSendTestNotification() {
        checkPermissionAndSendNotification("🎧 UCF Voice Drop Nearby!", "New voice graffiti posted near Memory Mall! Tap to listen.");
    }

    private void checkPermissionAndSendNotification(String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }
        sendLiveNotification(title, message);
    }

    private void sendLiveNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());

        Toast.makeText(this, "Live test notification sent to status bar!", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Audio Graffiti Alerts";
            String description = "Campus voice drop alerts and mentions";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeUtils.applyTheme(this);
    }
}
