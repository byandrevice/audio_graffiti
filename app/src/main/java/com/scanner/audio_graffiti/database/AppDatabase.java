package com.scanner.audio_graffiti.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.scanner.audio_graffiti.database.dao.ReplyMemoDao;
import com.scanner.audio_graffiti.database.dao.ReportDao;
import com.scanner.audio_graffiti.database.dao.UserDao;
import com.scanner.audio_graffiti.database.dao.VoiceMemoDao;
import com.scanner.audio_graffiti.database.entity.ReplyMemo;
import com.scanner.audio_graffiti.database.entity.Report;
import com.scanner.audio_graffiti.database.entity.User;
import com.scanner.audio_graffiti.database.entity.VoiceMemo;

@Database(
    entities = {User.class, VoiceMemo.class, ReplyMemo.class, Report.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract VoiceMemoDao voiceMemoDao();
    public abstract ReplyMemoDao replyMemoDao();
    public abstract ReportDao reportDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "audio_graffiti_db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
