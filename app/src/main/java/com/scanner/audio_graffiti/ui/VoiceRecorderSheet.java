package com.scanner.audio_graffiti.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.scanner.audio_graffiti.R;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

/** Immediate recording with real levels, Stop, and local playback. Never uploads audio. */
public final class VoiceRecorderSheet extends BottomSheetDialog {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final TextView title, clock, message;
    private final MaterialButton stop, play;
    private final RecordingWaveformView waveform;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private File output;
    private long started;
    private boolean recording;

    public VoiceRecorderSheet(Context context) {
        super(context);
        setContentView(R.layout.campus_recorder_sheet);
        title = findViewById(R.id.recorder_title); clock = findViewById(R.id.recorder_clock);
        message = findViewById(R.id.recorder_message); stop = findViewById(R.id.recorder_stop);
        play = findViewById(R.id.recorder_play); waveform = findViewById(R.id.recorder_waveform);
        stop.setOnClickListener(view -> { if (recording) stopCapture(); else dismiss(); });
        play.setOnClickListener(view -> togglePlayback());
        setCanceledOnTouchOutside(false);
        setOnDismissListener(dialog -> { stopCapture(); releasePlayer(); });
    }

    @SuppressWarnings("deprecation")
    public void beginRecording() {
        try {
            File drafts = new File(getContext().getFilesDir(), "voice-drafts");
            if (!drafts.exists() && !drafts.mkdirs()) throw new IOException("Cannot create drafts directory");
            output = File.createTempFile("voice-drop-", ".m4a", drafts);
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(128000);
            recorder.setAudioSamplingRate(44100);
            recorder.setOutputFile(output.getAbsolutePath());
            recorder.setOnErrorListener((source, what, extra) -> recordingFailed(R.string.campus_recording_error));
            recorder.prepare(); recorder.start();
            started = SystemClock.elapsedRealtime(); recording = true;
            handler.post(tick);
        } catch (IOException | RuntimeException error) { recordingFailed(R.string.campus_recording_error); }
    }

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (!recording || recorder == null) return;
            long seconds = (SystemClock.elapsedRealtime() - started) / 1000;
            clock.setText(String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60));
            try { waveform.addAmplitude((float) Math.sqrt(recorder.getMaxAmplitude() / 32767f)); }
            catch (RuntimeException error) { recordingFailed(R.string.campus_recording_error); return; }
            handler.postDelayed(this, 80);
        }
    };

    public void stopCapture() {
        if (!recording) return;
        recording = false; handler.removeCallbacks(tick);
        try { recorder.stop(); }
        catch (RuntimeException error) { recordingFailed(R.string.campus_recording_short); return; }
        finally { releaseRecorder(); }
        title.setText(R.string.campus_recorded);
        message.setVisibility(View.VISIBLE); message.setText(R.string.campus_recording_saved);
        play.setVisibility(View.VISIBLE);
        showDone();
    }

    private void recordingFailed(int messageId) {
        recording = false; handler.removeCallbacks(tick); releaseRecorder();
        if (output != null) { output.delete(); output = null; }
        title.setText(R.string.campus_recording_error);
        message.setVisibility(View.VISIBLE); message.setText(messageId);
        play.setVisibility(View.GONE); showDone();
    }
    private void showDone() {
        stop.setText(R.string.campus_done);
        stop.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.campus_primary)));
        stop.setTextColor(ContextCompat.getColor(getContext(), R.color.campus_foreground));
    }
    private void releaseRecorder() {
        if (recorder != null) { recorder.release(); recorder = null; }
    }
    private void releasePlayer() {
        if (player != null) { player.release(); player = null; }
        play.setText(R.string.campus_play);
    }
    public void pausePlayback() {
        if (player != null && player.isPlaying()) { player.pause(); play.setText(R.string.campus_play); }
    }
    private void togglePlayback() {
        try {
            if (player != null && player.isPlaying()) { pausePlayback(); return; }
            if (player == null) {
                player = new MediaPlayer(); player.setDataSource(output.getAbsolutePath()); player.prepare();
                player.setOnCompletionListener(media -> releasePlayer());
                player.setOnErrorListener((media, what, extra) -> { releasePlayer(); return true; });
            }
            player.start(); play.setText(R.string.campus_pause);
        } catch (IOException | RuntimeException error) {
            releasePlayer(); message.setText(R.string.campus_recording_error); message.setVisibility(View.VISIBLE);
        }
    }
}
