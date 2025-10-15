package fr.dawan.settingscomposeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.dawan.settingscomposeapp.data.UserPreferences
import fr.dawan.settingscomposeapp.data.dataStore.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val uiState: StateFlow<UserPreferences> = dataStoreManager.preferencesFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserPreferences()
        )

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch { dataStoreManager.updateNotifications(enabled) }
    }

    fun setTextScale(scale: Float) {
        viewModelScope.launch { dataStoreManager.updateTextScale(scale) }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch { dataStoreManager.updateTheme(theme) }
    }
}
