package com.scanner.audio_graffiti.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.card.MaterialCardView;
import com.scanner.audio_graffiti.R;

/**
 * Utility helper to apply personalization dark mode, AMOLED mode, and colors cleanly across all activities without breaking contrast.
 */
public class ThemeUtils {

    private static final String PREFS_NAME = "personalization_prefs";

    public static void applyTheme(Activity activity) {
        if (activity == null) return;

        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        int darkMode = prefs.getInt("dark_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        boolean isAmoled = prefs.getBoolean("amoled_mode", false);
        boolean isEink = prefs.getBoolean("eink_mode", false);

        boolean isDarkSystem = (activity.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        boolean isDark = darkMode == AppCompatDelegate.MODE_NIGHT_YES || (darkMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM && isDarkSystem);

        View rootView = activity.findViewById(android.R.id.content);
        if (rootView == null) return;

        if (isEink) {
            rootView.setBackgroundColor(Color.WHITE);
            styleViewGroupDark(rootView, Color.WHITE, Color.parseColor("#E0E0E0"), Color.BLACK, Color.DKGRAY);
        } else if (isAmoled && isDark) {
            rootView.setBackgroundColor(Color.BLACK); // Pitch Black for AMOLED
            styleViewGroupDark(rootView, Color.BLACK, Color.parseColor("#121212"), Color.WHITE, Color.parseColor("#B0B0B0"));
        } else if (isDark) {
            rootView.setBackgroundColor(Color.parseColor("#121212")); // Dark Charcoal
            styleViewGroupDark(rootView, Color.parseColor("#121212"), Color.parseColor("#1E1E24"), Color.WHITE, Color.parseColor("#A0A0A0"));
        } else {
            // Light Mode: Clean light background, preserve original card and text contrast
            rootView.setBackgroundColor(Color.parseColor("#FAFAFC"));
            styleViewGroupLight(rootView, Color.parseColor("#FAFAFC"));
        }
    }

    private static void styleViewGroupDark(View view, int bgColor, int cardBgColor, int textColor, int subtextColor) {
        if (view == null) return;

        // Change background of ScrollViews and layout containers so no white backgrounds remain
        if (view instanceof ScrollView) {
            view.setBackgroundColor(bgColor);
        } else if (view instanceof ViewGroup && !(view instanceof MaterialCardView) && !(view instanceof Button)) {
            if (view.getBackground() != null) {
                view.setBackgroundColor(bgColor);
            }
        }

        if (view instanceof MaterialCardView) {
            MaterialCardView card = (MaterialCardView) view;
            int id = card.getId();
            if (id != R.id.back_button_card && id != R.id.edit_avatar_badge && id != R.id.badge_color_swatch
                    && id != R.id.btn_mode_system && id != R.id.btn_mode_light && id != R.id.btn_mode_dark) {
                card.setCardBackgroundColor(cardBgColor);
            }
        } else if (view instanceof EditText) {
            EditText et = (EditText) view;
            et.setTextColor(textColor);
            et.setHintTextColor(subtextColor);
        } else if (view instanceof Button) {
            Button btn = (Button) view;
            btn.setTextColor(textColor);
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            if (tv.getId() != R.id.deleteAccount_textLink) {
                tv.setTextColor(textColor);
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                styleViewGroupDark(vg.getChildAt(i), bgColor, cardBgColor, textColor, subtextColor);
            }
        }
    }

    private static void styleViewGroupLight(View view, int bgColor) {
        if (view == null) return;

        if (view instanceof ScrollView) {
            view.setBackgroundColor(bgColor);
        } else if (view instanceof ViewGroup && !(view instanceof MaterialCardView) && !(view instanceof Button)) {
            if (view.getBackground() != null) {
                view.setBackgroundColor(bgColor);
            }
        }

        if (view instanceof EditText) {
            EditText et = (EditText) view;
            et.setTextColor(Color.parseColor("#121212"));
            et.setHintTextColor(Color.parseColor("#757575"));
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            if (tv.getId() != R.id.deleteAccount_textLink) {
                tv.setTextColor(Color.parseColor("#121212"));
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                styleViewGroupLight(vg.getChildAt(i), bgColor);
            }
        }
    }
}
