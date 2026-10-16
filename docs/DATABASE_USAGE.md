# Database Usage Guide (Room & SQL)

Audio Graffiti uses **Android Jetpack Room** for local database management in Java.

## Entity Structure

The database consists of 4 core entities matching the project ERD:

1. **User** (`users` table) - Stores user accounts and profiles.
2. **VoiceMemo** (`voice_memos` table) - Stores location-based audio pins created by users.
3. **ReplyMemo** (`reply_memos` table) - Stores voice replies to voice memos.
4. **Report** (`reports` table) - Stores user reports for flagged memos.

---

## How to Access the Database in Java

Room database operations **must** be executed on a background thread (not the Main UI thread).

### 1. Get the Database Instance
```java
AppDatabase db = AppDatabase.getInstance(context);
```

### 2. Insert Data
```java
Executors.newSingleThreadExecutor().execute(() -> {
    // 1. Create a User
    User user = new User(
        "johndoe", "John", "Doe", "Music lover", "http://example.com/avatar.jpg",
        "john@example.com", "hashed_password", false, false, "2025-01-01 12:00:00"
    );
    long newUserId = db.userDao().insertUser(user);

    // 2. Create a Voice Memo for the User
    VoiceMemo memo = new VoiceMemo(
        (int) newUserId, 37.7749, -122.4194, "Downtown San Francisco",
        "http://example.com/audio1.mp3", 30, false, "ACTIVE",
        "2025-01-02 12:00:00", "2025-01-01 12:00:00"
    );
    long newMemoId = db.voiceMemoDao().insertVoiceMemo(memo);

    // 3. Create a Reply
    ReplyMemo reply = new ReplyMemo(
        (int) newMemoId, (int) newUserId,
        "http://example.com/reply1.mp3", 10, "2025-01-01 12:05:00"
    );
    db.replyMemoDao().insertReply(reply);
});
```

### 3. Query Data
```java
Executors.newSingleThreadExecutor().execute(() -> {
    // Get user by ID
    User user = db.userDao().getUserById(1);

    // Get voice memos in a geographical area (bounding box latitude/longitude)
    List<VoiceMemo> nearbyMemos = db.voiceMemoDao().getVoiceMemosInArea(
        37.7000, 37.8000, -122.5000, -122.4000
    );

    // Get replies for a specific voice memo
    List<ReplyMemo> replies = db.replyMemoDao().getRepliesForMemo(1);
});
```

---

## Backend SQL Reference
If hosting an external MySQL or PostgreSQL database, use the pre-built schema file located at:
`app/src/main/assets/schema.sql`
