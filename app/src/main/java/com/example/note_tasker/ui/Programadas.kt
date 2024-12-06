package com.example.note_tasker.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.note_tasker.viewmodel.TareasNotasViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun programadas(navController: NavHostController, tareasNotasViewModel: TareasNotasViewModel) {
    val tareasProgramadas by tareasNotasViewModel.tareasProgramadas.collectAsState(emptyList())
    var tareaSeleccionada by remember { mutableStateOf<Tarea?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tareasNotasViewModel.getTareasProgramadas()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(25.dp)) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            encabezado(onClick = { navController.navigate("menu") }, R.drawable.menu_azul)
        }
        Column(modifier = Modifier.padding(vertical = 40.dp, horizontal = 20.dp)) {
            Text(stringResource(id = R.string.programadas), fontSize = 30.sp, color = Color.Blue)

            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareasProgramadas) { tarea ->
                    TareaCard(tarea, tareasNotasViewModel, onSelect = {
                        tareaSeleccionada = tarea
                        showDialog = true
                    })
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun programadasScreenPreview() {
    val navController = rememberNavController()
    val tareasNotasViewModel: TareasNotasViewModel = viewModel(factory = AppViewModelProvider.Factory)
    programadas(navController, tareasNotasViewModel)
}