package com.example.myprojecttreker.data.local.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Конвертеры для Room.
 * Позволяют сохранять типы LocalDate, LocalTime и LocalDateTime.
 * */
@RequiresApi(Build.VERSION_CODES.O)
class DateConverters {

    // Преобразование LocalDate в String
  @TypeConverter
  fun fromLocalDate(date: LocalDate?): String? =
      date?.toString()

    // Преобразование String в LocalDate
  @TypeConverter
  fun toLocalDate(value: String?): LocalDate? =
      value?.let { LocalDate.parse(it) }

    // Преобразование LocalTime в String
  @TypeConverter
  fun fromLocalTime(time: LocalTime?): String? =
      time?.toString()

    // Преобразование String в LocalTime
  @TypeConverter
  fun toLocalTime(value: String?): LocalTime? =
      value?.let { LocalTime.parse(it) }

    // Преобразование LocalDateTime в String
  @TypeConverter
  fun fromLocalDateTime(dateTime: LocalDateTime?): String? =
      dateTime?.toString()

    // Преобразование String в LocalDateTime
  @TypeConverter
  fun toLocalDateTime(value: String?): LocalDateTime? =
      value?.let { LocalDateTime.parse(it) }
}