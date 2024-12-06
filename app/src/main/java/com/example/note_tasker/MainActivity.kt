package com.example.note_tasker
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.note_tasker.ui.AppViewModelProvider
import com.example.note_tasker.ui.DetallesNotaScreen
import com.example.note_tasker.ui.hoy
import com.example.note_tasker.ui.menu
import com.example.note_tasker.ui.mistareas
import com.example.note_tasker.ui.notas
import com.example.note_tasker.ui.nueva
import com.example.note_tasker.ui.programadas
import com.example.note_tasker.ui.terminadas
import com.example.note_tasker.ui.DetallesTareaScreen
import com.example.note_tasker.ui.theme.NoteTaskerTheme
import com.example.note_tasker.ui.todas
import com.example.note_tasker.viewmodel.TareasNotasViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteTaskerTheme {  // Apply the theme here
                val navController = rememberNavController()
                val tareasNotasViewModel = viewModel<TareasNotasViewModel>(factory = AppViewModelProvider.Factory)
                NavHost(navController, startDestination = "first_screen") {
                    composable("first_screen") {
                        val mainViewModel: MainViewModel = viewModel()
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = R.drawable.fondo),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    val text = mainViewModel.text.collectAsState().value
                                    Text(
                                        text = text,
                                        fontSize = 36.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .offset(y = (-250).dp)
                                    )
                                }
                                Image(
                                    painter = painterResource(id = R.drawable.work_order),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(200.dp)
                                )
                                Greeting(
                                    modifier = Modifier.padding(innerPadding)
                                )
                                LaunchedEffect(Unit) {
                                    delay(3000)
                                    navController.navigate("menu") {
                                        popUpTo("first_screen") { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                    composable("menu") {
                        menu(navController)
                    }
                    composable("Hoy") {
                        hoy(navController, tareasNotasViewModel)
                    }
                    composable("Programadas") {
                        programadas(navController, tareasNotasViewModel)
                    }
                    composable("Todos") {
                        todas(navController, tareasNotasViewModel)
                    }
                    composable("Terminados") {
                        terminadas(navController, tareasNotasViewModel)
                    }
                    composable("Mis_tareas") {
                        mistareas(navController, tareasNotasViewModel)
                    }
                    composable("Notas") {
                        notas(navController,tareasNotasViewModel)
                    }
                    composable("Nueva") {
                        nueva(navController, tareasNotasViewModel)
                    }
                    composable(
                        "detalles_tarea/{tareaId}",
                        arguments = listOf(navArgument("tareaId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val tareaId = backStackEntry.arguments?.getInt("tareaId")
                        val tarea = tareaId?.let { tareasNotasViewModel.obtenerTareaPorId(it) }
                        if (tarea != null) {
                            DetallesTareaScreen(navController, tarea, tareasNotasViewModel)
                        }
                    }
                    composable(
                        "detalles_nota/{notaId}",
                        arguments = listOf(navArgument("notaId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val notaId = backStackEntry.arguments?.getInt("notaId")
                        val nota = notaId?.let { tareasNotasViewModel.obtenerNotaPorId(it) }
                        if (nota != null) {
                            DetallesNotaScreen(navController, nota, tareasNotasViewModel)
                        }
                    }
                }
            }
        }
    }

    class MainViewModel : ViewModel() {
        private val _text = MutableStateFlow("NoteTasker")
        val text: StateFlow<String> get() = _text

        fun updateText(newText: String) {
            viewModelScope.launch {
                _text.value = newText
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    // Content here
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NoteTaskerTheme(darkTheme = true) {
        Greeting()
    }
}