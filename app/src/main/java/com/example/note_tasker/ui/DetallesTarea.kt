package com.example.note_tasker.ui

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.note_tasker.data.Tarea
import com.example.note_tasker.viewmodel.TareasNotasViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import com.example.note_tasker.R
import com.example.note_tasker.notificationSystem.NotificationReceiver
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetallesTareaScreen(
    navController: NavHostController,
    tarea: Tarea,
    tareasNotasViewModel: TareasNotasViewModel
) {

    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf(tarea.titulo) }
    var descripcion by remember { mutableStateOf(tarea.descripcion) }
    var fecha by remember { mutableStateOf(tarea.fecha) }
    var hora by remember { mutableStateOf(tarea.hora) }
    var uriString by remember { mutableStateOf(tarea.multimedia) }
    var expanded by remember { mutableStateOf(false) }
    var selectedfiles by remember { mutableStateOf(listOf<Uri>()) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var currentVideoUri by remember { mutableStateOf<Uri?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }
    var audioFile: File? by remember { mutableStateOf(null) }



    // Lanzadores para fotos y videos
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedfiles = selectedfiles + uri
                uriString = selectedfiles.joinToString(",") { it.toString() }
            }
        }
    }

    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedfiles = selectedfiles + uri

                uriString = selectedfiles.joinToString(",") { it.toString() }
            }
        }
    }
    val audioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedfiles = selectedfiles + uri
                uriString = selectedfiles.joinToString(",") { it.toString() }
            }
        }
    }

    val VcameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakeVideo()) { curretVideoUri ->
        currentVideoUri?.let { uri ->
            selectedfiles = selectedfiles + uri
            uriString = selectedfiles.joinToString(",") { it.toString() }
        }
    }

// Lanzador para capturar una foto
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                selectedfiles = selectedfiles + uri
                uriString = selectedfiles.joinToString(",") { it.toString() }
            }
        }
    }

    // Función para capturar una foto
    fun takePhotoWithCamera(context: Context, cameraLauncher: ActivityResultLauncher<Uri>) {
        val photoFile = File(context.externalCacheDir, "temp_photo.jpg")
        val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
        currentPhotoUri = photoUri
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri) // Indicar la ubicación donde guardar la foto
        }
        cameraLauncher.launch(photoUri) // Lanzar el lanzador de cámara para fotos, usando el Uri
    }

    // Función para grabar un video
    fun takeVideoWithCamera(context: Context, VcameraLauncher: ActivityResultLauncher<Uri>) {
        val videoFile = File(context.externalCacheDir, "temp_video.mp4")
        val videoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", videoFile)
        currentVideoUri = videoUri
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, videoUri) // Dirección donde se guardará el video
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30) // Limitar la duración a 30 segundos (opcional)
        }
        VcameraLauncher.launch(videoUri) // Lanzar el lanzador de cámara para videos, usando el Uri
    }

