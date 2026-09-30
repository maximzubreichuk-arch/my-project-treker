package com.example.myprojecttreker.presentation.ui.pro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.myprojecttreker.R
import com.example.myprojecttreker.data.subscription.BillingManager
import com.example.myprojecttreker.data.subscription.SubscriptionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProScreen(
    subscriptionManager: SubscriptionManager,
    billingManager: BillingManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        billingManager.start()
    }
    val isPro by subscriptionManager.isPro.collectAsState()
    val product by billingManager.product.collectAsState()
    val price = product?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.lastOrNull()?.formattedPrice

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("MyProjectTreker Pro") },
            navigationIcon = { androidx.compose.material3.IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
        )
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth())
            Text(stringResourceSimple(context, R.string.pro_title), style = MaterialTheme.typography.headlineSmall)
            listOf(R.string.pro_tasks,R.string.pro_languages,R.string.pro_sounds,R.string.pro_extra_times,R.string.pro_no_ads).forEach { res ->
                androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.padding(4.dp))
                    Text(stringResourceSimple(context, res))
                }
            }
            Spacer(Modifier.height(10.dp))
            if (isPro) {
                Text("PRO уже активен", style = MaterialTheme.typography.titleLarge)
            } else {
                Text(price ?: stringResourceSimple(context, R.string.pro_price_unavailable))
                Button(onClick = { (context as? android.app.Activity)?.let(billingManager::launchPurchase) }, Modifier.fillMaxWidth()) {
                    Text(stringResourceSimple(context, R.string.buy))
                }
                OutlinedButton(onClick = { billingManager.restorePurchases() }, Modifier.fillMaxWidth()) {
                    Text(stringResourceSimple(context, R.string.restore))
                }
            }
        }
    }
}

private fun stringResourceSimple(context: android.content.Context, id: Int): String = context.getString(id)
