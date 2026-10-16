package com.scanner.audio_graffiti.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "reports",
    foreignKeys = {
        @ForeignKey(
            entity = VoiceMemo.class,
            parentColumns = "memo_id",
            childColumns = "memo_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = User.class,
            parentColumns = "user_id",
            childColumns = "reporter_user_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("memo_id"), @Index("reporter_user_id")}
)
public class Report {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "report_id")
    private int reportId;

    @ColumnInfo(name = "memo_id")
    private int memoId;

    @ColumnInfo(name = "reporter_user_id")
    private int reporterUserId;

    @ColumnInfo(name = "reason")
    private String reason;

    @ColumnInfo(name = "create_at")
    private String createAt;

    public Report(int memoId, int reporterUserId, String reason, String createAt) {
        this.memoId = memoId;
        this.reporterUserId = reporterUserId;
        this.reason = reason;
        this.createAt = createAt;
    }

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public int getMemoId() { return memoId; }
    public void setMemoId(int memoId) { this.memoId = memoId; }

    public int getReporterUserId() { return reporterUserId; }
    public void setReporterUserId(int reporterUserId) { this.reporterUserId = reporterUserId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getCreateAt() { return createAt; }
    public void setCreateAt(String createAt) { this.createAt = createAt; }
}
