package com.example.phoneapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.phoneapplication.ui.detail.DetailScreen
import com.example.phoneapplication.ui.main.MainScreen
import com.example.phoneapplication.ui.scanner.ScannerScreen

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.MAIN) {

        // Главный экран
        composable(Routes.MAIN) {
            MainScreen(
                onPlantClick = { id -> nav.navigate(Routes.detail(id)) },
                onScanClick = { nav.navigate(Routes.SCANNER) }
            )
        }

        // Детальный экран
        composable(
            Routes.DETAIL,
            arguments = listOf(navArgument("plantId") { type = NavType.LongType })
        ) {
            DetailScreen(onBack = { nav.popBackStack() })
        }

        // Сканер этикетки
        composable(Routes.SCANNER) {
            ScannerScreen(
                onBack = { nav.popBackStack() },
                onPlantAdded = { nav.popBackStack() }
            )
        }
    }
}