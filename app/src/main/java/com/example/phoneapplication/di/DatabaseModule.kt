package com.example.phoneapplication.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.phoneapplication.data.local.room.PlantDatabase
import com.example.phoneapplication.data.local.room.dao.CareEventDao
import com.example.phoneapplication.data.local.room.dao.DiagnosisDao
import com.example.phoneapplication.data.local.room.dao.PlantDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // Версия 2 -> 3: добавилась таблица с кэшем диагнозов (данные растений сохраняются)
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS diagnoses (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "plantId INTEGER NOT NULL, " +
                    "symptoms TEXT NOT NULL, " +
                    "symptomsKey TEXT NOT NULL, " +
                    "result TEXT NOT NULL, " +
                    "createdAt INTEGER NOT NULL)"
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PlantDatabase =
        Room.databaseBuilder(
            context,
            PlantDatabase::class.java,
            "plant_organizer.db"
        )
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePlantDao(db: PlantDatabase): PlantDao = db.plantDao()

    @Provides
    fun provideCareEventDao(db: PlantDatabase): CareEventDao = db.careEventDao()

    @Provides
    fun provideDiagnosisDao(db: PlantDatabase): DiagnosisDao = db.diagnosisDao()
}
