# NoteEase - Android (Jetpack Compose)

A modern notes app implemented with Jetpack Compose following the Ocean Professional theme.

## Features (current)
- Notes List: search/filter, empty state, rounded cards with timestamps, FAB to add note.
- Note Editor: create/edit with validation (title required), save and delete (for existing).
- Note Detail: view large title, content, timestamp with Edit, Delete, Share actions.
- In-memory repository using StateFlow; data persists for the app session.
- Clean architecture: NoteRepository interface allows swapping for real storage later.
- Material 3 styling with Ocean Professional palette, subtle gradient backgrounds, rounded corners.

## Project layout
- app/src/main/kotlin/org/example/app
  - data/: Note model and repository (in-memory singleton)
  - ui/: Composable screens
  - ui/theme/: Compose theme (Ocean Professional)
  - viewmodel/: ViewModels and UI state

## Build and run
This project uses the Declarative Gradle DSL sample environment provided here.

- Build:
  ./gradlew :app:assembleDebug

- Install on device/emulator:
  ./gradlew :app:installDebug

- Launch on device: look for "NoteEase".

The CI preview system builds the module from the `android_frontend` container; no extra steps needed. You do not need to start previews yourself; the platform will handle it.

## Notes
- The repository is in-memory for now. Replace `InMemoryNoteRepository` with a persistence-backed implementation later without changing UI.
- Compose Material 3 is used. If the build environment updates versions, the Compose BOM will align dependencies automatically.
