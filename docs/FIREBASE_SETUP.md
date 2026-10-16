# Firebase Integration Guide

This project is configured with **Firebase** for the package `com.scanner.audio_graffiti`.

## Configured Files
- `app/google-services.json`: Contains project credentials (`audiograffiti-23f3a`).
- `gradle/libs.versions.toml`: Contains Firebase BoM (`33.7.0`) and Google Services Plugin (`4.4.2`).
- `app/build.gradle.kts`: Google Services plugin applied and Firebase BoM configured.

---

## Adding Additional Firebase Dependencies

To add more Firebase features, add them to `gradle/libs.versions.toml` and `app/build.gradle.kts`.

### Firebase Authentication (Sign In / Sign Up)
1. Add to `libs.versions.toml`:
   ```toml
   [libraries]
   firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }
   ```
2. Add to `app/build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(libs.firebase.auth)
   }
   ```
3. Usage in Java:
   ```java
   FirebaseAuth mAuth = FirebaseAuth.getInstance();
   ```

### Firebase Cloud Storage (Audio Files Storage)
1. Add to `libs.versions.toml`:
   ```toml
   [libraries]
   firebase-storage = { group = "com.google.firebase", name = "firebase-storage" }
   ```
2. Add to `app/build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(libs.firebase.storage)
   }
   ```
3. Usage in Java:
   ```java
   FirebaseStorage storage = FirebaseStorage.getInstance();
   StorageReference storageRef = storage.getReference();
   ```

### Firebase Cloud Firestore (NoSQL Cloud Database)
1. Add to `libs.versions.toml`:
   ```toml
   [libraries]
   firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore" }
   ```
2. Add to `app/build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(libs.firebase.firestore)
   }
   ```
3. Usage in Java:
   ```java
   FirebaseFirestore db = FirebaseFirestore.getInstance();
   ```
