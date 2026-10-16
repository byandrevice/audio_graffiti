package com.scanner.audio_graffiti.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "user_name")
    private String userName;

    @ColumnInfo(name = "fname")
    private String fname;

    @ColumnInfo(name = "lname")
    private String lname;

    @ColumnInfo(name = "bio")
    private String bio;

    @ColumnInfo(name = "avatar_url")
    private String avatarUrl;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "is_banned")
    private boolean isBanned;

    @ColumnInfo(name = "is_annonymous")
    private boolean isAnonymous;

    @ColumnInfo(name = "create_at")
    private String createAt;

    public User(String userName, String fname, String lname, String bio, String avatarUrl,
                String email, String password, boolean isBanned, boolean isAnonymous, String createAt) {
        this.userName = userName;
        this.fname = fname;
        this.lname = lname;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.password = password;
        this.isBanned = isBanned;
        this.isAnonymous = isAnonymous;
        this.createAt = createAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; }

    public String getLname() { return lname; }
    public void setLname(String lname) { this.lname = lname; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { isBanned = banned; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public String getCreateAt() { return createAt; }
    public void setCreateAt(String createAt) { this.createAt = createAt; }
}
