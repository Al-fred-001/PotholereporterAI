// AppDatabase.kt
package com.potholereporter.ai.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room Database for the Pothole Reporter AI app.
 * It holds the single table: pothole_reports.
 */
@Database(entities = [PotholeReport::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Use Singleton pattern to ensure only one instance of the database exists
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pothole_reporter_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}