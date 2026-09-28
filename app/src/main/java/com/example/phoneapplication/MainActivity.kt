package com.example.phoneapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.concurrent.TimeUnit
import androidx.compose.ui.tooling.preview.Preview

// Модель данных растения
data class Plant(
    val id: Int,
    val name: String,
    val species: String,
    var lastWateredTime: Long, // Время последнего полива
    val intervalDays: Int      // Интервал в днях
)

enum class WateringStatus { OK, WARNING, OVERDUE }

// Логика расчета цвета для карточки
fun getWateringStatus(plant: Plant, referenceDate: Long): WateringStatus {
    val intervalMillis = TimeUnit.DAYS.toMillis(plant.intervalDays.toLong())
    val nextWateringTime = plant.lastWateredTime + intervalMillis
    val warningThreshold = nextWateringTime - TimeUnit.DAYS.toMillis(2)

    // Сравниваем с данной выбранной в календаре
    return when {
        referenceDate >= nextWateringTime -> WateringStatus.OVERDUE
        referenceDate >= warningThreshold -> WateringStatus.WARNING
        else -> WateringStatus.OK
    }
}

// Точка входа в программу
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PlantOrganizerApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantOrganizerApp() {
    // Тестовые данные: теперь 6 растений с разными интервалами
    val plants = remember { mutableStateListOf<Plant>().apply {
        val now = System.currentTimeMillis()
        val day = TimeUnit.DAYS.toMillis(1)

        // КРАСНЫЕ (Просрочены)
        add(Plant(1, "Кактус", "Эхинокактус", now - (12 * day), 10))   // Просрочен на 2 дня
        add(Plant(2, "Орхидея", "Фаленопсис", now - (15 * day), 14))   // Просрочена на 1 день
        add(Plant(3, "Роза", "Чайная", now - (4 * day), 3))            // Просрочена на 1 день

        // ОРАНЖЕВЫЕ (Скоро нужно поливать)
        add(Plant(4, "Фикус", "Бенджамина", now - (5 * day), 7))       // Осталось 2 дня
        add(Plant(5, "Герань", "Плющелистная", now - (8 * day), 10))   // Осталось 2 дня

        // ЗЕЛЕНЫЕ (Всё хорошо)
        add(Plant(6, "Папоротник", "Нефролепис", now - (1 * day), 7))  // До полива еще 6 дней
    }}

    // Состояние календаря
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
    val displayPlants = plants

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Календарь полива", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Календарь + Заголовок
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider()

                    Text(
                        text = "Статус всех растений на выбранную дату:",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // СПИСОК РАСТЕНИЙ
            items(displayPlants, key = { it.id }) { plant ->
                PlantCard(
                    plant = plant,
                    referenceDate = selectedDateMillis,
                    onWaterClick = {
                        val index = plants.indexOf(plant)
                        if (index != -1) {
                            plants[index] = plant.copy(lastWateredTime = selectedDateMillis)
                        }
                    }
                )
            }
        }
    }
}

// Карточка растения
@Composable
fun PlantCard(plant: Plant, referenceDate: Long, onWaterClick: () -> Unit) {
    // Статус считается конкретно для referenceDate
    val status = getWateringStatus(plant, referenceDate)

    val (containerColor, contentColor, statusText) = when (status) {
        WateringStatus.OVERDUE -> Triple(
            Color(0xFFFFCDD2), // Светло-красный фон
            Color(0xFFB71C1C), // Темно-красный текст/кнопка
            "Требует срочного полива!"
        )
        WateringStatus.WARNING -> Triple(
            Color(0xFFFFE0B2), // Светло-оранжевый фон
            Color(0xFFE65100), // Темно-оранжевый текст/кнопка
            "Скоро нужно поливать"
        )
        WateringStatus.OK -> Triple(
            Color(0xFFC8E6C9), // Светло-зеленый фон
            Color(0xFF1B5E20), // Темно-зеленый текст/кнопка
            "Всё хорошо"
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = plant.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = plant.species, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 4.dp))
                Text(text = statusText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onWaterClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = contentColor,
                    contentColor = containerColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Полить")
            }
        }
    }
}

// Превью главного экрана целиком
@Preview(showBackground = true, showSystemUi = true, name = "Главный экран")
@Composable
fun PlantOrganizerAppPreview() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            PlantOrganizerApp()
        }
    }
}

// Превью КРАСНОЙ карточки (Просрочено)
@Preview(showBackground = true, name = "Карточка: Просрочено (Красный)")
@Composable
fun PlantCardOverduePreview() {
    val now = System.currentTimeMillis()
    val day = TimeUnit.DAYS.toMillis(1)
    // Полит 12 дней назад, интервал 10 дней -> просрочка 2 дня
    val plant = Plant(1, "Кактус", "Эхинокактус", now - (12 * day), 10)

    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface(modifier = Modifier.padding(16.dp)) {
            PlantCard(plant = plant, referenceDate = now, onWaterClick = {})
        }
    }
}

// Превью ОРАНЖЕВОЙ карточки (Скоро)
@Preview(showBackground = true, name = "Карточка: Скоро (Оранжевый)")
@Composable
fun PlantCardWarningPreview() {
    val now = System.currentTimeMillis()
    val day = TimeUnit.DAYS.toMillis(1)
    // Полит 5 дней назад, интервал 7 дней -> осталось 2 дня
    val plant = Plant(2, "Фикус", "Бенджамина", now - (5 * day), 7)

    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface(modifier = Modifier.padding(16.dp)) {
            PlantCard(plant = plant, referenceDate = now, onWaterClick = {})
        }
    }
}

// Превью ЗЕЛЕНОЙ карточки (Всё хорошо)
@Preview(showBackground = true, name = "Карточка: Всё хорошо (Зеленый)")
@Composable
fun PlantCardOkPreview() {
    val now = System.currentTimeMillis()
    val day = TimeUnit.DAYS.toMillis(1)
    // Полит 1 день назад, интервал 7 дней -> до полива еще 6 дней
    val plant = Plant(3, "Папоротник", "Нефролепис", now - (1 * day), 7)

    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface(modifier = Modifier.padding(16.dp)) {
            PlantCard(plant = plant, referenceDate = now, onWaterClick = {})
        }
    }
}