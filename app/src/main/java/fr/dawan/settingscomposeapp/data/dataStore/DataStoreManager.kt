package fr.dawan.settingscomposeapp.data.dataStore

import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.content.Context
import fr.dawan.settingscomposeapp.data.UserPreferences

// 1. Déclare l’extension sur Context
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
        val TEXT_SCALE = floatPreferencesKey("text_scale")
        val THEME = stringPreferencesKey("theme")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { prefs ->
            val notifications = prefs[NOTIFICATIONS] ?: true
            val textScale = prefs[TEXT_SCALE] ?: 1f
            val theme = prefs[THEME] ?: "system"
            UserPreferences(notifications, textScale, theme)
        }

    suspend fun updateNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS] = enabled
        }
    }

    suspend fun updateTextScale(scale: Float) {
        context.dataStore.edit { prefs ->
            prefs[TEXT_SCALE] = scale
        }
    }

    suspend fun updateTheme(theme: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME] = theme
        }
    }
}
