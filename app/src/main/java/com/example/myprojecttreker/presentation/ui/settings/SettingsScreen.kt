package com.example.myprojecttreker.presentation.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.myprojecttreker.R
import com.example.myprojecttreker.data.audio.AppSoundId
import com.example.myprojecttreker.data.audio.AppSounds
import com.example.myprojecttreker.data.audio.SoundPreviewer
import com.example.myprojecttreker.domain.settings.AppThemeMode
import com.example.myprojecttreker.data.subscription.BillingManager
import com.example.myprojecttreker.data.subscription.SubscriptionManager
import com.example.myprojecttreker.data.settings.SettingsManager
import com.example.myprojecttreker.presentation.ui.pro.ProScreen

private data class AppLanguage(val tag: String, val title: String)

private val languages = listOf(
    AppLanguage("ru", "Русский"), AppLanguage("en", "English"),
    AppLanguage("de", "Deutsch"), AppLanguage("fr", "Français"),
    AppLanguage("es", "Español"), AppLanguage("pt", "Português"),
    AppLanguage("it", "Italiano"), AppLanguage("pl", "Polski"),
    AppLanguage("tr", "Türkçe"), AppLanguage("nl", "Nederlands"),
    AppLanguage("zh", "中文"), AppLanguage("ja", "日本語"),
    AppLanguage("ko", "한국어"), AppLanguage("hi", "हिन्दी"),
    AppLanguage("ar", "العربية")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    subscriptionManager: SubscriptionManager,
    billingManager: BillingManager,
    onBack: () -> Unit,
    onOpenPro: () -> Unit
) {
    val context = LocalContext.current
    val settings by settingsManager.settings.collectAsState()
    val isPro by subscriptionManager.isPro.collectAsState()
    var showLanguage by remember { mutableStateOf(false) }
    var showSounds by remember { mutableStateOf(false) }
    var showTheme by remember { mutableStateOf(false) }
    var showDefaultReminder by remember { mutableStateOf(false) }
    var showFirstDay by remember { mutableStateOf(false) }
    var showTimeFormat by remember { mutableStateOf(false) }
    var showPro by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Text(stringResource(R.string.appearance), style = MaterialTheme.typography.titleMedium) }
            item {
                SettingsRow(Icons.Default.DarkMode, stringResource(R.string.theme), when (settings.themeMode) {
                    AppThemeMode.LIGHT -> stringResource(R.string.light)
                    AppThemeMode.DARK -> stringResource(R.string.dark)
                    AppThemeMode.SYSTEM -> stringResource(R.string.system)
                }) { showTheme = true }
            }
            item {
                SettingsRow(Icons.Default.Language, stringResource(R.string.language), languages.firstOrNull { it.tag == settings.languageTag }?.title ?: settings.languageTag) { showLanguage = true }
            }

            item { Spacer(Modifier.height(4.dp)); Text(stringResource(R.string.notifications), style = MaterialTheme.typography.titleMedium) }
            item {
                SettingsRow(Icons.Default.VolumeUp, stringResource(R.string.notification_sound), soundTitle(context, settings.defaultSoundUri)) { showSounds = true }
            }
            item {
                SwitchRow(
                    icon = Icons.Default.Vibration,
                    title = stringResource(R.string.vibration),
                    checked = settings.vibrationEnabled
                ) {
                    settingsManager.setVibrationEnabled(it)
                }
            }
            item {
                val notificationsEnabled = if (Build.VERSION.SDK_INT >= 33) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }

                SettingsRow(
                    icon = Icons.Default.Notifications,
                    title = stringResource(R.string.notification_access),
                    value = if (notificationsEnabled) {
                        stringResource(R.string.enabled)
                    } else {
                        stringResource(R.string.disabled)
                    },
                    onClick = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    }
                )
            }
            item { Spacer(Modifier.height(4.dp)); Text(stringResource(R.string.tasks_settings), style = MaterialTheme.typography.titleMedium) }
            item {
                SettingsRow(Icons.Default.AccessTime, stringResource(R.string.default_reminder), "${settings.defaultReminderMinutes} мин") { showDefaultReminder = true }
            }
            item {
                SettingsRow(Icons.Default.Language, stringResource(R.string.first_day_of_week), if (settings.firstDayOfWeek == 1) stringResource(R.string.monday) else stringResource(R.string.sunday)) { showFirstDay = true }
            }
            item {
                SettingsRow(Icons.Default.AccessTime, stringResource(R.string.time_format), if (settings.timeFormat24Hour) stringResource(R.string.twenty_four_hour) else stringResource(R.string.twelve_hour)) { showTimeFormat = true }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth().clickable { showPro = true }) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.padding(4.dp))
                            Text(if (isPro) "PRO активен" else stringResource(R.string.pro), style = MaterialTheme.typography.titleMedium)
                        }
                        Text(stringResource(R.string.pro_title), style = MaterialTheme.typography.bodyMedium)
                        Text(stringResource(R.string.pro_tasks), style = MaterialTheme.typography.bodySmall)
                        Text(stringResource(R.string.pro_sounds), style = MaterialTheme.typography.bodySmall)
                        Text(stringResource(R.string.pro_no_ads), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item { Spacer(Modifier.height(4.dp)); Text(stringResource(R.string.about), style = MaterialTheme.typography.titleMedium) }
            item { SettingsRow(Icons.Default.Info, stringResource(R.string.version), "1.0") { } }
            item { SettingsRow(Icons.Default.Info, stringResource(R.string.privacy), "") { } }
            item { SettingsRow(Icons.Default.Info, stringResource(R.string.support), "") { } }
        }
    }

    if (showTheme) {
        ChoiceDialog(stringResource(R.string.theme), listOf(AppThemeMode.LIGHT, AppThemeMode.DARK, AppThemeMode.SYSTEM),
            { when (it) { AppThemeMode.LIGHT -> stringResource(R.string.light); AppThemeMode.DARK -> stringResource(R.string.dark); AppThemeMode.SYSTEM -> stringResource(R.string.system) } },
            settings.themeMode,
            { settingsManager.setThemeMode(it); showTheme = false },
            { showTheme = false }
        )
    }

    if (showLanguage) {
        AlertDialog(onDismissRequest = { showLanguage = false }, title = { Text(stringResource(R.string.language)) },
            text = {
                Column { languages.forEach { lang ->
                    val locked = !subscriptionManager.canUseLanguage(lang.tag)
                    Row(Modifier.fillMaxWidth().clickable(enabled = !locked) {
                        settingsManager.setLanguageTag(lang.tag); showLanguage = false; (context as? Activity)?.recreate()
                    }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = settings.languageTag == lang.tag, onClick = null)
                        Text(lang.title, Modifier.weight(1f))
                        if (locked) Text("PRO", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                    }
                }}
            }, confirmButton = { TextButton(onClick = { showLanguage = false }) { Text(stringResource(R.string.close)) } })
    }

    if (showSounds) {
        val previewer = remember { SoundPreviewer(context) }
        DisposableEffect(Unit) { onDispose { previewer.stop() } }
        AlertDialog(onDismissRequest = { previewer.stop(); showSounds = false }, title = { Text(stringResource(R.string.notification_sound)) },
            text = {
                Column { AppSounds.all.forEach { sound ->
                    val locked = !isPro && !sound.free
                    val selected = AppSounds.uri(context.packageName, sound) == settings.defaultSoundUri || (!settings.defaultSoundUri.isNullOrBlank() && AppSounds.idFromUri(context.packageName, settings.defaultSoundUri) == sound.id)
                    Row(Modifier.fillMaxWidth().clickable(enabled = !locked) {
                        settingsManager.saveDefaultSound(AppSounds.uri(context.packageName, sound)); showSounds = false
                    }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selected, onClick = null)
                        Text(stringResource(sound.titleRes), Modifier.weight(1f))
                        TextButton(onClick = { if (!locked) previewer.play(sound) }) { Text("▶") }
                        if (locked) Text("PRO", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                    }
                    Divider()
                }}
            }, confirmButton = { TextButton(onClick = { previewer.stop(); showSounds = false }) { Text(stringResource(R.string.close)) } })
    }

    if (showDefaultReminder) {
        ChoiceDialog(stringResource(R.string.default_reminder), listOf(0,5,10,15,30,60), { if (it == 0) "Выкл." else "$it мин" }, settings.defaultReminderMinutes,
            { settingsManager.setDefaultReminderMinutes(it); showDefaultReminder = false }, { showDefaultReminder = false })
    }

    if (showFirstDay) {
        ChoiceDialog(stringResource(R.string.first_day_of_week), listOf(1,7), { if (it == 1) stringResource(R.string.monday) else stringResource(R.string.sunday) }, settings.firstDayOfWeek,
            { settingsManager.setFirstDayOfWeek(it); showFirstDay = false }, { showFirstDay = false })
    }

    if (showTimeFormat) {
        ChoiceDialog(stringResource(R.string.time_format), listOf(true,false), { if (it) stringResource(R.string.twenty_four_hour) else stringResource(R.string.twelve_hour) }, settings.timeFormat24Hour,
            { settingsManager.setTimeFormat24Hour(it); showTimeFormat = false }, { showTimeFormat = false })
    }

    if (showPro) {
        AlertDialog(onDismissRequest = { showPro = false }, title = { Text(stringResource(R.string.pro_title)) },
            text = { Text("${stringResource(R.string.pro_tasks)}\n${stringResource(R.string.pro_languages)}\n${stringResource(R.string.pro_sounds)}\n${stringResource(R.string.pro_extra_times)}\n${stringResource(R.string.pro_no_ads)}") },
            confirmButton = { Button(onClick = { showPro = false; onOpenPro() }) { Text(stringResource(R.string.try_pro)) } },
            dismissButton = { TextButton(onClick = { showPro = false }) { Text(stringResource(R.string.close)) } })
    }
}

@Composable
private fun <T> ChoiceDialog(
    title: String,
    options: List<T>,
    label: @Composable (T) -> String,
    selected: T,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = {
        Column { options.forEach { option ->
            Row(Modifier.fillMaxWidth().clickable { onSelect(option) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = option == selected, onClick = { onSelect(option) })
                Text(label(option))
            }
        }}
    }, confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) } })
}

@Composable
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.padding(5.dp))
            Text(title, Modifier.weight(1f))
            if (value.isNotBlank()) Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SwitchRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.padding(5.dp))
            Text(title, Modifier.weight(1f))
            Switch(checked = checked, onCheckedChange = onChange)
        }
    }
}

private fun soundTitle(context: Context, uri: String?): String {
    if (uri == null) return context.getString(R.string.sound_default)
    val id = AppSounds.idFromUri(context.packageName, uri)
    return context.getString(AppSounds.find(id).titleRes)
}
