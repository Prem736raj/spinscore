# Privacy Policy — Spin Bottle

**Effective date:** September 20, 2026

Spin Bottle is designed as a primarily local/offline party game. This policy describes the behavior implemented in the application repository. Google Play Console declarations and the final published store listing must be kept consistent with the release that is actually shipped.

## Data the app stores locally

Depending on the features you use, the app can store the following on your device:

- player names, avatars, local profile statistics, and game preferences;
- custom Truth or Dare prompts, favorites, prompt history, and enabled prompt packs;
- theme and bottle selections;
- adult-content confirmation state and the local parent/content-gate PIN;
- an active game-session snapshot used to recover from process death;
- optional dare-proof photos and their local metadata;
- locally derived Google Play premium/supporter entitlement state while the app is running.

The app does not currently include a developer-operated account system, analytics SDK, advertising SDK, remote multiplayer backend, Firebase, Supabase, or a custom telemetry service.

## Camera and dare-proof photos

Camera access is optional and is requested only when you choose to capture a dare-proof photo.

Dare-proof photos are stored in the app's private internal storage. The app does not upload those photos to a developer server. A photo leaves the app only when you explicitly choose the Share action and select another app or destination through Android.

Deleting a dare proof from the gallery deletes both its saved metadata and its underlying image file.

## Android backup and device transfer

The current backup configuration allows selected SharedPreferences data, such as ordinary settings and local game customization, to participate in Android backup/device-transfer mechanisms when the device and Google account support them.

The active game-session snapshot, dare-proof photos, dare-proof metadata, and the DataStore that contains the PIN/adult-content state are not included by the app's current backup rules.

Android/Google may process eligible backup data under the user's platform settings and Google's applicable terms.

## Google Play Billing

The app uses Google Play Billing for its supporter/premium purchase flow. Purchase processing, payment information, account information, refunds, subscription status, and transaction infrastructure are handled by Google Play.

The application queries Google Play for purchase ownership so that eligible purchases can be restored and entitlement can be derived correctly. The developer does not operate a separate payment server in the current implementation.

## Permissions

### Camera

Optional. Used only for dare-proof capture.

### Vibration

Used for gameplay haptic feedback and can be disabled in app settings.

No permission or manifest comment should be interpreted as evidence of online multiplayer; the current product does not implement an online multiplayer backend.

## Sharing

The app does not intentionally sell personal data or send gameplay data to a developer-operated server.

When you explicitly use Android sharing for a dare-proof photo, the selected receiving app becomes responsible for the copy you share under that app's own privacy practices.

## Children and mature content

The app includes a Kids Safe gameplay mode as well as mature/adult-gated content in other modes. Kids Safe uses a dedicated prompt path and a parent PIN exit boundary.

The adult-content checkbox is a self-confirmation only; it is not identity verification or robust proof of age.

The final Google Play target-audience, content-rating, Families-policy applicability, and Data Safety declarations must be configured accurately in Play Console before release.

## Data deletion

You can delete individual dare-proof photos from the in-app Dare Gallery.

Other local application data can be removed through available in-app reset controls where provided, or by clearing the application's storage/uninstalling the application through Android. Android backup may restore data that was eligible for platform backup, subject to the user's device/account backup settings.

## Security

The parent/content-gate PIN is intended as a lightweight local content boundary, not as high-security authentication. It should not be reused as a banking, device-unlock, or other sensitive PIN.

## Changes

If future versions add analytics, advertising, cloud accounts, online multiplayer, remote prompt services, or any other network data processing, this policy and the Google Play Data Safety disclosures must be updated before that behavior is released.

## Contact

A support/contact address should be added here before publishing this policy as the production Google Play privacy-policy URL.
