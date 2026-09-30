# v1.10 — startup stability fix

## Runtime log diagnosis
The supplied runtime log does **not** contain an app-level `FATAL EXCEPTION` for `com.example.myprojecttreker`. It does show:

- repeated Google Play Billing `DeadObjectException` / service disconnects;
- automatic Billing reconnection activity;
- heavy main-thread frame skips;
- a Google Mobile Ads/WebView renderer crash near process shutdown.

## Changes

1. `BillingManager` no longer uses `enableAutoServiceReconnection()`.
2. Billing is **not started from `MainActivity`** anymore. It starts only when the Pro screen is opened or restore/purchase is requested.
3. Billing connection state is guarded to prevent repeated concurrent connections.
4. `MobileAds.initialize()` is removed from app startup.
5. Ad banners are disabled in **debuggable/local builds** and are initialized lazily only when an ad banner is actually rendered in a release build.
6. Existing portrait-only behavior, reminder scheduler, settings, localization, sounds and previous UI fixes are preserved.

## Important
The provided log does not prove that Billing or Ads is the sole cause of the process termination; it shows them as the strongest external-runtime failures around the time the UI became unstable. This revision removes both integrations from normal app startup so the core tracker can be tested independently.
