package com.scanner.audio_graffiti.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "voice_memos",
    foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "user_id",
        childColumns = "pin_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("pin_id")}
)
public class VoiceMemo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "memo_id")
    private int memoId;

    @ColumnInfo(name = "pin_id")
    private int userId;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "location_name")
    private String locationName;

    @ColumnInfo(name = "audio_file_url")
    private String audioFileUrl;

    @ColumnInfo(name = "duration_seconds")
    private int durationSeconds;

    @ColumnInfo(name = "is_anonymous")
    private boolean isAnonymous;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "expires_at")
    private String expiresAt;

    @ColumnInfo(name = "create_at")
    private String createAt;

    public VoiceMemo(int userId, double latitude, double longitude, String locationName,
                     String audioFileUrl, int durationSeconds, boolean isAnonymous,
                     String status, String expiresAt, String createAt) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.audioFileUrl = audioFileUrl;
        this.durationSeconds = durationSeconds;
        this.isAnonymous = isAnonymous;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createAt = createAt;
    }

    public int getMemoId() { return memoId; }
    public void setMemoId(int memoId) { this.memoId = memoId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getAudioFileUrl() { return audioFileUrl; }
    public void setAudioFileUrl(String audioFileUrl) { this.audioFileUrl = audioFileUrl; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }

    public String getCreateAt() { return createAt; }
    public void setCreateAt(String createAt) { this.createAt = createAt; }
}
