package com.example.myprojecttreker.presentation.localization

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleContextWrapper {
    fun wrap(context: Context, languageTag: String): Context {
        if (languageTag.isBlank()) return context
        val locale = Locale.forLanguageTag(languageTag)
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales(android.os.LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            configuration.locale = locale
        }
        return context.createConfigurationContext(configuration)
    }
}
