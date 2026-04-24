package com.example.myprojecttreker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myprojecttreker.data.local.database.DateConverters
import com.example.myprojecttreker.data.local.dao.DayResultDao
import com.example.myprojecttreker.data.local.entity.DayResultEntity
import com.example.myprojecttreker.data.local.dao.SubTaskDao
import com.example.myprojecttreker.data.local.entity.SubTaskEntity
import com.example.myprojecttreker.data.local.dao.TaskDao
import com.example.myprojecttreker.data.local.entity.TaskEntity

/**
 * Главная база данных приложения.
 * Содержит таблицы задач, подзадач и результатов выполнения.
 */
@Database(
    entities = [
        TaskEntity::class,
        SubTaskEntity::class,
        DayResultEntity::class
    ],
    version = 4
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {

    // DAO для работы с задачами
    abstract fun taskDao(): TaskDao

    // DAO для работы с подзадачами
    abstract fun subTaskDao(): SubTaskDao

    // DAO для работы с результатами по дням
    abstract fun dayResultDao(): DayResultDao

    companion object {

        // Singleton экземпляр базы данных
        @Volatile
        private var INSTANCE: AppDatabase? = null



        // Получение экземпляра базы данных
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tasks_db"
                )
                    // При изменении схемы БД данные будут удалены
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}