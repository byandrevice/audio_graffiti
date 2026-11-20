package com.scanner.audio_graffiti.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatImageButton;
import com.scanner.audio_graffiti.R;

/**
 * Interactive UCF Campus Map Panel.
 * Renders UCF campus surface, voice drop pins, live GPS user location marker, and Dev Simulation walking controls.
 */
public final class CampusMapPanel extends FrameLayout {

    private static final String TAG = "CampusMapPanel";
    private static final float MAP_WIDTH = 1290f;
    private static final float MAP_HEIGHT = 2064f;

    // UCF Main Campus Bounding Box for GPS Projection
    private static final double UCF_MIN_LAT = 28.5950;
    private static final double UCF_MAX_LAT = 28.6100;
    private static final double UCF_MIN_LNG = -81.2100;
    private static final double UCF_MAX_LNG = -81.1900;

    // Projected landmark pin anchors
    private static final float[][] PIN_ANCHORS = {
        {637.289f, 1004.934f}, // Student Union
        {517.218f, 1244.192f}, // Library
        {809.833f, 743.068f}   // Memory Mall
    };

    private final AppCompatImageView userLocation;
    private static final int[] PLACES = {R.string.campus_student_union, R.string.campus_library, R.string.campus_memory_mall};
    private final AppCompatImageButton[] targets = new AppCompatImageButton[3];
    private OnDropSelectedListener listener;

    private float userNormX = 0.60f;
    private float userNormY = 0.56f;
    private boolean isDevSimMode = false;

    public interface OnDropSelectedListener {
        void onDropSelected(int number);
    }

    public CampusMapPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.campus_map_surface);
        setClipToOutline(true);

        AppCompatImageView map = new AppCompatImageView(context);
        map.setImageResource(R.drawable.campus_map_preview);
        map.setScaleType(ImageView.ScaleType.CENTER_CROP);
        map.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(map, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // User Location Blue Pulsing Dot
        userLocation = new AppCompatImageView(context);
        userLocation.setImageResource(R.drawable.campus_user_location);
        userLocation.setContentDescription(getResources().getString(R.string.campus_user_location));
        int locationSize = Math.round(56 * getResources().getDisplayMetrics().density);
        addView(userLocation, new LayoutParams(locationSize, locationSize));

        // Interactive Voice Pins
        int targetSize = getResources().getDimensionPixelSize(R.dimen.campus_touch_target);
        for (int i = 0; i < targets.length; i++) {
            final int number = i + 1;
            AppCompatImageButton target = new AppCompatImageButton(context);
            target.setBackgroundResource(R.drawable.campus_pin_target);
            target.setImageResource(R.drawable.ic_campus_voice_pin);
            int iconPadding = Math.round(4 * getResources().getDisplayMetrics().density);
            target.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
            target.setScaleType(ImageView.ScaleType.FIT_CENTER);
            target.setContentDescription(getResources().getString(R.string.campus_drop_label, number)
                    + ", " + getResources().getString(PLACES[i]));
            target.setOnClickListener(view -> {
                for (AppCompatImageButton button : targets) button.setSelected(button == view);
                if (listener != null) listener.onDropSelected(number);
            });
            targets[i] = target;
            int targetHeight = Math.round(56 * getResources().getDisplayMetrics().density);
            addView(target, new LayoutParams(targetSize, targetHeight));
        }

        TextView credit = new TextView(context);
        credit.setText(R.string.campus_map_attribution);
        credit.setTextSize(10);
        credit.setTextColor(Color.BLACK);
        credit.setBackgroundColor(0xE6FFFFFF);
        int creditPadding = Math.round(4 * getResources().getDisplayMetrics().density);
        credit.setPadding(creditPadding, creditPadding, creditPadding, creditPadding);
        addView(credit, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.END));
    }

    public void setOnDropSelectedListener(OnDropSelectedListener listener) {
        this.listener = listener;
    }

    public void setDevSimMode(boolean enabled) {
        this.isDevSimMode = enabled;
    }

    /** Updates user location from real GPS latitude and longitude. */
    public void updateUserLocationGps(double latitude, double longitude) {
        float normX = (float) ((longitude - UCF_MIN_LNG) / (UCF_MAX_LNG - UCF_MIN_LNG));
        float normY = (float) ((UCF_MAX_LAT - latitude) / (UCF_MAX_LAT - UCF_MIN_LAT));

        // Clamp within map boundaries
        userNormX = Math.max(0.08f, Math.min(0.92f, normX));
        userNormY = Math.max(0.08f, Math.min(0.92f, normY));

        requestLayout();
    }

    /** Moves user location marker to specific landmark anchor. */
    public void highlightLandmark(int landmarkIndex) {
        if (landmarkIndex >= 0 && landmarkIndex < PIN_ANCHORS.length) {
            userNormX = PIN_ANCHORS[landmarkIndex][0] / MAP_WIDTH;
            userNormY = PIN_ANCHORS[landmarkIndex][1] / MAP_HEIGHT;
            requestLayout();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If Dev Simulation Mode is enabled, allow tapping anywhere on map to move user position!
        if (isDevSimMode && (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)) {
            userNormX = Math.max(0.08f, Math.min(0.92f, event.getX() / getWidth()));
            userNormY = Math.max(0.08f, Math.min(0.92f, event.getY() / getHeight()));
            requestLayout();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float scale = Math.max(getWidth() / MAP_WIDTH, getHeight() / MAP_HEIGHT);
        float offsetX = (getWidth() - MAP_WIDTH * scale) / 2f;
        float offsetY = (getHeight() - MAP_HEIGHT * scale) / 2f;

        // Position User Location Marker dynamically
        int locationSize = userLocation.getMeasuredWidth();
        int locationX = Math.round(offsetX + (userNormX * MAP_WIDTH) * scale - locationSize / 2f);
        int locationY = Math.round(offsetY + (userNormY * MAP_HEIGHT) * scale - locationSize / 2f);
        userLocation.layout(locationX, locationY, locationX + locationSize, locationY + locationSize);

        // Position Landmark Voice Drop Pins
        for (int i = 0; i < targets.length; i++) {
            int size = targets[i].getMeasuredWidth();
            int height = targets[i].getMeasuredHeight();
            int x = Math.round(offsetX + PIN_ANCHORS[i][0] * scale - size / 2f);
            int y = Math.round(offsetY + PIN_ANCHORS[i][1] * scale - height);
            targets[i].layout(x, y, x + size, y + height);
        }
    }
}
