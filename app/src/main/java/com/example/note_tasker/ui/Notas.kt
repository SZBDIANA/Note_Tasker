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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.note_tasker.R
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.note_tasker.data.Nota
import com.example.note_tasker.data.Tarea
import com.example.note_tasker.viewmodel.TareasNotasViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun notas(navController: NavHostController, tareasNotasViewModel: TareasNotasViewModel) {
    val notas by tareasNotasViewModel.notas.collectAsState()
    var notaSeleccionada by remember { mutableStateOf<Nota?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(25.dp)
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(16.dp))
            encabezado(onClick = { navController.navigate("menu") }, R.drawable.menu_amarillo)
        }

        Column(modifier = Modifier.padding(vertical = 40.dp, horizontal = 20.dp)) {
            Text(stringResource(id = R.string.m_notas), fontSize = 30.sp, color = Color(0xFFFDC334))
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notas) { nota ->
                    NotaCard(nota, onSelect = {
                        notaSeleccionada = nota
                        showDialog = true
                    })
                }
            }
        }
        ConfirmEditDeleteDialog_N(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onEdit = {
                notaSeleccionada?.let { nota ->
                    navController.navigate("detalles_nota/${nota.id}")
                }
                showDialog = false
            },
            onDelete = {
                notaSeleccionada?.let { nota ->
                    tareasNotasViewModel.eliminarNota(nota)
                    showDialog = false
                    notaSeleccionada = null
                }
            },
            title = "¿Qué deseas hacer?",
            message = "Puedes acceder o eliminar la nota seleccionada."
        )

        agregar_nueva(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp), R.drawable.add_amarillo, onClick = { navController.navigate("Nueva") }
        )
    }
}



@Composable
fun NotaCard(nota: Nota, onSelect: (Nota) -> Unit) {
    Card(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .clickable { onSelect(nota) },  // Ocupa el 97% del ancho de la pantalla
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = nota.titulo,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = nota.descripcion ?: "",  // Asegura que la descripción no sea null
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
@Composable
fun ConfirmEditDeleteDialog_N(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    title: String = "Acción requerida",
    message: String = "¿Qué acción deseas realizar con esta nota?",
    editIcon: ImageVector = Icons.Default.Edit,
    deleteIcon: ImageVector = Icons.Default.Delete,
    editTint: Color = Color.Blue,
    deleteTint: Color = Color.Red
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                IconButton(onClick = onDelete) {
                    Icon(deleteIcon, contentDescription = "Eliminar", tint = deleteTint)
                }
            },
            dismissButton = {
                IconButton(onClick = onEdit) {
                    Icon(editIcon, contentDescription = "Ver", tint = editTint)
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun notasScreenPreview() {
    val navController = rememberNavController()
    val tareasNotasViewModel: TareasNotasViewModel = viewModel(factory = AppViewModelProvider.Factory)
    notas(navController, tareasNotasViewModel)
}
