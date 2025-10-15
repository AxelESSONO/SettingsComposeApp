package fr.dawan.settingscomposeapp.data

data class UserPreferences(
    val notifications: Boolean = true,
    val textScale: Float = 1f,
    val theme: String = "system" // "light", "dark", ou "system"
)
