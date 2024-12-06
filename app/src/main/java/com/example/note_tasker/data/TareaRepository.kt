package com.example.note_tasker.data

import java.util.Date


interface TareaRepository {
    fun getAllTarea(): List<Tarea>
    fun getTareaById(id: Int): Tarea?
    fun getTareasByDate(fecha: String): List<Tarea>
    suspend fun insertTarea(tarea: Tarea)
    suspend fun updateTarea(tarea: Tarea)
    suspend fun deleteTarea(tarea: Tarea)
    suspend fun updateTareaTerminada(id: Int, terminada: Boolean)
    fun getTareasCompletadas(): List<Tarea>
    fun getTareasProgramadas(): List<Tarea>
}