# Android Jetpack Compose — SettingsComposeApp

---

## Table des matières
1. Contexte & objectifs
2. Pré-requis
3. Arborescence recommandée
4. Partie 0 — Création du projet
5. Partie 1 — Architecture & Material3
6. Partie 2 — TopAppBar et Menu d'options
7. Partie 3 — Menu contextuel (clic long)
8. Partie 4 — SettingsScreen (DataStore)
9. Partie 5 — Sauvegarder / lire préférences (DataStore)
10. Partie 6 — Partage des préférences entre écrans
11. Bonus — Thème & Dark mode
12. Code final
13. Livrables et exercices pour les étudiants

---

## 1. Contexte & objectifs
Ce TP permet d'apprendre à implémenter dans Jetpack Compose :
- une barre d'actions (TopAppBar) avec menu d'options,
- un menu contextuel sur un élément via clic long,
- la navigation entre écrans (Navigation Compose),
- la sauvegarde des préférences utilisateurs avec **DataStore Preferences**,
- la lecture et le partage de ces préférences entre écrans.

**Objectifs pédagogiques :**
- Savoir structurer une app Compose avec `Scaffold` et `TopAppBar`.
- Savoir créer des actions et un menu contextuel.
- Implémenter DataStore pour persister des préférences.
- Partager et afficher ces préférences dans différents écrans.

---

## 2. Pré-requis
- Android Studio Flamingo ou +
- Kotlin 1.8+
- Compose Material3
- Connaissances de base Compose (Composables, State, Modifier)
- Plugin Kotlin et Gradle à jour

---

## 3. Arborescence recommandée
```
app/src/main/java/com/example/settingscomposeapp/
│
├─ ui/
│  ├─ theme/
│  ├─ components/
│  └─ screens/
│     ├─ HomeScreen.kt
│     ├─ SettingsScreen.kt
│     └─ DetailScreen.kt
├─ navigation/
│  └─ NavGraph.kt
├─ data/
│  └─ datastore/
│     └─ DataStoreManager.kt
└─ MainActivity.kt
```

---

## 4. Partie 0 — Création du projet
**Étapes rapides :**
1. Nouveau projet -> Empty Compose Activity
2. Minimum SDK : 26+
3. Ajouter dépendances (build.gradle : module app) :

```gradle
dependencies {
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
}
```

> **Exercice #0** : Créez le projet et vérifiez que `MainActivity` compile et s'exécute.

---

## 5. Partie 1 — Architecture & Material3 (10 min)
**But :** Mettre en place le thème Material3 + `Scaffold` de base.

### MainActivity.kt (squelette)
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsComposeAppTheme {
                val navController = rememberNavController()
                // Scaffold + TopAppBar sera dans MainScreen
                MainScreen(navController = navController)
            }
        }
    }
}
```

### MainScreen.kt (Scaffold + TopAppBar placeholder)
```kotlin
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        topBar = { AppTopBar(onSettingsClick = { navController.navigate("settings") }) }
    ) { innerPadding ->
        NavGraph(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}
```

> **Exercice #1** : Implémente `AppTopBar` avec un titre et un bouton d'action (icône) qui navigue vers `settings`.

---

## 6. Partie 2 — TopAppBar et Menu d'options (15 min)
**But :** Créer une TopAppBar avec actions, dont un menu déroulant d'options.

### AppTopBar.kt
```kotlin
@Composable
fun AppTopBar(onSettingsClick: () -> Unit, onShareClick: () -> Unit = {}) {
    SmallTopAppBar(
        title = { Text("SettingsComposeApp") },
        actions = {
            IconButton(onClick = onShareClick) {
                Icon(Icons.Default.Share, contentDescription = "Partager")
            }
            // Overflow menu
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Plus")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("Paramètres") }, onClick = {
                    expanded = false
                    onSettingsClick()
                })
                DropdownMenuItem(text = { Text("À propos") }, onClick = { /* TODO */ })
            }
        }
    )
}
```

**Explications :**
- `SmallTopAppBar` (Material3) affiche le titre.
- `IconButton` + `DropdownMenu` créent le menu overflow.

> **Mini-exo** : Ajoute une action `onShareClick` qui affichera un `Snackbar` temporaire.

---

## 7. Partie 3 — Menu contextuel (clic long) (10 min)
**But :** Sur `HomeScreen`, un item (ex : une carte) doit afficher un menu contextuel au clic long.

### HomeScreen.kt (extrait)
```kotlin
@Composable
fun HomeScreen(navController: NavController, dataStoreManager: DataStoreManager) {
    val items = List(12) { "Item #$it" }
    LazyColumn {
        itemsIndexed(items) { index, item ->
            var showMenu by remember { mutableStateOf(false) }
            val context = LocalContext.current

            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .combinedClickable(
                    onClick = { navController.navigate("detail/$index") },
                    onLongClick = { showMenu = true }
                )) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(item, Modifier.weight(1f))
                    if (showMenu) {
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = { Text("Favoris") }, onClick = { /*mark favorite*/ })
                            DropdownMenuItem(text = { Text("Supprimer") }, onClick = { /*delete*/ })
                        }
                    }
                }
            }
        }
    }
}
```

**Notes :**
- `combinedClickable` provient de `androidx.compose.foundation` et permet de gérer clic simple et clic long.

> **Exercice** : Ajoute une option "Partager" dans le menu contextuel qui déclenche un `Intent` ACTION_SEND via le `context`.

---

## 8. Partie 4 — SettingsScreen (DataStore) (15-20 min)
**But :** Créer un écran de paramètres avec :
- un `Switch` pour activer/ désactiver un mode (ex: notifications)
- un `Slider` pour la taille du texte (ou volume fictif)
- un groupe de `RadioButton` pour choisir le thème (Clair / Sombre)

### DataStoreManager.kt (squelette)
```kotlin
class DataStoreManager(context: Context) {
    private val dataStore = context.createDataStore(name = "user_prefs")

