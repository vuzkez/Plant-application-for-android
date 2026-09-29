package com.example.phoneapplication.data.remote

import com.example.phoneapplication.data.remote.dto.TrefleSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PlantApi {

    @GET("species/search")
    suspend fun searchSpecies(
        @Query("q") query: String,
        @Header("Authorization") auth: String
    ): TrefleSearchResponse
}