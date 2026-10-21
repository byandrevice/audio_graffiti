package com.scanner.audio_graffiti.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Gravity;
import android.graphics.Color;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatImageButton;
import com.scanner.audio_graffiti.R;

/** A quiet local OSM map, sample audio drops, and a preview user position. */
public final class CampusMapPanel extends FrameLayout {
    private static final float MAP_WIDTH = 1290f;
    private static final float MAP_HEIGHT = 2064f;
    // Projected from real landmark coordinates; these are sample drops, not live data.
    private static final float[][] PIN_ANCHORS = {{637.289f, 1004.934f}, {517.218f, 1244.192f}, {809.833f, 743.068f}};
    private final AppCompatImageView userLocation;
    private static final int[] PLACES = {R.string.campus_student_union, R.string.campus_library, R.string.campus_memory_mall};
    private final AppCompatImageButton[] targets = new AppCompatImageButton[3];
    private OnDropSelectedListener listener;

    public interface OnDropSelectedListener { void onDropSelected(int number); }

    public CampusMapPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.campus_map_surface);
        setClipToOutline(true);
        AppCompatImageView map = new AppCompatImageView(context);
        map.setImageResource(R.drawable.campus_map_preview);
        map.setScaleType(ImageView.ScaleType.CENTER_CROP);
        map.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(map, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        userLocation = new AppCompatImageView(context);
        userLocation.setImageResource(R.drawable.campus_user_location);
        userLocation.setContentDescription(getResources().getString(R.string.campus_user_location));
        int locationSize = Math.round(56 * getResources().getDisplayMetrics().density);
        addView(userLocation, new LayoutParams(locationSize, locationSize));
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

    public void setOnDropSelectedListener(OnDropSelectedListener listener) { this.listener = listener; }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float scale = Math.max(getWidth() / MAP_WIDTH, getHeight() / MAP_HEIGHT);
        float offsetX = (getWidth() - MAP_WIDTH * scale) / 2f;
        float offsetY = (getHeight() - MAP_HEIGHT * scale) / 2f;
        int locationSize = userLocation.getMeasuredWidth();
        int locationX = Math.round(offsetX + 774f * scale - locationSize / 2f);
        int locationY = Math.round(offsetY + 1172.382f * scale - locationSize / 2f);
        userLocation.layout(locationX, locationY, locationX + locationSize, locationY + locationSize);
        for (int i = 0; i < targets.length; i++) {
            int size = targets[i].getMeasuredWidth();
            int height = targets[i].getMeasuredHeight();
            int x = Math.round(offsetX + PIN_ANCHORS[i][0] * scale - size / 2f);
            int y = Math.round(offsetY + PIN_ANCHORS[i][1] * scale - height);
            targets[i].layout(x, y, x + size, y + height);
        }
    }
}
