package com.example.myprojecttreker.data.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

/**
 * Loads a banner only in release/non-debug builds.
 * This keeps local development independent from WebView/GMS initialization.
 */
@Composable
fun AdBanner(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (!AdsConfig.isEnabled(context)) return

    Card(modifier = modifier.fillMaxWidth().heightIn(min = 50.dp, max = 110.dp)) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                MobileAds.initialize(ctx.applicationContext) {}
                AdView(ctx).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = AdsConfig.BANNER_UNIT_ID
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
