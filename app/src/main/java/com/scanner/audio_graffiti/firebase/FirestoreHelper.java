package com.scanner.audio_graffiti.firebase;

import com.google.firebase.firestore.FirebaseFirestore;
import com.scanner.audio_graffiti.database.entity.User;
import com.scanner.audio_graffiti.database.entity.VoiceMemo;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {

    private final FirebaseFirestore firestore;

    public FirestoreHelper() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    // Save or update User in Firestore 'users' collection
    public void saveUserToFirestore(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("user_id", user.getUserId());
        userData.put("user_name", user.getUserName());
        userData.put("fname", user.getFname());
        userData.put("lname", user.getLname());
        userData.put("email", user.getEmail());
        userData.put("is_banned", user.isBanned());
        userData.put("is_annonymous", user.isAnonymous());
        userData.put("create_at", user.getCreateAt());

        firestore.collection("users")
                .document(String.valueOf(user.getUserId()))
                .set(userData);
    }

    // Save Voice Memo in Firestore 'voice_memos' collection
    public void saveVoiceMemoToFirestore(VoiceMemo memo) {
        Map<String, Object> memoData = new HashMap<>();
        memoData.put("pin_id", memo.getUserId());
        memoData.put("latitude", memo.getLatitude());
        memoData.put("longitude", memo.getLongitude());
        memoData.put("location_name", memo.getLocationName());
        memoData.put("audio_file_url", memo.getAudioFileUrl());
        memoData.put("duration_seconds", memo.getDurationSeconds());
        memoData.put("status", memo.getStatus());
        memoData.put("create_at", memo.getCreateAt());

        firestore.collection("voice_memos")
                .add(memoData);
    }
}
