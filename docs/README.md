# Audio Graffiti - Developer Guide

Welcome to the **Audio Graffiti** Android application developer guide. This project is built using **Java** with Jetpack libraries and **Firebase**.

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture & Tech Stack](#architecture--tech-stack)
- [Database Guide](DATABASE_USAGE.md)
- [Firebase Guide](FIREBASE_SETUP.md)
- [Getting Started](#getting-started)

## Project Overview
Audio Graffiti allows users to create, share, reply to, and report location-based voice memos.

## Architecture & Tech Stack
- **Language**: Java 17
- **UI Toolkit**: Android XML Views & Material 3 (`com.google.android.material`)
- **Database**: Android Jetpack Room (SQLite local storage)
- **Backend / Services**: Firebase (Google Services, Analytics, BoM)
- **Build System**: Gradle with Kotlin DSL & Gradle Version Catalog (`gradle/libs.versions.toml`)

## Getting Started
1. Open the project in Android Studio.
2. Sync Gradle files (`File -> Sync Project with Gradle Files`).
3. Build and run on an Android Emulator or connected device (Min SDK: 24, Target SDK: 35).
