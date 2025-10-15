package fr.dawan.settingscomposeapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController
) {
    val items = List(12) { "Item #$it" }

    LazyColumn {
        itemsIndexed(items) { index, item ->
            var showMenu by remember { mutableStateOf(false) }
            val context = LocalContext.current

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .combinedClickable(
                        onClick = {

                            // Naviguer vers l'écran de détail
                            // Envoi de l'index de l'élément detail/$index
                            navController.navigate("detail/$index")
                        },
                        onLongClick = { showMenu = true }
                    )
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item, Modifier.weight(1f))
                    if (showMenu) {
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Favoris",
                                        fontSize = 25.sp
                                    )
                                },
                                onClick = { /* marquer favoris */ }
                            )
                            DropdownMenuItem(
                                text = { Text("Supprimer") },
                                onClick = { /* supprimer item */ }
                            )
                            DropdownMenuItem(
                                text = { Text("Partager") },
                                onClick = {
                                    Toast.makeText(context, "Partager $item", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
