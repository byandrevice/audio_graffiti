package com.scanner.audio_graffiti.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.scanner.audio_graffiti.R;
import java.util.ArrayList;
import java.util.List;

/** Scrolling microphone amplitudes; silence remains flat. */
public final class RecordingWaveformView extends View {
    private final List<Float> levels = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;
    public RecordingWaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        paint.setStrokeCap(Paint.Cap.ROUND);
    }
    public void addAmplitude(float value) {
        levels.add(Math.max(0f, Math.min(1f, value)));
        if (levels.size() > 100) levels.remove(0);
        invalidate();
    }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float center = getHeight() / 2f, head = getWidth() * .8f;
        paint.setColor(ContextCompat.getColor(getContext(), R.color.campus_border));
        paint.setStrokeWidth(density);
        canvas.drawLine(0, center, getWidth(), center, paint);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.campus_recording_red));
        paint.setStrokeWidth(2 * density);
        for (int i = levels.size() - 1; i >= 0; i--) {
            float x = head - (levels.size() - 1 - i) * 3 * density;
            if (x < 0) break;
            float height = Math.max(2 * density, levels.get(i) * getHeight() * .84f);
            canvas.drawLine(x, center - height / 2, x, center + height / 2, paint);
        }
        canvas.drawLine(head + 4 * density, 8 * density, head + 4 * density, getHeight() - 8 * density, paint);
    }
}
