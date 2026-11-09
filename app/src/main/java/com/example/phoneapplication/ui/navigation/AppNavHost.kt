package com.example.phoneapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.phoneapplication.ui.detail.DetailScreen
import com.example.phoneapplication.ui.diagnosis.DiagnosisScreen
import com.example.phoneapplication.ui.main.MainScreen
import com.example.phoneapplication.ui.scanner.ScannerScreen
import com.example.phoneapplication.ui.voice.VoiceScreen
import com.example.phoneapplication.ui.catalog.CatalogScreen

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.MAIN) {

        // Главный экран
        composable(Routes.MAIN) {
            MainScreen(
                onPlantClick = { id -> nav.navigate(Routes.detail(id)) },
                onScanClick = { nav.navigate(Routes.SCANNER) },
                onCatalogClick = { nav.navigate(Routes.CATALOG) },
                onVoiceClick = { nav.navigate(Routes.VOICE) }
            )
        }

        // Детальный экран
        composable(
            Routes.DETAIL,
            arguments = listOf(navArgument("plantId") { type = NavType.LongType })
        ) { entry ->
            val plantId = entry.arguments?.getLong("plantId") ?: 0L
            DetailScreen(
                onBack = { nav.popBackStack() },
                onDiagnosisClick = { nav.navigate(Routes.diagnosis(plantId)) }
            )
        }

        // Диагностика растения (LLM)
        composable(
            Routes.DIAGNOSIS,
            arguments = listOf(navArgument("plantId") { type = NavType.LongType })
        ) {
            DiagnosisScreen(onBack = { nav.popBackStack() })
        }

        // Сканер этикетки
        composable(Routes.SCANNER) {
            ScannerScreen(
                onBack = { nav.popBackStack() },
                onPlantAdded = { nav.popBackStack() }
            )
        }

        // Голосовой ввод ухода
        composable(Routes.VOICE) {
            VoiceScreen(onBack = { nav.popBackStack() })
        }

        // Справочник
        composable(Routes.CATALOG) {
            CatalogScreen(onBack = { nav.popBackStack() })
        }
    }
}