package com.scanner.audio_graffiti.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.scanner.audio_graffiti.database.entity.Report;

import java.util.List;

@Dao
public interface ReportDao {

    @Insert
    long insertReport(Report report);

    @Query("SELECT * FROM reports WHERE memo_id = :memoId")
    List<Report> getReportsForMemo(int memoId);
}
