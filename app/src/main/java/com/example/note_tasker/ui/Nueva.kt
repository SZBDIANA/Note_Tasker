package com.example.note_tasker.ui

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.note_tasker.R
import java.util.Calendar
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.FileProvider
import com.example.note_tasker.data.Nota
import com.example.note_tasker.data.Tarea
import com.example.note_tasker.viewmodel.TareasNotasViewModel
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import androidx.compose.runtime.DisposableEffect
import com.example.note_tasker.notificationSystem.NotificationReceiver
import com.google.android.exoplayer2.MediaItem
import java.time.LocalDateTime
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun nueva(navController: NavHostController, tareasNotasViewModel: TareasNotasViewModel) {
    Log.d("nuevaComposable", "Accediendo a la pantalla 'nueva'")

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val initialSelectedDate = stringResource(id = R.string.s_f)
    val initialSelectedTime = stringResource(id = R.string.s_h)
    var isTaskSelected by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(initialSelectedDate) }
    var selectedTime by remember { mutableStateOf(initialSelectedTime) }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf(listOf<Uri>()) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var currentVideoUri by remember { mutableStateOf<Uri?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }
    var audioFile: File? by remember { mutableStateOf(null) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d ->
            selectedDate = String.format("%02d/%02d/%d", d, m + 1, y) // Formato de fecha con ceros a la izquierda
        },
        year, month, day
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, h, m ->
            selectedTime = String.format("%02d:%02d", h, m) // Formato de hora con ceros a la izquierda
        },
        hour, minute, true
    )

    // Lanzadores para fotos y videos
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImages = selectedImages + uri
            }
        }
    }

    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImages = selectedImages + uri
            }
        }
    }
    val audioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Aquí puedes agregar el URI del archivo de audio a la lista seleccionada
                selectedImages = selectedImages + uri
            }
        }
    }

val VcameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakeVideo()) { curretVideoUri ->
    currentVideoUri?.let { uri ->
        selectedImages = selectedImages + uri
    }
}

