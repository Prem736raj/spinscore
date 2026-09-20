# Spin Bottle

Spin Bottle is a local-first Android party game for Truth or Dare, bottle spinning, Quick Fire, Couples, Kids Safe, custom prompts, prompt packs, scoring, themes, and optional dare-proof photos.

> **Repository name**: `SpinScore`  
> **Application ID**: `com.spinbottle.truthdare.games`  
> **Android App Name**: `Spin Bottle`  
> **Current Version**: `versionCode 1`, `versionName 1.0.0`

The repository name, application ID, and user-facing app name are intentionally kept stable to protect Google Play Store identity and configuration.

---

## Production Hardening Status

Active hardening branch:
`astra/spinscore-production-hardening` (targeting `master`)

- **Audit & Backlog**: Tracked in [AUDIT_FINDINGS.md](AUDIT_FINDINGS.md).
- **Target Engineering Readiness**: ~8.5 / 10.
- **Release Gating**: Automated local/CI build and test gates are active.
- **Manual Gate Statement**: Do NOT treat this app as ready for production release until the required physical device / emulator verifications and Google Play Console release checks are performed.

---

## Features

- **Bottle-Based Selection**: Accurate nearest-player angular mapping for physical-style bottle spinning.
- **Dynamic Truth & Dare Prompts**: Over 1,000 sanitized, safe prompts across Casual, Party, Spicy, and Couples categories.
- **Game Modes**:
  - **Classic**: Turn-based party game with scores and custom round limits.
  - **Quick Fire**: Fast-paced countdown timer with lifecycle pause protection.
  - **Couples**: Romantic mode requiring exactly two configured players.
  - **Kids Safe**: Strict prompt isolation and parent PIN-protected exit boundary.
- **Session State & Cold Resume**: Active sessions survive process death; cold-launch "Resume Game" action on Home screen restores exact turn state.
- **Custom Prompts & Packs**: In-app prompt creator with character validation, prompt packs, favorites, and repeat-avoidance history.
- **Persistent Settings & Themes**: Haptics, sound, bottle spin speed, and unlockable themes backed by gameplay achievements.
- **Local Dare Proofs**: Optional photo capture saved strictly to app-private storage, with scoped FileProvider sharing and deletion.
- **Supporter Purchases**: Google Play Billing Library 9.1.0 integration for optional lifetime supporter status.

---

## Tech Stack & Compatibility

| Component | Specification | Notes |
|---|---|---|
| Language | Kotlin 1.9.22 | Compatible with Compose Compiler 1.5.8 |
| UI Toolkit | Jetpack Compose (BOM 2024.01.00) | Material 3 |
| Navigation | Navigation Compose | Type-safe route pattern |
| Build Tool | Gradle 8.13 / AGP 8.13.2 | JDK 17 (Eclipse Temurin / OpenJDK) |
| Min SDK | API 24 (Android 7.0 Nougat) | Broad device reach |
| Compile SDK | API 36 (Android 16) | Latest platform APIs |
| Target SDK | API 36 (Android 16) | Meets Google Play August 2026 requirement |
| In-App Billing | Google Play Billing 9.1.0 | Current supported Play Billing generation |
| State & Storage | DataStore, SharedPreferences | Local-first, offline-capable |

The application architecture is intentionally single-module and strictly local-first. There is no custom backend, Firebase, Supabase, advertising SDK, or third-party analytics tracker.

---

## Build & Validation

### Requirements
- JDK 17 (configured via standard `JAVA_HOME`).
- Android SDK with API 36 platforms and build-tools installed.
- No developer-specific paths committed to `gradle.properties`.

### Production Validation Commands

#### Linux / macOS
```bash
./gradlew clean
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
./gradlew lintRelease
./gradlew assembleRelease
./gradlew bundleRelease
```

#### Windows (PowerShell)
```powershell
.\gradlew.bat clean
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
.\gradlew.bat assembleDebug
.\gradlew.bat lintRelease
.\gradlew.bat assembleRelease
.\gradlew.bat bundleRelease
```

### Instrumentation Smoke Tests
```powershell
.\gradlew.bat assembleDebugAndroidTest
```
*To run on a connected emulator or device:*
```powershell
.\gradlew.bat connectedDebugAndroidTest
```

