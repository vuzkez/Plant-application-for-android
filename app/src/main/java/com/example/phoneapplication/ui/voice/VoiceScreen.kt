package com.example.phoneapplication.ui.voice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.phoneapplication.domain.voice.VoiceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceScreen(
    onBack: () -> Unit,
    viewModel: VoiceViewModel = hiltViewModel()
) {
    val voiceState by viewModel.voiceState.collectAsStateWithLifecycle()
    val screen by viewModel.screen.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Здесь только запрос разрешения. SpeechRecognizer и TextToSpeech в UI не трогаем.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // Если отказали, репозиторий сам выставит состояние Error
        viewModel.onMicClick()
    }

    fun onMicPressed() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) viewModel.onMicClick()
        else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Голосовой ввод") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Скажите, что вы сделали:", fontWeight = FontWeight.Bold)
                    Text("«Полил кактус»")
                    Text("«Подкормил розу»")
                    Text("«Пересадил фикус»")
                }
            }

            Button(
                onClick = { onMicPressed() },
                enabled = voiceState !is VoiceUiState.Processing,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Text(
                    when (voiceState) {
                        VoiceUiState.Listening -> "Слушаю… (нажмите, чтобы остановить)"
                        VoiceUiState.Processing -> "Распознаю…"
                        else -> "🎤 Нажмите и говорите"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }

            val current = voiceState
            if (current is VoiceUiState.Error) {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(current.message, Modifier.padding(16.dp))
                }
            }

            if (screen.recognized.isNotEmpty()) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Вы сказали:", style = MaterialTheme.typography.labelMedium)
                        Text(screen.recognized)
                    }
                }
            }

            if (screen.reply.isNotEmpty()) {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (screen.replySuccess)
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Ответ:", style = MaterialTheme.typography.labelMedium)
                        Text(screen.reply, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
