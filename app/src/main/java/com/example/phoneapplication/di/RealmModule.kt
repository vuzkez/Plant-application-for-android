package com.example.phoneapplication.di

import com.example.phoneapplication.data.local.realm.objects.FertilizerType
import com.example.phoneapplication.data.local.realm.objects.PlantFamily
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    @Provides
    @Singleton
    fun provideRealm(): Realm {
        val config = RealmConfiguration.Builder(
            schema = setOf(PlantFamily::class, FertilizerType::class)
        )
            .name("catalog.realm")
            .build()
        return Realm.open(config)
    }
}