---

## Architecture & State Persistence

The app adopts a resilient, pragmatic architecture tailored for offline party games:
- **UI**: Declarative Compose screens organized by feature in `com.spinbottle.truthdare.games.screens`.
- **Navigation**: Centrally declared in `AppNavigation.kt` with `SessionResumeRouter` handling cold resumes.
- **Session Management**: `GameSessionHolder` provides an in-memory active session coupled with automatic JSON serialization to persistent preferences for cold launches and process-death recovery.
- **Content Gates**: Centralized 4-digit PIN verification (`PinManager`) shielding Kids Safe exit boundaries.
- **Billing**: `BillingManager` acts as a reactive bridge to Google Play Billing Library 9.1.0, deriving premium state strictly from verified Play purchases.

---

## Privacy & Local Dare Proofs

- **No Remote Servers**: Player names, custom prompts, scores, settings, and PINs never leave the local device.
- **Dare Proof Storage**: Captured dare photos are stored in the app-private internal directory (`context.filesDir/dare_proofs`).
- **FileProvider Sharing**: Scoped strictly to `dare_proofs` in `res/xml/file_paths.xml`. Only invoked when the user explicitly triggers Android system sharing.
- **Cloud Backup Exclusion**: Both `res/xml/backup_rules.xml` and `res/xml/data_extraction_rules.xml` explicitly exclude active session preferences and internal proof files from Google Cloud backups and device-to-device transfers.

---

## Google Play Billing Test Guidance

1. **Architecture**: Implemented using Google Play Billing Library 9.1.0.
2. **Product Configuration**:
   - In-app product ID: `remove_ads_lifetime` (One-time purchase / Supporter tier).
3. **Play Console Testing Setup**:
   - Add tester Google accounts under **Play Console > Setup > License Testing**.
   - Set **License Test Response** to `RESPOND_NORMALLY`.
   - Install the signed debug/release build onto a test device signed into a license tester account.
   - Open **Support Spin Bottle** screen and complete a test purchase using Google Play's test payment instrument.
   - Verify that supporter status reflects immediately and remains active upon restarting the app.

---

## Manual Verification Requirements

Host unit tests and static linters cannot verify physical hardware behavior. The following MUST be validated on physical Android hardware or emulators prior to publishing:

- **Camera & Photos**: Permissions handling (grant, deny, permanent denial), hardware camera capture, portrait/landscape orientation, and gallery deletion.
- **Accessibility & TalkBack**: Full screen traversal, button roles, spoken descriptions, and minimum 48dp touch targets.
- **Font & Display Scaling**: UI rendering and readability under **1.5x and 2.0x font scaling**, as well as small phone displays (<=360dp width).
- **Process Death & Backgrounding**: Process kill via `adb shell am kill com.spinbottle.truthdare.games` during gameplay, followed by cold launch "Resume Game".
- **Performance & Battery**: Smooth bottle spinning without frame drops (60/120fps) and low battery/thermal impact.

---

## Pull Request Summary (PR #1)

- **Title**: `[PROD] SpinScore production hardening — state, safety, billing, privacy, CI`
- **Target Branch**: `master` (from `astra/spinscore-production-hardening`)
- **Baseline**: `560b80a6e0453a57f62cbcfea9bf88a845e88212`
- **Scope**:
  - Upgraded build to API 36 and Billing Library 9.1.0.
  - Implemented cold launch game resume CTA and per-mode turn state persistence.
  - Removed coercive, physically hazardous, and privacy-invasive built-in dares.
  - Hardened Kids Safe mode with mandatory parent PIN creation and system back interception.
  - Resolved dare proof deletion leaks and tightened FileProvider and backup rules.
  - Added accessibility touch targets, semantics, and responsive scrollable layouts.
  - Created 8 unit test suites and 2 Compose instrumentation test suites.
  - Added release bundle and lint report artifact uploads in GitHub Actions CI.
- **Do Not Merge Until**:
  - All automated release gates pass cleanly.
  - All P0 and P1 audit findings are closed.
  - Release AAB is generated and inspected.
  - Manual device and Play Console release checklists are verified.
