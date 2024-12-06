package com.example.note_tasker.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import com.example.note_tasker.data.Nota
import com.example.note_tasker.data.Tarea
import com.example.note_tasker.viewmodel.TareasNotasViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun todas(navController: NavHostController, tareasNotasViewModel: TareasNotasViewModel) {
    val tareas by tareasNotasViewModel.tareas.collectAsState(emptyList())
    val notas by tareasNotasViewModel.notas.collectAsState(emptyList())
    var tareaSeleccionada by remember { mutableStateOf<Tarea?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var notaSeleccionada by remember { mutableStateOf<Nota?>(null) }
    var showDialogN by remember { mutableStateOf(false) }

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
            encabezado(onClick = { navController.navigate("menu") }, R.drawable.menu_mora)

            Text(
                stringResource(id = R.string.todas),
                fontSize = 30.sp,
                color = Color.Magenta,
                modifier = Modifier.padding(vertical = 40.dp, horizontal = 20.dp)
            )

            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Column for notes
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Section(stringResource(id = R.string.m_notas))
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        contentPadding = PaddingValues(2.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notas) { nota ->
                            NotaCard(nota, onSelect = {
                                notaSeleccionada = nota
                                showDialogN = true
                            })
                        }
                    }
                }

                // Column for tasks
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Section(stringResource(id = R.string.m_tareas))
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        contentPadding = PaddingValues(2.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tareas) { tarea ->
                            TareaCard(tarea, tareasNotasViewModel, onSelect = {
                                tareaSeleccionada = tarea
                                showDialog = true
                            })
                        }
                    }
                }
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
    ConfirmEditDeleteDialog_N(
        showDialog = showDialogN,
        onDismiss = { showDialogN = false },
        onEdit = {
            notaSeleccionada?.let { nota ->
                navController.navigate("detalles_nota/${nota.id}")
            }
            showDialogN = false
        },
        onDelete = {
            notaSeleccionada?.let { nota ->
                tareasNotasViewModel.eliminarNota(nota)
                showDialogN = false
                notaSeleccionada = null
            }
        },
        title = "¿Qué deseas hacer?",
        message = "Puedes acceder o eliminar la nota seleccionada."
    )
}
@Composable
fun Section(title: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
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
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun totasScreenPreview() {
    val navController = rememberNavController()
    val tareasNotasViewModel: TareasNotasViewModel = viewModel(factory = AppViewModelProvider.Factory)
    todas(navController, tareasNotasViewModel)
}