package fr.dawan.settingscomposeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.dawan.settingscomposeapp.data.dataStore.DataStoreManager


class SettingsViewModelFactory(
    val value: DataStoreManager
) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(value) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
