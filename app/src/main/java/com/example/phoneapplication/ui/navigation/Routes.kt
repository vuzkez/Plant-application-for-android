package com.example.phoneapplication.ui.navigation

object Routes {
    const val MAIN = "main"
    const val DETAIL = "detail/{plantId}"
    const val SCANNER = "scanner"
    const val VOICE = "voice"
    const val DIAGNOSIS = "diagnosis/{plantId}"

    const val CATALOG = "catalog"

    fun detail(plantId: Long) = "detail/$plantId"
    fun diagnosis(plantId: Long) = "diagnosis/$plantId"
}