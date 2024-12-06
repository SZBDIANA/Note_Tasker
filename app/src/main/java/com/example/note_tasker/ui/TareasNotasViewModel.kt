package com.example.note_tasker.viewmodel

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note_tasker.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class TareasNotasViewModel(
    private val tareaRepository: TareaRepository,
    private val notaRepository: NotaRepository,
    private val multimediaNotaRepository: MultimediaNotaRepository,
    private val multimediaTareaRepository: MultimediaTareaRepository
) : ViewModel() {

    // Estado mutable para notas y tareas
    private val _notas = MutableStateFlow<List<Nota>>(emptyList())
    private val _tareas = MutableStateFlow<List<Tarea>>(emptyList())
    private val _tareasf = MutableStateFlow<List<Tarea>>(emptyList())
    private val _tareasCompletadas = MutableStateFlow<List<Tarea>>(emptyList())
    private val _tareasProgramadas = MutableStateFlow<List<Tarea>>(emptyList())

    // Estado expuesto
    val notas: StateFlow<List<Nota>> = _notas
    val tareas: StateFlow<List<Tarea>> = _tareas
    val tareasf: StateFlow<List<Tarea>> = _tareasf
    val tareasCompletadas: StateFlow<List<Tarea>> = _tareasCompletadas
    val tareasProgramadas: StateFlow<List<Tarea>> = _tareasProgramadas

    private val now = LocalDate.now()
    private val formattedDate = now.format(DateTimeFormatter.ISO_DATE)

    init {
        getNotas()
        getTareas()
        getTareasByDate(formattedDate)
        getTareasCompletadas()
        getTareasProgramadas()
    }

    // Métodos para obtener datos
    private fun getNotas() = viewModelScope.launch(Dispatchers.IO) {
        _notas.value = notaRepository.getAllNotas()
    }

    private fun getTareas() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("TareasNotasViewModel", "Obteniendo tareas...")
        _tareas.value = tareaRepository.getAllTarea()
    }

    fun getTareasByDate(fecha: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("TareasNotasViewModel", "Fecha recibida: $fecha")
        _tareasf.value = tareaRepository.getTareasByDate(fecha)
    }

    fun getTareasCompletadas() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("TareasNotasViewModel", "Obteniendo tareas completadas...")
        _tareasCompletadas.value = tareaRepository.getTareasCompletadas()
    }

    fun getTareasProgramadas() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("TareasNotasViewModel", "Obteniendo tareas programadas...")
        _tareasProgramadas.value = tareaRepository.getTareasProgramadas()
    }

    // Métodos para insertar datos
    fun insertNota(titulo: String, descripcion: String, fechaCreacion: Date, multimedia: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val nota = Nota(titulo = titulo, descripcion = descripcion, horaCreacion = fechaCreacion, multimedia = multimedia)
            notaRepository.insertNota(nota)
            getNotas()
        }
    }
    //fun obtenerUltimoIdNota(): Int? {
      //  return (notas.value.lastOrNull()?.id ?: 0) + 1
    //}

    fun insertTarea(
        titulo: String, descripcion: String, fecha: LocalDate,
        hora: String, multimedia: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val tarea = Tarea(
                titulo = titulo, descripcion = descripcion,
                fecha = fecha.toString(), hora = hora,
                multimedia = multimedia

            )
            tareaRepository.insertTarea(tarea)
            getTareas()
            getTareasByDate(formattedDate)
            getTareasCompletadas()
            getTareasProgramadas()
        }
    }

    // Métodos para actualizar datos
    fun updateTareaTerminada(id: Int, terminada: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TareasNotasViewModel", "Actualizando tarea con ID: $id")
            tareaRepository.updateTareaTerminada(id, terminada)
            getTareas()
            getTareasByDate(formattedDate)
            getTareasCompletadas()
        }
    }

    fun actualizarTarea(tarea: Tarea) {
        viewModelScope.launch(Dispatchers.IO) {
            tareaRepository.updateTarea(tarea)
            getTareas()
            getTareasByDate(formattedDate)
            getTareasCompletadas()
            getTareasProgramadas()
        }
    }
    fun actualizarNota(nota: Nota) {
        viewModelScope.launch(Dispatchers.IO) {
            notaRepository.updateNota(nota)
            getNotas()
        }
    }

    // Métodos para eliminar datos
    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch(Dispatchers.IO) {
            tareaRepository.deleteTarea(tarea)
            getTareas()
            getTareasByDate(formattedDate)
            getTareasCompletadas()
            getTareasProgramadas()
        }
    }

    fun eliminarNota(nota: Nota) {
        viewModelScope.launch(Dispatchers.IO) {
            notaRepository.deleteNota(nota)
            getNotas()
        }
    }

    // Métodos para obtener datos específicos
    fun obtenerTareaPorId(id: Int): Tarea? {
        return tareas.value.find { it.id == id }
    }
    fun obtenerNotaPorId(id: Int): Nota? {
        return notas.value.find { it.id == id }
    }

    // Métodos para insertar multimedia
    fun insertMultimediaNota(notaId: Int, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val multimediaNota = MultimediaNota(notaId = notaId, url = url)
            multimediaNotaRepository.insertMultimediaNota(multimediaNota)
            Log.d("TareasNotasViewModel", "Multimedia insertada para nota con ID: $notaId")
        }
    }

    fun insertMultimediaTarea(tareaId: Int, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val multimediaTarea = MultimediaTarea(tareaId = tareaId, url = url)
            multimediaTareaRepository.insertMultimediaTarea(multimediaTarea)
        }
    }
}

