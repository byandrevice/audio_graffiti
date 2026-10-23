package com.scanner.audio_graffiti;

import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.scanner.audio_graffiti.ui.CampusMapPanel;
import com.scanner.audio_graffiti.ui.VoiceRecorderSheet;

/** Local campus map and sample drop selections, with real microphone recording. */
public final class CampusMapActivity extends AppCompatActivity {
    private VoiceRecorderSheet recorderSheet;
    private final ActivityResultLauncher<String> microphonePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) startRecording();
                else Toast.makeText(this, R.string.campus_microphone_permission, Toast.LENGTH_LONG).show();
            });
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);
        View root = findViewById(R.id.main);
        int gutter = getResources().getDimensionPixelSize(R.dimen.campus_gutter);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(gutter + bars.left, bars.top, gutter + bars.right, bars.bottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);
        CampusMapPanel map = findViewById(R.id.campus_map);
        map.setOnDropSelectedListener(number -> {
            showSheet(getString(R.string.campus_drop_label, number),
                    getString(R.string.campus_drop_description));
            Toast.makeText(this, R.string.campus_preview_audio_notice, Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.campus_record).setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) startRecording();
            else microphonePermission.launch(Manifest.permission.RECORD_AUDIO);
        });
    }

    private void startRecording() {
        if (isFinishing() || (recorderSheet != null && recorderSheet.isShowing())) return;
        recorderSheet = new VoiceRecorderSheet(this);
        recorderSheet.show(); recorderSheet.beginRecording();
    }

    @Override protected void onStop() {
        if (recorderSheet != null) { recorderSheet.stopCapture(); recorderSheet.pausePlayback(); }
        super.onStop();
    }

    @Override protected void onDestroy() {
        if (recorderSheet != null) recorderSheet.dismiss();
        super.onDestroy();
    }

    private void showSheet(String title, String description) {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        sheet.setContentView(R.layout.campus_map_sheet);
        ((TextView) sheet.findViewById(R.id.campus_sheet_title)).setText(title);
        ((TextView) sheet.findViewById(R.id.campus_sheet_description)).setText(description);
        sheet.findViewById(R.id.campus_sheet_close).setOnClickListener(view -> sheet.dismiss());
        sheet.show();
    }
}
