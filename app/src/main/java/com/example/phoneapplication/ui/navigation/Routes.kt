package com.example.phoneapplication.ui.navigation

object Routes {
    const val MAIN = "main"
    const val DETAIL = "detail/{plantId}"
    const val SCANNER = "scanner"

    const val CATALOG = "catalog"

    fun detail(plantId: Long) = "detail/$plantId"
}