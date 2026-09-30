package com.example.myprojecttreker.presentation.ui.taskicon
import com.example.myprojecttreker.domain.model.TaskIconId

object TaskIconSuggester {
    fun suggest(title: String): String {
        val t = title.trim().lowercase()
        if (t.isBlank()) return TaskIconId.DEFAULT
        return when {
            any(t, "таблет", "витамин", "лекар", "medicine", "vitamin", "tablet", "medication", "medik", "medycyna") -> TaskIconId.MEDICATION
            any(t, "спорт", "трен", "заряд", "бег", "йог", "gym", "workout", "fitness", "sport", "laufen", "sport") -> TaskIconId.SPORT
            any(t, "магазин", "покуп", "продукт", "shopping", "grocery", "store", "einkauf", "compras", "zakup") -> TaskIconId.SHOPPING
            any(t, "дом", "уборк", "home", "clean", "house", "haushalt", "casa", "maison", "dom") -> TaskIconId.HOME
            any(t, "учеб", "курс", "урок", "экзам", "study", "lesson", "school", "learn", "étude", "studia") -> TaskIconId.STUDY
            any(t, "книг", "читать", "book", "read", "livre", "libro", "książ") -> TaskIconId.BOOK
            any(t, "путеш", "отпуск", "travel", "trip", "flight", "reise", "voyage", "viaggio", "podróż") -> TaskIconId.TRAVEL
            any(t, "еда", "завтрак", "обед", "ужин", "food", "breakfast", "lunch", "dinner", "essen", "comida") -> TaskIconId.FOOD
            any(t, "вода", "water", "drink", "wasser", "agua") -> TaskIconId.WATER
            any(t, "финанс", "банк", "счет", "деньги", "finance", "bank", "money", "konto", "dinero") -> TaskIconId.FINANCE
            any(t, "музык", "music", "song", "песн", "musik", "música", "muzyka") -> TaskIconId.MUSIC
            any(t, "семь", "ребен", "родител", "family", "child", "familie", "famille", "familia", "rodzina") -> TaskIconId.FAMILY
            any(t, "ноутбук", "компьют", "программ", "код", "project", "work", "работ", "office", "job", "laptop", "computer", "bureau", "praca") -> TaskIconId.LAPTOP
            any(t, "портфел", "договор", "документ", "встреч", "briefcase", "document", "meeting", "tasche") -> TaskIconId.BRIEFCASE
            any(t, "лопат", "сад", "огород", "копать", "garden", "shovel", "garten", "jardin") -> TaskIconId.SHOVEL
            else -> TaskIconId.DEFAULT
        }
    }

    private fun any(value: String, vararg words: String): Boolean =
        words.any(value::contains)
}