// Lanzador para permisos de cámara para fotos
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // El permiso fue otorgado, puedes proceder a tomar la foto
            takePhotoWithCamera(context, cameraLauncher)
        } else {
            // El permiso fue denegado, muestra un mensaje al usuario
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

// Lanzador para permisos de cámara para videos
    val cameraPermissionLauncherV = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // El permiso fue otorgado, puedes proceder a grabar el video
            takeVideoWithCamera(context, VcameraLauncher)
        } else {
            // El permiso fue denegado, muestra un mensaje al usuario
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun startRecording(context: Context) {
        audioFile = File(context.externalCacheDir, "temp_audio.m4a")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile?.absolutePath)
            prepare()
            start()
        }
        isRecording = true
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false

        // Agregar el archivo de audio grabado a la lista de seleccionados
        audioFile?.let { file ->
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            if (context.contentResolver.getType(uri)?.startsWith("video/") == true) {
                val correctedUri = Uri.fromFile(file)
                selectedfiles = selectedfiles + correctedUri
                uriString = selectedfiles.joinToString(",") { it.toString() }
            } else {
                selectedfiles = selectedfiles + uri
                uriString = selectedfiles.joinToString(",") { it.toString() }
            }
        }
    }
    // Lanzador para permisos de grabación de audio
    val audioPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.all { it.value }) {
            startRecording(context)
        } else {
            Toast.makeText(context, "Permiso de grabación denegado", Toast.LENGTH_SHORT).show()
        }
    }
    fun updateUriString(newUris: List<Uri>) {
        uriString = newUris.joinToString(",") { it.toString() }
    }
    fun onDeleteRequest(uri: Uri) {
        selectedfiles = selectedfiles.filter { it != uri }
        updateUriString(selectedfiles)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(16.dp))
            encabezado(onClick = { navController.navigate("menu") }, R.drawable.menu_notas)
        }
        Column(modifier = Modifier.padding(vertical = 40.dp, horizontal = 20.dp).fillMaxWidth()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isEditing) "Editar Tarea" else "Detalles de la Tarea",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFDC334)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Campo Título
            Text(stringResource(id = R.string.name), fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            TextField(
                value = titulo,
                onValueChange = { titulo = it },
                enabled = isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Campo Descripción
            Text(stringResource(id = R.string.description), fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            TextField(
                value = descripcion.toString(),
                onValueChange = { descripcion = it },
                enabled = isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha
            Text("Fecha", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            TextField(
                value = fecha,
                onValueChange = { fecha = it },
                enabled = isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hora
            Text("Hora", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            TextField(
                value = hora,
                onValueChange = { hora = it },
                enabled = isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = true }
            ) {
                Text(
                    text = stringResource(id = R.string.archivos),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.add_notas),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            selectImageFromGallery (context, galleryLauncher)
                        },
                        text = { Text(text = "Foto") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        text = { Text(text = "Tomar Foto") }
                    )
                    DropdownMenuItem(
                        onClick = { expanded = false
                            selectVideoFromGallery(context, videoLauncher)
                        },
                        text = { Text(text = "Video") }
                    )
                    DropdownMenuItem(
                        onClick = { expanded = false
                            cameraPermissionLauncherV.launch(Manifest.permission.CAMERA)
                        },
                        text = { Text(text = "Grabar Video") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            selectAudioFromGallery(context, audioLauncher)
                        },
                        text = { Text(text = "Audio") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            if (!isRecording) {
                                audioPermissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                            } else {
                                stopRecording()
                            }

                        },
                        text={Text(if (isRecording) "Detener Grabación" else "Iniciar Grabación")}
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (uriString!="") {
                selectedfiles = uriString?.let { stringToUriList(it) }!!
                ArchivosSeleccionadosD(fileUris = selectedfiles,context = context,onDeleteUri = { uri -> onDeleteRequest(uri) }, uriString = uriString.toString())
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isEditing) {
                    Button(
                        onClick = {
                            val tareaActualizada = tarea.copy(
                                titulo = titulo,
                                descripcion = descripcion,
                                fecha = fecha,
                                hora = hora,
                                multimedia= uriString
                            )
                            tareasNotasViewModel.actualizarTarea(tareaActualizada)
                            val dateTimeString = "$fecha $hora"
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            val dateTime = LocalDateTime.parse(dateTimeString, formatter)

                            val triggerTime = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                            val intent = Intent(context, NotificationReceiver::class.java).apply {
                                putExtra("task_title", tarea.titulo)
                                putExtra("task_id", tarea.id)
                            }

                            val pendingIntent = PendingIntent.getBroadcast(
                                context,
                                tarea.id,
                                intent,
                                PendingIntent.FLAG_IMMUTABLE // Esto asegura que si ya existe un PendingIntent con el mismo ID, se actualiza
                            )

                            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            alarmManager.set(
                                AlarmManager.RTC_WAKEUP,
                                triggerTime,
                                pendingIntent
                            )

                            navController.navigate("Mis_tareas")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDC334)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Guardar", color = Color.White)
                    }

                    Button(
                        onClick = { isEditing = false },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Cancelar")
                    }
                } else {
                    Button(
                        onClick = { isEditing = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDC334)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Editar", color = Color.White)
                    }

                    Button(
                        onClick = {
                            tareasNotasViewModel.eliminarTarea(tarea)
                            navController.navigate("Mis_tareas")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                }
            }
        }
    }
}

