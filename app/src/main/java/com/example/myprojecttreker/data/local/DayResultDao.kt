package com.example.myprojecttreker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.LocalDate



/**
 * DAO для работы с результатами выполнения задач по дням
  */
@Dao
interface DayResultDao {

    // Получить результаты за конкретную дату
    @Query("SELECT * FROM day_results WHERE date = :date")
    suspend fun getByDate(date: LocalDate): List<DayResultEntity>

    //Сохранить результат выполнения задачи
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: DayResultEntity)

    //Получить результаты за период
    @Query("""
    SELECT * FROM day_results 
    WHERE date BETWEEN :start AND :end
""")
    suspend fun getForPeriod(
        start: LocalDate,
        end: LocalDate
    ): List<DayResultEntity>
}