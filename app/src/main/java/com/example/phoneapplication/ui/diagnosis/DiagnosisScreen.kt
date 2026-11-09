package com.example.phoneapplication.ui.diagnosis

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val QUICK_SYMPTOMS = listOf(
    "Желтеют нижние листья",
    "Сохнет земля",
    "Пятна на листьях",
    "Вянут листья"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisScreen(
    onBack: () -> Unit,
    viewModel: DiagnosisViewModel = hiltViewModel()
) {
    val plant by viewModel.plant.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    var symptoms by rememberSaveable { mutableStateOf("") }
    val fmt = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    val isLoading = state is DiagnosisUiState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Диагностика") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    plant?.let { "${it.name} — ${it.species}" } ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!viewModel.isLlmConfigured) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            "Ключ LLM_API_KEY не задан в local.properties. " +
                                "Можно смотреть только сохранённые ответы.",
                            Modifier.padding(16.dp)
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = symptoms,
                    onValueChange = { symptoms = it },
                    label = { Text("Опишите симптомы") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 5,
                    enabled = !isLoading
                )
            }

            item {
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QUICK_SYMPTOMS.forEach { text ->
                        AssistChip(onClick = { symptoms = text }, label = { Text(text) })
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.diagnose(symptoms) },
                    enabled = symptoms.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Определить причину") }
            }

            item {
                when (val s = state) {
                    DiagnosisUiState.Idle -> Unit

                    DiagnosisUiState.Loading -> Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(Modifier.size(24.dp))
                        Text("Садовод думает…")
                    }

                    is DiagnosisUiState.Success -> Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (s.fromCache) {
                                Text(
                                    "Ответ из памяти приложения",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(s.text)
                            if (s.fromCache) {
                                OutlinedButton(onClick = { viewModel.diagnose(symptoms, forceRefresh = true) }) {
                                    Text("Спросить заново")
                                }
                            }
                        }
                    }

                    is DiagnosisUiState.Error -> Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(s.message, Modifier.padding(16.dp))
                    }
                }
            }

            if (history.isNotEmpty()) {
                item {
                    Text(
                        "Прошлые диагнозы",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(history) { d ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "${fmt.format(Date(d.createdAt))} · ${d.symptoms}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(d.result, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
