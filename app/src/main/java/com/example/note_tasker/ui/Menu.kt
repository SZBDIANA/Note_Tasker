package com.example.note_tasker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.note_tasker.ui.hoy
import com.example.note_tasker.ui.theme.NoteTaskerTheme
import com.example.note_tasker.R
import androidx.compose.ui.res.stringResource


@Composable
fun menu(navController: NavHostController) {
    val searchText = remember { mutableStateOf("") }
    NoteTaskerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
                    label = { Text(stringResource(id = R.string.buscar)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Row {
                        TaskButton(R.drawable.calendar, stringResource(id = R.string.hoy)) {
                            navController.navigate("Hoy")
                        }
                        TaskButton(R.drawable.calendar_azul, stringResource(id = R.string.programadas)) {
                            navController.navigate("Programadas")
                        }
                    }
                    Row {
                        TaskButton(R.drawable.folder, stringResource(id = R.string.todas)) {
                            navController.navigate("Todos")
                        }
                        TaskButton(R.drawable.yes, stringResource(id = R.string.terminadas)) {
                            navController.navigate("Terminados")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(id = R.string.m_listas), style = MaterialTheme.typography.headlineSmall)
                }
                SectionHeader(stringResource(id = R.string.m_notas), R.drawable.notes) {
                    navController.navigate("Notas")
                }
                SectionHeader(stringResource(id = R.string.m_tareas), R.drawable.tareas) {
                    navController.navigate("Mis_tareas")
                }
            }
        }
    }
}

@Composable
fun TaskButton(iconResId: Int, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(170.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text)
        }
    }
}

@Composable
fun SectionHeader(title: String, iconResId: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(60.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    val navController = rememberNavController()
    NoteTaskerTheme(darkTheme = true) {  // Aplica el tema oscuro en la vista previa
        menu(navController)
    }
}
