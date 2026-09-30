package com.example.myprojecttreker.data.ads

object AdsConfig {
    /** Ads are disabled for local/debug builds to keep startup independent from WebView/GMS. */
    fun isEnabled(context: android.content.Context): Boolean =
        (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) == 0

    // Google test IDs. Replace with your own App ID and ad unit ID before production.
    const val APPLICATION_ID = "ca-app-pub-3940256099942544~3347511713"
    const val BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
}
