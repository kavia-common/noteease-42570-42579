# NoteEase Workspace

This workspace contains the Android frontend for the NoteEase app.

How to build the Android app:

- Change directory to the android_frontend folder (already configured with the declarative Gradle DSL).
- Build:
  ./gradlew :app:assembleDebug

- Install on a connected device/emulator:
  ./gradlew :app:installDebug

The application uses Jetpack Compose, with an in‑memory repository persisting for the app session, and includes:
- Notes list with search and FAB
- Create/Edit note with validation
- Note detail with edit/delete/share actions
- Ocean Professional theme (primary #2563EB, secondary #F59E0B, error #EF4444, background #f9fafb, surface #ffffff, text #111827)

Refer to android_frontend/README.md for more details.
