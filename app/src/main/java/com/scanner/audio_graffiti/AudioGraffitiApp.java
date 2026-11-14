package com.scanner.audio_graffiti;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Global Application class for Audio Graffiti.
 * Initializes default theme preferences and night mode state on startup.
 */
public class AudioGraffitiApp extends Application {

    private static final String PREFS_NAME = "personalization_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    @Override
    public void onCreate() {
        super.onCreate();

        // Apply saved dark mode theme globally on startup
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int darkMode = prefs.getInt(KEY_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(darkMode);
    }
}
