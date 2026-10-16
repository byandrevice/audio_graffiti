package com.scanner.audio_graffiti.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.scanner.audio_graffiti.database.entity.ReplyMemo;

import java.util.List;

@Dao
public interface ReplyMemoDao {

    @Insert
    long insertReply(ReplyMemo replyMemo);

    @Delete
    void deleteReply(ReplyMemo replyMemo);

    @Query("SELECT * FROM reply_memos WHERE memo_id = :memoId ORDER BY create_at ASC")
    List<ReplyMemo> getRepliesForMemo(int memoId);
}
