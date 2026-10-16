package com.scanner.audio_graffiti.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.scanner.audio_graffiti.database.entity.VoiceMemo;

import java.util.List;

@Dao
public interface VoiceMemoDao {

    @Insert
    long insertVoiceMemo(VoiceMemo voiceMemo);

    @Update
    void updateVoiceMemo(VoiceMemo voiceMemo);

    @Delete
    void deleteVoiceMemo(VoiceMemo voiceMemo);

    @Query("SELECT * FROM voice_memos WHERE memo_id = :memoId LIMIT 1")
    VoiceMemo getVoiceMemoById(int memoId);

    @Query("SELECT * FROM voice_memos WHERE pin_id = :userId")
    List<VoiceMemo> getVoiceMemosByUserId(int userId);

    @Query("SELECT * FROM voice_memos WHERE status = :status")
    List<VoiceMemo> getVoiceMemosByStatus(String status);

    @Query("SELECT * FROM voice_memos WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    List<VoiceMemo> getVoiceMemosInArea(double minLat, double maxLat, double minLng, double maxLng);
}
