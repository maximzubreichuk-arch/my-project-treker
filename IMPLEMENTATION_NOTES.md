# MyProjectTreker — implementation status

This build keeps the current product functionality while preparing the codebase for a clearer Clean Architecture boundary.

## Implemented

- Portrait-only Activity (`screenOrientation=portrait` and runtime lock).
- Light/dark theme foundation.
- Typography isolated in presentation resources/components.
- Unified task icon catalog using Material vector icons.
- Automatic icon suggestion from task title; a user-selected icon is never overwritten; unknown text falls back to the universal task icon.
- Room schema migration for `Task.iconId`.
- Built-in reminder sounds in `res/raw`.
- Settings screen with theme, language, sound, vibration, default reminder for new tasks, first day of week, time format, Pro and app info.
- 15 locale resource sets: ru, en, de, fr, es, pt, it, pl, tr, nl, zh, ja, ko, hi, ar.
- FREE limit: 5 tasks per day.
- FREE COURSE limit: one additional time.
- FREE reminder sound: one built-in default sound.
- PRO entitlement model in a single app, backed by Google Play Billing integration.
- Banner ads on the Today screen only, approximately every third new session, disabled for PRO.
- Dedicated task details screen.
- Reminder system remains AlarmManager-based.
- Existing recurring-task logic, subtasks, extra times, Room storage and reboot restoration remain in place.

## Architecture refactor in v1.12

The former `core/` package has been removed. Its contents are now placed according to responsibility:

- `presentation/ui/ads` — Compose advertising UI.
- `data/ads` — advertising configuration and session persistence.
- `data/audio` — Android audio resources and preview implementation.
- `presentation/ui/taskicon` — icon UI model and catalog.
- `domain/model` — stable task icon identifiers.
- `domain/service` — platform-independent task icon suggestion logic.
- `presentation/localization` — UI locale context wrapper.
- `domain/settings` — application settings models.
- `data/subscription` — Google Play Billing and current subscription state implementation.

The architectural cleanup is intentionally performed in separate steps so each change can be built and tested independently.

## Next architecture steps

- Move day-result access behind a domain repository and remove Room entities from Presentation state.
- Separate subscription business rules from Billing/SharedPreferences implementation.
- Introduce a domain reminder-scheduler abstraction and keep AlarmManager implementation in Data.
- Move reminder-time calculation into Domain.
- Remove UI state such as `isExpanded` from the domain `Task` model.
- Replace Android-specific sound URI data in the domain model with a platform-independent sound identifier.
- Reduce direct Data dependencies from Compose screens and ViewModels.
- Separate reminder orchestration from `RoomTaskRepository` where appropriate.

## Production configuration still required

1. Create the subscription product `myprojecttreker_pro_monthly` in Google Play Console and configure its offer/price.
2. Replace the Google test AdMob App ID and banner unit ID in `AndroidManifest.xml` / `data/ads/AdsConfig.kt` with production IDs before release.
3. Purchase verification should be hardened with a server/backend before production monetization.
4. The additional locale resources should receive native proofreading before publishing localized builds.
5. Run a real Gradle/Android build and device tests after dependencies are downloaded; this environment cannot download the Gradle 9.3.1 distribution.
