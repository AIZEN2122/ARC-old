# ARC — Native Android Workout App

ARC is a real Kotlin + Jetpack Compose Android app. It is designed as an offline-first personal training system with a large exercise library, custom split builder, detailed beginner tutorials, procedural exercise demonstrations, adaptive warm-up/cool-down, live workout logging, rest timer, goals, history, checklists, reports, backup/restore, and the supplied ARC dashboard image.

## Build
1. Open this folder in Android Studio (the project uses current 2026 Android tooling: AGP 9.4.0 / Gradle 9.6.0 / Compose BOM 2026.09.00).
2. Let Gradle Sync finish.
3. Connect an Android device or create an emulator.
4. Press **Run** to test.
5. Use **Build → Build APK(s)** for a debug APK.

## APK
The debug APK will normally appear at `app/build/outputs/apk/debug/app-debug.apk`.

## Exercise library
The app ships with a bundled seed set and has a Settings action **SYNC 800+ OPEN EXERCISES**. The first sync downloads the open English exercise dataset from Kinetic.place (MIT licensed) and caches it locally. The app can then search/use the synced library offline.

## Photo
The provided image is bundled as `app/src/main/res/drawable-nodpi/arc_thumbnail.png` and used as the main dashboard visual.

## Important
This environment cannot run Android Gradle because the Android SDK is not installed here. I have therefore not claimed a successful local APK build. Android Studio on your PC performs that final compilation step.
