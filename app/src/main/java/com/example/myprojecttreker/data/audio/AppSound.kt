package com.example.myprojecttreker.data.audio

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.example.myprojecttreker.R

object AppSoundId {
    const val DEFAULT = "default"
    const val SOFT = "soft"
    const val BELL = "bell"
    const val CHIME = "chime"
    const val DIGITAL = "digital"
    const val PIANO = "piano"
    const val NATURE = "nature"
}

data class AppSound(
    val id: String,
    @StringRes val titleRes: Int,
    @RawRes val rawRes: Int,
    val free: Boolean
)

object AppSounds {
    val all = listOf(
        AppSound(AppSoundId.DEFAULT, R.string.sound_default, R.raw.sound_default, true),
        AppSound(AppSoundId.SOFT, R.string.sound_soft, R.raw.sound_soft, false),
        AppSound(AppSoundId.BELL, R.string.sound_bell, R.raw.sound_bell, false),
        AppSound(AppSoundId.CHIME, R.string.sound_chime, R.raw.sound_chime, false),
        AppSound(AppSoundId.DIGITAL, R.string.sound_digital, R.raw.sound_digital, false),
        AppSound(AppSoundId.PIANO, R.string.sound_piano, R.raw.sound_piano, false),
        AppSound(AppSoundId.NATURE, R.string.sound_nature, R.raw.sound_nature, false)
    )

    fun find(id: String): AppSound = all.firstOrNull { it.id == id } ?: all.first()
    fun uri(packageName: String, sound: AppSound): String = "android.resource://$packageName/${sound.rawRes}"
    fun idFromUri(packageName: String, uri: String?): String =
        all.firstOrNull { uri == uri(packageName, it) }?.id ?: AppSoundId.DEFAULT
}
