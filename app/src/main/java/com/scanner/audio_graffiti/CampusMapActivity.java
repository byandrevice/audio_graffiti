package com.scanner.audio_graffiti;

import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import com.scanner.audio_graffiti.ui.VoiceCommentsSheet;
import com.scanner.audio_graffiti.ui.VoiceRecorderSheet;

import org.maplibre.android.MapLibre;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;

/**
 * Main Campus Map Activity using MapLibre Native SDK (Free 60fps Vector Maps for UCF Campus).
 * Centers on UCF Main Campus (28.6024° N, 81.2001° W) with real interactive voice drop markers.
 */
public final class CampusMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "CampusMapActivity";
    private static final LatLng UCF_STUDENT_UNION = new LatLng(28.6024, -81.2001);
    private static final LatLng UCF_LIBRARY = new LatLng(28.6001, -81.2015);
    private static final LatLng UCF_MEMORY_MALL = new LatLng(28.6045, -81.1985);
    private static final LatLng UCF_PEGASUS_CIRCLE = new LatLng(28.6010, -81.2025);

    private MapView mapView;
    private MapLibreMap mapLibreMap;
    private CampusMapPanel fallbackCampusMapPanel;
    private VoiceRecorderSheet recorderSheet;
    private MediaPlayer dropPlayer;
    private boolean isDevSimMode = false;

    // Combined Microphone & Location Permission Launcher
    private final ActivityResultLauncher<String[]> appPermissionsLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean micGranted = result.getOrDefault(Manifest.permission.RECORD_AUDIO, false);
                Boolean locationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);

                if (Boolean.TRUE.equals(micGranted) && Boolean.TRUE.equals(locationGranted)) {
                    Toast.makeText(this, "Microphone and Location permissions enabled!", Toast.LENGTH_SHORT).show();
                } else if (Boolean.TRUE.equals(locationGranted)) {
                    Toast.makeText(this, "Location permission enabled.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        // Initialize MapLibre BEFORE layout inflation
        MapLibre.getInstance(this);

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

        // Prompt user for Microphone and Location permissions
        checkAndRequestAppPermissions();

        // Initialize MapLibre MapView
        mapView = findViewById(R.id.maplibre_view);
        if (mapView != null) {
            mapView.onCreate(state);
            mapView.getMapAsync(this);
        } else {
            fallbackCampusMapPanel = findViewById(R.id.campus_map);
            if (fallbackCampusMapPanel != null) {
                fallbackCampusMapPanel.setVisibility(View.VISIBLE);
            }
        }

        // Settings Button -> Opens Settings Activity
        View settingsButton = findViewById(R.id.btn_settings);
        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> {
                Intent intent = new Intent(CampusMapActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        // Search Input Handling
        EditText searchInput = findViewById(R.id.search_input);
        if (searchInput != null) {
            searchInput.setOnEditorActionListener((v, actionId, event) -> {
                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    Toast.makeText(CampusMapActivity.this, "Searching UCF campus for: " + query, Toast.LENGTH_SHORT).show();
                }
                return false;
            });
        }

        // Campus Landmark Quick Filter Chips
        setupLandmarkChips();

        // Developer Simulation Mode FAB
        View btnDevSim = findViewById(R.id.btn_dev_sim);
        if (btnDevSim != null) {
            btnDevSim.setOnClickListener(v -> {
                isDevSimMode = !isDevSimMode;
                Toast.makeText(CampusMapActivity.this, isDevSimMode ? "Dev Sim Mode ON - Tap map to simulate position!" : "Dev Sim Mode OFF", Toast.LENGTH_SHORT).show();
            });
        }

        // My Location Recenter FAB
        View btnMyLocation = findViewById(R.id.btn_my_location);
        if (btnMyLocation != null) {
            btnMyLocation.setOnClickListener(v -> {
                if (mapLibreMap != null) {
                    mapLibreMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(UCF_STUDENT_UNION)
                                    .zoom(16.0)
                                    .build()));
                    Toast.makeText(CampusMapActivity.this, "Re-centered on UCF Main Campus", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Record Microphone Button -> Opens Recorder Sheet (campus_recorder_sheet.xml)
        View recordBtn = findViewById(R.id.campus_record);
        if (recordBtn != null) {
            recordBtn.setOnClickListener(view -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    startRecording(null);
                } else {
                    appPermissionsLauncher.launch(new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    });
                }
            });
        }
    }

    @Override
    public void onMapReady(@NonNull MapLibreMap map) {
        this.mapLibreMap = map;

        // Load Free OpenStreetMap Vector Map Style
        mapLibreMap.setStyle(new Style.Builder().fromUri("https://demotiles.maplibre.org/style.json"), style -> {
            // Center map camera on UCF Student Union
            mapLibreMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(UCF_STUDENT_UNION)
                            .zoom(15.8)
                            .build()));

            // Add UCF Voice Drop Pins
            addVoicePinMarker(UCF_STUDENT_UNION, "Student Union Drop", "Length: 1:24 • Student Union");
            addVoicePinMarker(UCF_LIBRARY, "Hitt Library Study Drop", "Length: 0:45 • John C. Hitt Library");
            addVoicePinMarker(UCF_MEMORY_MALL, "Tailgate Music Drop", "Length: 2:10 • Memory Mall");

            // Handle Marker Clicks -> Opens Callout Sheet (campus_map_sheet.xml)
            mapLibreMap.setOnMarkerClickListener(marker -> {
                showDropCalloutSheet(marker.getTitle(), marker.getSnippet());
                return true;
            });

            // Handle Map Click in Dev Sim Mode
            mapLibreMap.addOnMapClickListener(latLng -> {
                if (isDevSimMode) {
                    mapLibreMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    Toast.makeText(this, "Simulated position: " + String.format(java.util.Locale.US, "%.4f, %.4f", latLng.getLatitude(), latLng.getLongitude()), Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        });
    }

    private void addVoicePinMarker(LatLng position, String title, String snippet) {
        if (mapLibreMap == null) return;
        mapLibreMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet));
    }

    private void setupLandmarkChips() {
        View chipStudentUnion = findViewById(R.id.chip_student_union);
        View chipLibrary = findViewById(R.id.chip_library);
        View chipMemoryMall = findViewById(R.id.chip_memory_mall);
        View chipPegasusCircle = findViewById(R.id.chip_pegasus_circle);

        if (chipStudentUnion != null) {
            chipStudentUnion.setOnClickListener(v -> moveCameraTo(UCF_STUDENT_UNION, "Student Union"));
        }
        if (chipLibrary != null) {
            chipLibrary.setOnClickListener(v -> moveCameraTo(UCF_LIBRARY, "John C. Hitt Library"));
        }
        if (chipMemoryMall != null) {
            chipMemoryMall.setOnClickListener(v -> moveCameraTo(UCF_MEMORY_MALL, "Memory Mall"));
        }
        if (chipPegasusCircle != null) {
            chipPegasusCircle.setOnClickListener(v -> moveCameraTo(UCF_PEGASUS_CIRCLE, "Pegasus Circle"));
        }
    }

    private void moveCameraTo(LatLng target, String label) {
        if (mapLibreMap != null) {
            mapLibreMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(target)
                            .zoom(16.5)
                            .build()));
            Toast.makeText(this, "Centered on " + label, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndRequestAppPermissions() {
        boolean hasMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        boolean hasLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!hasMic || !hasLocation) {
            appPermissionsLauncher.launch(new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void startRecording(String replyToUsername) {
        if (isFinishing() || (recorderSheet != null && recorderSheet.isShowing())) return;
        recorderSheet = new VoiceRecorderSheet(this, replyToUsername);
        recorderSheet.show();
        recorderSheet.beginRecording();
    }

    private void showDropCalloutSheet(String title, String description) {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        sheet.setContentView(R.layout.campus_map_sheet);

        TextView titleTv = sheet.findViewById(R.id.campus_sheet_title);
        TextView descTv = sheet.findViewById(R.id.campus_sheet_description);

        if (titleTv != null && !TextUtils.isEmpty(title)) titleTv.setText(title);
        if (descTv != null && !TextUtils.isEmpty(description)) descTv.setText(description);

        // PLAY Button
        View btnPlay = sheet.findViewById(R.id.btn_play_drop);
        if (btnPlay != null) {
            btnPlay.setOnClickListener(v -> 
                Toast.makeText(CampusMapActivity.this, "Playing " + title + "...", Toast.LENGTH_SHORT).show());
        }

        // REPLY Button -> Opens Voice Comments Sheet
        View btnReply = sheet.findViewById(R.id.btn_reply_drop);
        if (btnReply != null) {
            btnReply.setOnClickListener(v -> {
                sheet.dismiss();
                showCommentsSheet(title, description);
            });
        }

        // Report Pin Button -> Opens Report User Screen
        View btnReport = sheet.findViewById(R.id.btn_report_drop);
        if (btnReport != null) {
            btnReport.setOnClickListener(v -> {
                sheet.dismiss();
                startActivity(new Intent(CampusMapActivity.this, ReportUserActivity.class));
            });
        }

        // Close Button
        View closeBtn = sheet.findViewById(R.id.campus_sheet_close);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(v -> sheet.dismiss());
        }

        sheet.show();
    }

    private void showCommentsSheet(String title, String authorLocation) {
        VoiceCommentsSheet commentsSheet = new VoiceCommentsSheet(this, title, authorLocation, replyToUsername -> {
            startRecording(replyToUsername);
        });
        commentsSheet.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    protected void onStop() {
        if (mapView != null) mapView.onStop();
        if (recorderSheet != null) {
            recorderSheet.stopCapture();
            recorderSheet.pausePlayback();
        }
        if (dropPlayer != null) {
            dropPlayer.release();
            dropPlayer = null;
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        if (mapView != null) mapView.onDestroy();
        if (recorderSheet != null) recorderSheet.dismiss();
        super.onDestroy();
    }
}
