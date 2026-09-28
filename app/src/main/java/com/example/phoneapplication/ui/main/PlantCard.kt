package com.example.phoneapplication.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phoneapplication.data.model.Plant
import com.example.phoneapplication.data.model.WateringStatus
import com.example.phoneapplication.util.WateringCalculator

@Composable
fun PlantCard(
    plant: Plant,
    referenceDate: Long,
    onClick: () -> Unit,
    onWaterClick: () -> Unit
) {
    val status = WateringCalculator.getStatus(plant, referenceDate)

    val (containerColor, contentColor, statusText) = when (status) {
        WateringStatus.OVERDUE -> Triple(Color(0xFFFFCDD2), Color(0xFFB71C1C),
            "Требует срочного полива!")
        WateringStatus.WARNING -> Triple(Color(0xFFFFE0B2), Color(0xFFE65100),
            "Скоро нужно поливать")
        WateringStatus.OK -> Triple(Color(0xFFC8E6C9), Color(0xFF1B5E20),
            "Всё хорошо")
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(plant.name, style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text(plant.species, style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp))
                Text(statusText, style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onWaterClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = contentColor, contentColor = containerColor),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Полить") }
        }
    }
}