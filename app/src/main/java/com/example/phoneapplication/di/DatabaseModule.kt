package com.example.phoneapplication.di

import android.content.Context
import androidx.room.Room
import com.example.phoneapplication.data.local.room.PlantDatabase
import com.example.phoneapplication.data.local.room.dao.CareEventDao
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PlantDatabase =
        Room.databaseBuilder(
            context,
            PlantDatabase::class.java,
            "plant_organizer.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePlantDao(db: PlantDatabase): PlantDao = db.plantDao()

    @Provides
    fun provideCareEventDao(db: PlantDatabase): CareEventDao = db.careEventDao()
}