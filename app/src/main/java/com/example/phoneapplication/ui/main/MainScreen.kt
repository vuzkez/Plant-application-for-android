package com.example.phoneapplication.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onPlantClick: (Long) -> Unit,
    onScanClick: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val plants by viewModel.plants.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let(viewModel::onDateSelected)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Календарь полива", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onScanClick) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider()
                    Text(
                        "Статус всех растений на выбранную дату:",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            items(plants, key = { it.id }) { plant ->
                PlantCard(
                    plant = plant,
                    referenceDate = selectedDate,
                    onClick = { onPlantClick(plant.id) },
                    onWaterClick = { viewModel.waterPlant(plant.id) }
                )
            }
        }
    }
}