    companion object {
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
        val TEXT_SCALE = floatPreferencesKey("text_scale")
        val THEME = stringPreferencesKey("theme")
    }

    val preferencesFlow: Flow<UserPreferences> = dataStore.data.map { prefs ->
        val notifications = prefs[NOTIFICATIONS] ?: true
        val textScale = prefs[TEXT_SCALE] ?: 1f
        val theme = prefs[THEME] ?: "system"
        UserPreferences(notifications, textScale, theme)
    }

    suspend fun updateNotifications(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[NOTIFICATIONS] = enabled }
    }

    suspend fun updateTextScale(scale: Float) {
        dataStore.edit { prefs -> prefs[TEXT_SCALE] = scale }
    }

    suspend fun updateTheme(theme: String) {
        dataStore.edit { prefs -> prefs[THEME] = theme }
    }
}
```

> **Exercice** : Implémente `UserPreferences` (data class) et expose un `ViewModel` qui wrappe `DataStoreManager`.

### SettingsScreen.kt (extrait)
```kotlin
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Notifications" , Modifier.weight(1f))
            Switch(checked = uiState.notifications, onCheckedChange = { viewModel.setNotifications(it) })
        }

        Spacer(Modifier.height(16.dp))

        Text("Taille du texte: ${String.format("%.2f", uiState.textScale)}")
        Slider(value = uiState.textScale, onValueChange = { viewModel.setTextScale(it) }, valueRange = 0.8f..1.4f)

        Spacer(Modifier.height(16.dp))

        Text("Thème")
        Row {
            RadioButton(selected = uiState.theme == "light", onClick = { viewModel.setTheme("light") })
            Text("Clair")
            Spacer(Modifier.width(8.dp))
            RadioButton(selected = uiState.theme == "dark", onClick = { viewModel.setTheme("dark") })
            Text("Sombre")
        }
    }
}
```

---

## 9. Partie 5 — Sauvegarder / lire préférences (DataStore) (15 min)
**But :** Implémenter le ViewModel et connecter DataStore.

### SettingsViewModel.kt (extrait)
```kotlin
class SettingsViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {
    val uiState: StateFlow<UserPreferences> = dataStoreManager.preferencesFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences()
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
```

> **Exercice** : Branche `SettingsScreen` sur ce `ViewModel` via `hilt` ou un `ViewModelFactory` simple.

---

## 10. Partie 6 — Partage des préférences entre écrans (10 min)
**But :** Afficher les préférences dans `HomeScreen` et utiliser le `textScale` par exemple.

### Exemple d'utilisation
```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {
    val prefs by viewModel.preferences.collectAsState()

    Text("Bienvenue", fontSize = (16 * prefs.textScale).sp)
}
```

**Notes :**
- Les `ViewModel` partagés par `NavGraph` ou via injection permettent d'avoir un état commun.

> **Exercice** : Affiche l'état du `Switch` (Notifications) sur la HomeScreen (icone ON/OFF).

---

## 11. Bonus — Thème & Dark mode (5-10 min)
- Applique le thème choisi (light/dark) en observant `uiState.theme` dans `MainActivity` et en passant `darkTheme = (uiState.theme == "dark")`.

---

## 12. Code final (rappel)
Le code final complet (toutes les classes) est donné en annexe.

---

## 13. Livrables et exercices pour les étudiants
**Livrables demandés :**
- Repo GitHub / zip contenant le projet
- Captures d’écran du résultat
- Courte description (README) : comment lancer

**Exercices notés :**
1. Ajouter la fonctionnalité "Favoris" persistée via DataStore.
2. Ajouter une option d'export des préférences (JSON) dans le stockage externe.
3. Amélioration UI : utiliser `LazyVerticalGrid` pour la HomeScreen.

---
