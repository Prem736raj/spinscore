# Spin Bottle

Spin Bottle is a local-first Android party game for Truth or Dare, bottle spinning, Quick Fire, Couples, Kids Safe, custom prompts, prompt packs, scoring, themes, and optional dare-proof photos.

> Repository name: **SpinScore**  
> Current Android app name: **Spin Bottle**  
> Application ID: `com.spinbottle.truthdare.games`

The repository/app naming mismatch is intentionally left unchanged during production hardening. Changing the application ID can affect Play Store identity and is outside this hardening pass.

## Current status

Production hardening is in progress on:

`astra/spinscore-production-hardening`

The evidence-backed audit and open backlog are tracked in [AUDIT_FINDINGS.md](AUDIT_FINDINGS.md).

Do not treat the app as release-ready until the CI release gates, required device checks, and Play Console checks pass.

## Features

- Bottle-based player selection
- Truth and Dare prompts with difficulty levels
- Classic and Quick Fire gameplay
- Couples mode
- Kids Safe mode with a parent PIN exit boundary
- Player scores and session statistics
- Prompt packs, favorites, history, and custom prompts
- Persistent gameplay settings
- Themes and bottle customization
- Optional camera-based dare proof stored in app-private storage
- Google Play Billing supporter purchase flow

## Tech stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Android DataStore / SharedPreferences
- Kotlin Coroutines
- Gson
- Coil
- Google Play Billing
- Java 17
- R8 and resource shrinking

The app is intentionally single-module and primarily local/offline. No backend, Firebase, Supabase, analytics platform, or online multiplayer stack is required by the current product.

## Requirements

- JDK 17
- Android SDK with API 36 installed
- Android Studio compatible with AGP 8.13.x

## Build

### Linux / macOS

```bash
./gradlew clean
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
./gradlew lintRelease
./gradlew assembleRelease
./gradlew bundleRelease
```

### Windows

```powershell
gradlew.bat clean
gradlew.bat testDebugUnitTest
gradlew.bat lintDebug
gradlew.bat assembleDebug
gradlew.bat lintRelease
gradlew.bat assembleRelease
gradlew.bat bundleRelease
```

No developer-specific JDK path should be committed to `gradle.properties`.

## Architecture

The application uses a pragmatic local-first architecture:

- Compose screens for UI
- Navigation Compose for app routing
- focused managers/holders for local product state
- persistent settings and active-session snapshots where lifecycle reliability requires them
- app-private files for dare-proof photos
- Google Play ownership as the authority for Play purchases

The hardening work deliberately avoids adding Hilt, Room, cloud accounts, a backend, or extra Gradle modules without a demonstrated product need.

## Privacy

Most gameplay data remains on the device. This includes player/game preferences, custom prompts, prompt history, and optional dare-proof photos.

Dare-proof photos are stored under the app's private files directory and are shared only through a scoped FileProvider URI when the user explicitly chooses Share. Proof photos and active game-session snapshots are excluded from the current backup policy.

Google Play Billing involves Google Play platform processing; Play Console Data Safety and privacy-policy declarations must be verified before release.

## Testing

Current hardening tests prioritize behavior with the highest release risk, including:

- bottle rotation to player-index mapping and distribution bounds
- premium entitlement aggregation
- prompt/content safety regression checks
- PIN input validation

Device verification is still required for camera flows, TalkBack, font scaling, edge-to-edge, predictive Back, screen-size behavior, proof-gallery memory, and rendering/battery measurements.

## CI

GitHub Actions runs:

- clean
- debug unit tests
- debug lint
- debug assembly
- release lint
- release assembly
- release bundle

The workflow does not require signing secrets for ordinary pull-request validation.

## Screenshots

Store-ready screenshots are not committed yet.

- Home
- Player setup
- Mode selection
- Bottle/gameplay
- Kids Safe
- Quick Fire
- Dare Gallery
- Premium/supporter screen

## Contributing

1. Branch from the current audited base.
2. Keep changes focused and evidence-driven.
3. Add or update tests for behavior changes.
4. Run the relevant Gradle gates.
5. Never commit keystores, passwords, signing credentials, or Play Console secrets.
6. Do not change `applicationId` casually.

## Release

Before a Play release, verify the repository release gates plus:

- Play App Signing / upload key
- exact Billing product configuration and test purchases
- target audience and content rating
- Data Safety and privacy-policy URL
- store listing copy and assets
- internal/closed testing track
- device-level camera, accessibility, performance, and lifecycle checks

See [AUDIT_FINDINGS.md](AUDIT_FINDINGS.md) for the current engineering backlog.
