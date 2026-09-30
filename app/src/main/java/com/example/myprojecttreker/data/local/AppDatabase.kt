package com.example.myprojecttreker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.TypeConverters

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
    version = 6
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

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN reminderOffsetDays INTEGER")
                db.execSQL("ALTER TABLE tasks ADD COLUMN reminderOffsetHours INTEGER")
                db.execSQL("ALTER TABLE tasks ADD COLUMN reminderOffsetMinutes INTEGER")
            }
        }


        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN iconId TEXT NOT NULL DEFAULT 'default'")
            }
        }

        // Получение экземпляра базы данных
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tasks_db"
                )
                    // При изменении схемы БД данные будут удалены
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}