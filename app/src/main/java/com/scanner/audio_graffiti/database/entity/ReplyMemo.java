package com.scanner.audio_graffiti.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "reply_memos",
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
            childColumns = "user_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("memo_id"), @Index("user_id")}
)
public class ReplyMemo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "reply_id")
    private int replyId;

    @ColumnInfo(name = "memo_id")
    private int memoId;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "audio_file_url")
    private String audioFileUrl;

    @ColumnInfo(name = "duration_seconds")
    private int durationSeconds;

    @ColumnInfo(name = "create_at")
    private String createAt;

    public ReplyMemo(int memoId, int userId, String audioFileUrl, int durationSeconds, String createAt) {
        this.memoId = memoId;
        this.userId = userId;
        this.audioFileUrl = audioFileUrl;
        this.durationSeconds = durationSeconds;
        this.createAt = createAt;
    }

    public int getReplyId() { return replyId; }
    public void setReplyId(int replyId) { this.replyId = replyId; }

    public int getMemoId() { return memoId; }
    public void setMemoId(int memoId) { this.memoId = memoId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getAudioFileUrl() { return audioFileUrl; }
    public void setAudioFileUrl(String audioFileUrl) { this.audioFileUrl = audioFileUrl; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public String getCreateAt() { return createAt; }
    public void setCreateAt(String createAt) { this.createAt = createAt; }
}
