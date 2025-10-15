package fr.dawan.settingscomposeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.dawan.settingscomposeapp.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Notifications", Modifier.weight(1f))
            Switch(
                checked = uiState.notifications,
                onCheckedChange = { viewModel.setNotifications(it) }
            )
        }

        Spacer(Modifier.height(16.dp))

        Text("Taille du texte: ${String.format("%.2f", uiState.textScale)}")
        Slider(
            value = uiState.textScale,
            onValueChange = { viewModel.setTextScale(it) },
            valueRange = 0.8f..1.4f
        )

        Spacer(Modifier.height(16.dp))

        Text("Thème")
        Row {
            RadioButton(
                selected = uiState.theme == "light",
                onClick = { viewModel.setTheme("light") }
            )
            Text("Clair")
            Spacer(Modifier.width(8.dp))
            RadioButton(
                selected = uiState.theme == "dark",
                onClick = { viewModel.setTheme("dark") }
            )
            Text("Sombre")
        }
    }
}
