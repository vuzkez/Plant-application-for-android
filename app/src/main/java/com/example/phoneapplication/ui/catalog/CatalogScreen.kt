package com.example.phoneapplication.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onBack: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val families by viewModel.families.collectAsStateWithLifecycle()
    val fertilizers by viewModel.fertilizers.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Справочник (Realm)") },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Семейства растений",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
            }
            items(families, key = { it.id }) { family ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(family.iconName, style = MaterialTheme.typography.headlineMedium)
                        Column {
                            Text(family.name, fontWeight = FontWeight.SemiBold)
                            Text(family.id, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text("Типы удобрений",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
            }
            items(fertilizers, key = { it.id }) { fert ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(fert.iconName, style = MaterialTheme.typography.headlineMedium)
                        Column {
                            Text(fert.name, fontWeight = FontWeight.SemiBold)
                            Text(fert.id, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}