# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug APK on connected device/emulator
./gradlew installDebug

# Clean build
./gradlew clean
```

## Testing

```bash
# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "hcmute.edu.vn.smartstudyassistant.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest
```

## Project Structure

Single-module Android app (`app/`) with package `hcmute.edu.vn.smartstudyassistant`.

- **`app/src/main/java/`** — Kotlin source code
- **`app/src/main/res/`** — layouts, drawables, values (colors, strings, themes)
- **`app/src/main/AndroidManifest.xml`** — app entry point, activity declarations
- **`gradle/libs.versions.toml`** — centralized dependency version catalog (AGP 9.0.1)

## Tech Stack

- **Language**: Kotlin
- **Min SDK**: 24 | **Target SDK**: 36 | **Compile SDK**: 36
- **UI**: View-based (XML layouts, ConstraintLayout)
- **Dependencies**: AndroidX AppCompat, Core KTX, Material Components, ConstraintLayout

## Branch Strategy

| Branch | Owner |
|--------|-------|
| `main` | stable, production-ready |
| `dev` | integration branch — merge here before `main` |
| `bao` | member bao |
| `tai` | member tai |
| `duy` | member duy |

Workflow: develop on personal branch → merge into `dev` → PR to `main`.
