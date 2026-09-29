package com.example.phoneapplication.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrefleSearchResponse(
    @SerialName("data") val data: List<TreflePlant>? = null,
    @SerialName("meta") val meta: TrefleMeta? = null
)

@Serializable
data class TreflePlant(
    @SerialName("id") val id: Int? = null,
    @SerialName("common_name") val commonName: String? = null,
    @SerialName("scientific_name") val scientificName: String? = null,
    @SerialName("family") val family: String? = null,
    @SerialName("family_common_name") val familyCommonName: String? = null,
    @SerialName("genus") val genus: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("year") val year: Int? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("rank") val rank: String? = null,
    @SerialName("observation") val observation: String? = null
)

@Serializable
data class TrefleMeta(
    @SerialName("total") val total: Int? = null
)