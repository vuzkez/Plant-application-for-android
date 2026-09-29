package com.example.phoneapplication.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val plant by viewModel.plant.collectAsStateWithLifecycle()
    val fmt = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plant?.name ?: "Растение") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        val p = plant
        if (p == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Растение не найдено")
            }
            return@Scaffold
        }

        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(p.species, style = MaterialTheme.typography.titleMedium)
                        Text("Свет: ${p.careInfo.light}")
                        Text("Влажность: ${p.careInfo.humidity}")
                        Text("Температура: ${p.careInfo.temperature}")
                        if (p.careInfo.description.isNotBlank())
                            Text(p.careInfo.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            if (p.careGuide.isNotBlank()) {
                item {
                    Card {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Справка из API Trefle",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(p.careGuide, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            item {
                Text("Журнал ухода", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
            }
            if (p.history.isEmpty()) {
                item { Text("Пока нет записей") }
            } else {
                items(p.history.sortedByDescending { it.timestamp }) { event ->
                    Card {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(event.type.name, fontWeight = FontWeight.SemiBold)
                                Text(event.note, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(fmt.format(Date(event.timestamp)),
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}