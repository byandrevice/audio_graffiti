package com.scanner.audio_graffiti.settings;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.scanner.audio_graffiti.R;

/**
 * Handles Personalization Sub-Settings Screen (setting_personalization.xml).
 * Customizes app theme, dark mode, accent colors, background style, font size, and display preferences.
 */
public class SettingPersonalizationActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "personalization_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_COLOR_THEME = "color_theme";
    private static final String KEY_COLOR_HEX = "color_hex";
    private static final String KEY_BG_OPTION = "bg_option";
    private static final String KEY_FONT_SIZE = "font_size";

    private MaterialCardView btnModeSystem;
    private MaterialCardView btnModeLight;
    private MaterialCardView btnModeDark;

    private MaterialCardView badgeColorSwatch;
    private TextView textSelectedColor;
    private TextView textSelectedBg;
    private TextView textSelectedFont;

    private MaterialSwitch switchAdaptive;
    private MaterialSwitch switchAmoled;
    private MaterialSwitch switchEink;
    private MaterialSwitch switchGlass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_personalization);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Circular Back Button -> Returns to Settings main screen
        View backButton = findViewById(R.id.back_button_card);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        // Dark Mode Selector Buttons
        btnModeSystem = findViewById(R.id.btn_mode_system);
        btnModeLight = findViewById(R.id.btn_mode_light);
        btnModeDark = findViewById(R.id.btn_mode_dark);

        if (btnModeSystem != null) {
            btnModeSystem.setOnClickListener(v -> setAppDarkMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, "System Default"));
        }
        if (btnModeLight != null) {
            btnModeLight.setOnClickListener(v -> setAppDarkMode(AppCompatDelegate.MODE_NIGHT_NO, "Light Mode"));
        }
        if (btnModeDark != null) {
            btnModeDark.setOnClickListener(v -> setAppDarkMode(AppCompatDelegate.MODE_NIGHT_YES, "Dark Mode"));
        }

        // Accent Color Theme Picker Card
        badgeColorSwatch = findViewById(R.id.badge_color_swatch);
        textSelectedColor = findViewById(R.id.text_selected_color);
        View cardColorTheme = findViewById(R.id.card_color_theme);

        if (cardColorTheme != null) {
            cardColorTheme.setOnClickListener(v -> showColorThemePicker());
        }

        // Background Option Card
        textSelectedBg = findViewById(R.id.text_selected_bg);
        View cardBackground = findViewById(R.id.card_background_option);

        if (cardBackground != null) {
            cardBackground.setOnClickListener(v -> showBackgroundPicker());
        }

        // Font and Size Option Card
        textSelectedFont = findViewById(R.id.text_selected_font);
        View cardFontSize = findViewById(R.id.card_font_size);

        if (cardFontSize != null) {
            cardFontSize.setOnClickListener(v -> showFontSizePicker());
        }

        // Material Switches
        switchAdaptive = findViewById(R.id.switch_adaptive);
        switchAmoled = findViewById(R.id.switch_amoled);
        switchEink = findViewById(R.id.switch_eink);
        switchGlass = findViewById(R.id.switch_glass);

        if (switchAdaptive != null) {
            switchAdaptive.setChecked(prefs.getBoolean("adaptive_colors", true));
            switchAdaptive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("adaptive_colors", isChecked).apply();
                Toast.makeText(this, isChecked ? "Adaptive colors enabled" : "Adaptive colors disabled", Toast.LENGTH_SHORT).show();
                applyPersonalizationUI();
            });
        }

        if (switchAmoled != null) {
            switchAmoled.setChecked(prefs.getBoolean("amoled_mode", false));
            switchAmoled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("amoled_mode", isChecked).apply();
                Toast.makeText(this, isChecked ? "AMOLED pure black enabled" : "AMOLED mode disabled", Toast.LENGTH_SHORT).show();
                applyPersonalizationUI();
            });
        }

        if (switchEink != null) {
            switchEink.setChecked(prefs.getBoolean("eink_mode", false));
            switchEink.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("eink_mode", isChecked).apply();
                Toast.makeText(this, isChecked ? "E-Ink high contrast enabled" : "E-Ink mode disabled", Toast.LENGTH_SHORT).show();
                applyPersonalizationUI();
            });
        }

        if (switchGlass != null) {
            switchGlass.setChecked(prefs.getBoolean("liquid_glass", false));
            switchGlass.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("liquid_glass", isChecked).apply();
                Toast.makeText(this, isChecked ? "Liquid Glass effect enabled" : "Liquid Glass effect disabled", Toast.LENGTH_SHORT).show();
                applyPersonalizationUI();
            });
        }

        // Apply Personalization Styling to Current UI
        applyPersonalizationUI();
    }

    private void applyPersonalizationUI() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        int darkMode = prefs.getInt(KEY_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        boolean isAmoled = prefs.getBoolean("amoled_mode", false);
        boolean isEink = prefs.getBoolean("eink_mode", false);
        String savedColor = prefs.getString(KEY_COLOR_THEME, "UCF Gold Theme");
        String savedHex = prefs.getString(KEY_COLOR_HEX, "#FDC804");
        String savedBg = prefs.getString(KEY_BG_OPTION, "None");
        String savedFont = prefs.getString(KEY_FONT_SIZE, "Default (100%)");

        // Update Subtitle Texts
        if (textSelectedColor != null) textSelectedColor.setText(savedColor);
        if (textSelectedBg != null) textSelectedBg.setText(savedBg);
        if (textSelectedFont != null) textSelectedFont.setText(savedFont);

        int accentColor = Color.parseColor(savedHex);
        if (badgeColorSwatch != null) badgeColorSwatch.setCardBackgroundColor(accentColor);

        // Determine Theme Colors
        int bgColor;
        int cardBgColor;
        int textColor;
        int subtextColor;

        boolean isDarkSystem = (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        boolean isDark = darkMode == AppCompatDelegate.MODE_NIGHT_YES || (darkMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM && isDarkSystem);

        if (isEink) {
            bgColor = Color.WHITE;
            cardBgColor = Color.parseColor("#E0E0E0");
            textColor = Color.BLACK;
            subtextColor = Color.DKGRAY;
        } else if (isAmoled && isDark) {
            bgColor = Color.BLACK; // Pure Pitch Black for AMOLED
            cardBgColor = Color.parseColor("#121212");
            textColor = Color.WHITE;
            subtextColor = Color.parseColor("#B0B0B0");
        } else if (isDark) {
            bgColor = Color.parseColor("#121212"); // Dark Charcoal
            cardBgColor = Color.parseColor("#1E1E24");
            textColor = Color.WHITE;
            subtextColor = Color.parseColor("#A0A0A0");
        } else {
            bgColor = Color.parseColor("#FAFAFC"); // Light
            cardBgColor = Color.parseColor("#F1F2F6");
            textColor = Color.parseColor("#121212");
            subtextColor = Color.parseColor("#757575");
        }

        // Apply Background Color to Root & ScrollView
        View rootView = findViewById(R.id.root_layout);
        View scrollView = findViewById(R.id.scroll_view);
        if (rootView != null) rootView.setBackgroundColor(bgColor);
        if (scrollView != null) scrollView.setBackgroundColor(bgColor);

        // Apply Header Text Colors
        int[] headerIds = {R.id.screen_title, R.id.section_interface_header, R.id.section_screen_header, R.id.section_text_header};
        for (int id : headerIds) {
            TextView tv = findViewById(id);
            if (tv != null) tv.setTextColor(textColor);
        }

        // Apply Primary Label Text Colors
        int[] labelIds = {
            R.id.text_label_color, R.id.text_label_dark_mode, R.id.text_label_background,
            R.id.text_label_adaptive, R.id.text_label_amoled, R.id.text_label_eink,
            R.id.text_label_glass, R.id.text_label_font
        };
        for (int id : labelIds) {
            TextView tv = findViewById(id);
            if (tv != null) tv.setTextColor(textColor);
        }

        // Apply Subtext Colors
        int[] subtextIds = {
            R.id.text_selected_color, R.id.text_selected_bg, R.id.text_selected_font,
            R.id.text_sub_adaptive, R.id.text_sub_amoled, R.id.text_sub_eink, R.id.text_sub_glass
        };
        for (int id : subtextIds) {
            TextView tv = findViewById(id);
            if (tv != null) tv.setTextColor(subtextColor);
        }

        // Apply Card Background Colors
        int[] cardIds = {
            R.id.card_color_theme, R.id.card_dark_mode, R.id.card_background_option,
            R.id.card_adaptive, R.id.card_amoled, R.id.card_eink, R.id.card_glass, R.id.card_font_size
        };
        for (int id : cardIds) {
            MaterialCardView card = findViewById(id);
            if (card != null) card.setCardBackgroundColor(cardBgColor);
        }

        // Update Dark Mode Button Highlights
        updateDarkModeUI(darkMode);

        // Calculate Font Scale
        float fontScale = 1.0f;
        if (savedFont.contains("85%")) fontScale = 0.85f;
        else if (savedFont.contains("115%")) fontScale = 1.15f;
        else if (savedFont.contains("130%")) fontScale = 1.30f;

        TextView titleView = findViewById(R.id.screen_title);
        if (titleView != null) {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28 * fontScale);
        }
    }

    private void setAppDarkMode(int mode, String label) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_DARK_MODE, mode).apply();
        AppCompatDelegate.setDefaultNightMode(mode);
        applyPersonalizationUI();
        Toast.makeText(this, "Theme set to " + label, Toast.LENGTH_SHORT).show();
    }

    private void updateDarkModeUI(int mode) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedHex = prefs.getString(KEY_COLOR_HEX, "#FDC804");
        int activeColor = Color.parseColor(savedHex);
        int inactiveColor = Color.parseColor("#E0E0E5");

        if (btnModeSystem != null) {
            btnModeSystem.setCardBackgroundColor(mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM ? activeColor : inactiveColor);
        }
        if (btnModeLight != null) {
            btnModeLight.setCardBackgroundColor(mode == AppCompatDelegate.MODE_NIGHT_NO ? activeColor : inactiveColor);
        }
        if (btnModeDark != null) {
            btnModeDark.setCardBackgroundColor(mode == AppCompatDelegate.MODE_NIGHT_YES ? activeColor : inactiveColor);
        }
    }

    private void showColorThemePicker() {
        final String[] colors = {"UCF Gold Theme", "Ocean Blue Theme", "Emerald Green Theme", "Sunset Crimson Theme", "Royal Purple Theme"};
        final String[] hexes = {"#FDC804", "#3498DB", "#2ECC71", "#E74C3C", "#9B59B6"};

        new AlertDialog.Builder(this)
                .setTitle("Select Accent Color Theme")
                .setItems(colors, (dialog, which) -> {
                    String selectedName = colors[which];
                    String selectedHex = hexes[which];

                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    prefs.edit()
                            .putString(KEY_COLOR_THEME, selectedName)
                            .putString(KEY_COLOR_HEX, selectedHex)
                            .apply();

                    applyPersonalizationUI();
                    Toast.makeText(SettingPersonalizationActivity.this, "Accent color set to " + selectedName, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showBackgroundPicker() {
        final String[] bgOptions = {"None (Solid)", "UCF Campus Map Pattern", "Subtle Grid Pattern", "Blurred Audio Waveform"};

        new AlertDialog.Builder(this)
                .setTitle("Select Background Style")
                .setItems(bgOptions, (dialog, which) -> {
                    String selected = bgOptions[which];
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            .edit()
                            .putString(KEY_BG_OPTION, selected)
                            .apply();

                    applyPersonalizationUI();
                    Toast.makeText(SettingPersonalizationActivity.this, "Background set to " + selected, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showFontSizePicker() {
        final String[] fontOptions = {"Small (85%)", "Default (100%)", "Large (115%)", "Extra Large (130%)"};

        new AlertDialog.Builder(this)
                .setTitle("Select Font Size")
                .setItems(fontOptions, (dialog, which) -> {
                    String selected = fontOptions[which];
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            .edit()
                            .putString(KEY_FONT_SIZE, selected)
                            .apply();

                    applyPersonalizationUI();
                    Toast.makeText(SettingPersonalizationActivity.this, "Font size set to " + selected, Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