// Lanzador para capturar una foto
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                selectedImages = selectedImages + uri
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
                selectedImages = selectedImages + correctedUri
            } else {
                selectedImages = selectedImages + uri
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(25.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(16.dp))
            encabezado(onClick = { navController.navigate("menu") }, R.drawable.menu_notas)

        }
        Column(modifier = Modifier.padding(vertical = 40.dp, horizontal = 20.dp)) {
            Text(
                stringResource(id = R.string.nueva),
                fontSize = 30.sp,
                color = Color(0xFFFDC334),
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(modifier = Modifier.padding(vertical = 70.dp, horizontal = 20.dp)) {

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Text(stringResource(id = R.string.name), fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Text(stringResource(id = R.string.description), fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                TextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
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
                                selectImageFromGallery(context, galleryLauncher)
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
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item { ArchivosSeleccionados(fileUris = selectedImages, onDeleteUri = { uriToDelete ->
                selectedImages = selectedImages.filterNot { it == uriToDelete }}, context) }

            item {
                SegmentedButton(
                    onLeftClick = { isTaskSelected = false },
                    onRightClick = { isTaskSelected = true }
                )
            }

            if (isTaskSelected) {
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Text(stringResource(id = R.string.fecha), fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                    Text(
                        text = selectedDate,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .clickable { datePickerDialog.show() }
                            .padding(16.dp)
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Text(stringResource(id = R.string.hora), fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                    Text(
                        text = selectedTime,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .clickable { timePickerDialog.show() }
                            .padding(16.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Button(
                    onClick = {
                        if (isTaskSelected) {
                            // Crea la tarea como lo haces actualmente
                            val tarea = Tarea(
                                id = 0, // Deja que Room maneje la autogeneración del ID
                                titulo = nombre,
                                descripcion = descripcion,
                                fecha = selectedDate,
                                hora = selectedTime,
                                fechaCreacion = Date(),
                                multimedia = selectedImages.joinToString(",") { it.toString() }
                            )
                            // Inserta la tarea en la base de datos
                            tareasNotasViewModel.insertTarea(
                                tarea.titulo,
                                tarea.descripcion.toString(),
                                LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("d/M/yyyy")),
                                tarea.hora,
                                tarea.multimedia.toString()
                            )
                            Log.d("nuevaComposable", "Tarea insertada: $tarea")

                            // Convierte la fecha y hora seleccionadas en un objeto LocalDateTime
                            val dateTimeString = "$selectedDate $selectedTime" // Combina fecha y hora
                            val formatter = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm")
                            val dateTime = LocalDateTime.parse(dateTimeString, formatter)

                            // Convierte LocalDateTime a milisegundos
                            val triggerTime = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                            // Crea el Intent para la notificación
                            val intent = Intent(context, NotificationReceiver::class.java).apply {
                                putExtra("task_title", tarea.titulo)
                            }

                            // Crea un PendingIntent para el Intent
                            val pendingIntent = PendingIntent.getBroadcast(
                                context,
                                tarea.id, // Usa el ID de la tarea para identificar la alarma
                                intent,
                                PendingIntent.FLAG_MUTABLE
                            )

                            // Usa AlarmManager para programar la alarma
                            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            alarmManager.set(
                                AlarmManager.RTC_WAKEUP, // Usamos RTC_WAKEUP para despertar el dispositivo
                                triggerTime, // El tiempo en milisegundos para la alarma
                                pendingIntent // El PendingIntent que se ejecutará cuando se dispare la alarma
                            )

                            Log.d("nuevaComposable", "Alarma programada para la tarea")

                            // Navegar a la pantalla de tareas
                            navController.navigate("Mis_tareas")
                        }
                     else {
                            val multimediaString = if (selectedImages.isNotEmpty()) {
                                selectedImages.joinToString(",") {
                                    it.toString() } }
                            else { "" }
                            val nota = Nota(
                                id = 0, // Deja que Room maneje la autogeneración del ID
                                titulo = nombre,
                                descripcion = descripcion,
                                horaCreacion = Date(),
                                multimedia = multimediaString

                            )
                            tareasNotasViewModel.insertNota(
                                nota.titulo,
                                nota.descripcion.toString(),
                                nota.horaCreacion,
                                nota.multimedia.toString()
                            )

                            Log.d("nuevaComposable", "Nota insertada: $nota")
                            navController.navigate("Notas")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFDC334)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(id = R.string.Añadir), fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ArchivosSeleccionados(fileUris: List<Uri>,onDeleteUri: (Uri) -> Unit, context: Context) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedMimeType by remember { mutableStateOf<String?>(null) }
    // Column que muestra los archivos seleccionados
    Column(modifier = Modifier.fillMaxWidth()) {
        fileUris.forEach { fileUri ->
            val mimeType = context.contentResolver.getType(fileUri)

            val iconRes = when {
                mimeType?.startsWith("video/") == true -> R.drawable.video_editor
                mimeType?.startsWith("audio/") == true -> R.drawable.music_file
                mimeType?.startsWith("image/") == true -> R.drawable.photo
                else -> R.drawable.folder
            }

            val description = when {
                mimeType?.startsWith("video/") == true -> "Video seleccionado"
                mimeType?.startsWith("audio/") == true -> "Audio seleccionado"
                mimeType?.startsWith("image/") == true -> "Imagen seleccionada"
                else -> ""
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = description,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            selectedUri = fileUri
                            selectedMimeType = mimeType // Guardamos el tipo de archivo seleccionado
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Mostrar un dialogo con el archivo seleccionado
    if (selectedUri != null) {
        AlertDialog(
            onDismissRequest = { selectedUri = null },
            title = {
                Text(text = "Vista previa")
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Mostrar la imagen, video o audio según el tipo de archivo
                    when (selectedMimeType?.substringBefore("/")) {
                        "image" -> {
                            val painter = rememberImagePainter(selectedUri)
                            Image(
                                painter = painter,
                                contentDescription = "Vista previa de la imagen",
                                modifier = Modifier.size(200.dp)
                            )
                        }
                        "video" -> {
                            VideoPlayer(uri = selectedUri) // Llamamos a la función para mostrar el video
                        }
                        "audio" -> {
                            AudioPlayer(uri = selectedUri) // Llamamos a la función para reproducir el audio
                        }
                        else -> {
                            Text("Tipo de archivo no soportado")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedUri = null }) {
                    Text("Cerrar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDeleteUri(selectedUri!!)
                    selectedUri = null
                    selectedMimeType = null
                }) {
                    Text("Eliminar")
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VideoPlayer(uri: Uri?) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            uri?.let {
                setMediaItem(MediaItem.fromUri(it))
                prepare()
                playWhenReady = true
            }
        }
    }
    // Reproductor de video utilizando ExoPlayer
    AndroidView(
        factory = {
            PlayerView(context).apply {
                this.player = player
                useController = true // Muestra los controles del reproductor
            }
        },
        modifier = Modifier.size(300.dp)
    )
    // Liberar el reproductor cuando el Composable se descarte
    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }
}


@Composable
fun AudioPlayer(uri: Uri?) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer: MediaPlayer? = remember { MediaPlayer() }

    LaunchedEffect(uri) {
        mediaPlayer?.setDataSource(context, uri!!)
        mediaPlayer?.prepare()
    }

    Button(
        onClick = {
            if (isPlaying) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
            }
            isPlaying = !isPlaying
        }
    ) {
        Text(if (isPlaying) "Pausar" else "Reproducir")
    }
}



fun selectImageFromGallery(context: Context, launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    launcher.launch(intent)
}
fun selectVideoFromGallery(context: Context, launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "video/*"
    if (intent.resolveActivity(context.packageManager) != null) {
        launcher.launch(intent)
    } else {
        Toast.makeText(context, "No se encontró una aplicación para manejar el video.", Toast.LENGTH_SHORT).show()
    }
}
fun selectAudioFromGallery(context: Context, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "audio/*" // Establece el tipo para archivos de audio
    launcher.launch(intent)
}

@Composable
fun SegmentedButton(
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color(0xFFFDC334), RoundedCornerShape(16.dp))
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    Color(0xFFFDC334),
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                )
                .clickable(onClick = onLeftClick),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(id = R.string.nota), color = MaterialTheme.colorScheme.onSecondary)
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                )
                .clickable(onClick = onRightClick),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(id = R.string.tarea), color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}



