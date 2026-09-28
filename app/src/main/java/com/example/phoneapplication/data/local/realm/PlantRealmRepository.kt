package com.example.phoneapplication.data.local.realm

import com.example.phoneapplication.data.local.realm.objects.FertilizerType
import com.example.phoneapplication.data.local.realm.objects.PlantFamily
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.ext.asFlow
import io.github.xilinjia.krdb.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRealmRepository @Inject constructor(
    private val realm: Realm
) {
    fun observeFamilies(): Flow<List<PlantFamily>> =
        realm.query<PlantFamily>().asFlow().map { it.list }

    fun observeFertilizers(): Flow<List<FertilizerType>> =
        realm.query<FertilizerType>().asFlow().map { it.list }

    suspend fun seedIfEmpty() {
        val families = realm.query<PlantFamily>().find()
        if (families.isNotEmpty()) return

        realm.write {
            copyToRealm(PlantFamily().apply {
                id = "cactaceae"; name = "Кактусовые"; iconName = "🌵"
            })
            copyToRealm(PlantFamily().apply {
                id = "orchidaceae"; name = "Орхидные"; iconName = "🌸"
            })
            copyToRealm(PlantFamily().apply {
                id = "araceae"; name = "Ароидные"; iconName = "🌿"
            })
            copyToRealm(FertilizerType().apply {
                id = "nitrogen"; name = "Азотное"; iconName = "N"
            })
            copyToRealm(FertilizerType().apply {
                id = "phosphorus"; name = "Фосфорное"; iconName = "P"
            })
            copyToRealm(FertilizerType().apply {
                id = "potassium"; name = "Калийное"; iconName = "K"
            })
        }
    }
}