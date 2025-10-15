package fr.dawan.settingscomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.dawan.settingscomposeapp.data.dataStore.DataStoreManager
import fr.dawan.settingscomposeapp.ui.screens.DetailScreen
import fr.dawan.settingscomposeapp.ui.screens.HomeScreen
import fr.dawan.settingscomposeapp.ui.screens.SettingsScreen
import fr.dawan.settingscomposeapp.ui.theme.SettingsComposeAppTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {

    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStoreManager = DataStoreManager(applicationContext)
        val viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(dataStoreManager)
        )[SettingsViewModel::class.java]

        enableEdgeToEdge()
        setContent {

           // val snackbarHostState = remember { SnackbarHostState() }

            val uiState = viewModel.uiState.collectAsState().value
            // Passe darkTheme selon le choix de l'utilisateur
            val isDark = when (uiState.theme) {
                "light" -> false
                "dark" -> true
                else -> false
            }

            SettingsComposeAppTheme(
                darkTheme = isDark,
                textScale = uiState.textScale
            ) {
                val navController = rememberNavController()
                MainScreen(
                    navController = navController,
                    dataStoreManager = dataStoreManager,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    dataStoreManager: DataStoreManager,
    viewModel: SettingsViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppTopBar(
                onSettingsClick = {
                    navController.navigate("settings")
                },
                onShareClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Share not available",
                            actionLabel = "Annuler"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            dataStoreManager = dataStoreManager,
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel
        )
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    dataStoreManager: DataStoreManager,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier)
    {
        composable("home") {
            HomeScreen(
                navController = navController
            )
        }
        composable("settings") {
            SettingsScreen(viewModel = viewModel)
        }
        composable("detail/{itemId}") { backStackEntry ->

            // Recupere l'id de l'item depuis les arguments de la route
            val itemId = backStackEntry.arguments?.getString("itemId")
            DetailScreen(itemId = itemId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onSettingsClick: () -> Unit,
    onShareClick: () -> Unit = {}
) {
    // SmallTopAppBar est dans version ancienne Material 3
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        title = { Text("SettingsComposeApp") },
        actions = {
            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Partager"
                )
            }

            // Overflow menu
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Plus"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Paramètres") }, onClick = {
                        expanded = false
                        onSettingsClick()
                    })
                DropdownMenuItem(
                    text = { Text("À propos") },
                    onClick = { /* TODO */ }
                )
            }
        }
    )
}
