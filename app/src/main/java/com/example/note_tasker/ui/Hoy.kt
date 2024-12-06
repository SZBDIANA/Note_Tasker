package com.example.note_tasker.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.note_tasker.R
import com.example.note_tasker.data.Tarea
import com.example.note_tasker.ui.theme.NoteTaskerTheme
import com.example.note_tasker.viewmodel.TareasNotasViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun hoy(navController: NavHostController, tareasNotasViewModel: TareasNotasViewModel) {
    val tareasf by tareasNotasViewModel.tareasf.collectAsState()
    var tareaSeleccionada by remember { mutableStateOf<Tarea?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val now = LocalDate.now()
    val formattedDate = now.format(DateTimeFormatter.ISO_DATE)

    LaunchedEffect(formattedDate) {
        tareasNotasViewModel.getTareasByDate(formattedDate)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(25.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            encabezado(onClick = { navController.navigate("menu") }, R.drawable.menu_rojo)
        }

        Column(modifier = Modifier.padding(vertical = 40.dp, horizontal = 20.dp)) {
            Text(
                text = stringResource(id = R.string.hoy),
                fontSize = 30.sp,
                color = Color.Red
            )
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareasf) { tareaf ->
                    TareaCard(tareaf, tareasNotasViewModel, onSelect = {
                        tareaSeleccionada = tareaf
                        showDialog = true
                    })
                }
            }
        }
        ConfirmEditDeleteDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onEdit = {
                tareaSeleccionada?.let { tarea ->
                    navController.navigate("detalles_tarea/${tarea.id}")
                }
                showDialog = false
            },
            onDelete = {
                tareaSeleccionada?.let { tarea ->
                    tareasNotasViewModel.eliminarTarea(tarea)
                    showDialog = false
                    tareaSeleccionada = null
                }
            },
            title = "¿Qué deseas hacer?",
            message = "Puedes acceder o eliminar la tarea seleccionada."
        )

        agregar_nueva(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            iconResId = R.drawable.add_rojo,
            onClick = { navController.navigate("Nueva") }
        )
    }
}


@Composable
fun encabezado(onClick: () -> Unit, iconResId: Int) {
    val isDarkTheme = isSystemInDarkTheme()
    val arrowIcon = if (isDarkTheme) R.drawable.left_arrow_b else R.drawable.left_arrow

    var expanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = arrowIcon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(15.dp)
                        .padding(end = 3.dp)
                )
                Text(
                    text = stringResource(id = R.string.listas),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Box {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = "menu",
                    modifier = Modifier
                        .size(26.dp)
                        .clickable { expanded = true }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                        },
                        text = { Text("Editar") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                        },
                        text = { Text("Eliminar") }
                    )
                }
            }
        }
    }
}


@Composable
fun agregar_nueva(modifier: Modifier = Modifier, iconResId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .padding(end = 3.dp)
            )
            Text(stringResource(id = R.string.nueva), fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun hoyScreenPreview() {
    NoteTaskerTheme(darkTheme = true) {
        val navController = rememberNavController()
        val tareasNotasViewModel: TareasNotasViewModel = viewModel(factory = AppViewModelProvider.Factory)
        hoy(navController, tareasNotasViewModel)
    }

}