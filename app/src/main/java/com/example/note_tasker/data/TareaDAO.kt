package com.example.note_tasker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TareaDAO {
    @Insert
    suspend fun insertTarea(tarea: Tarea)

    @Query("SELECT * FROM tareas")
    fun getAllTareas(): List<Tarea>

    @Query("SELECT * FROM tareas WHERE id = :id")
    fun getTareaById(id: Int): Tarea?

    @Query("SELECT * FROM tareas WHERE fecha = :fecha")
    fun getTareasByDate(fecha: String): List<Tarea>

    @Query("SELECT * FROM tareas WHERE terminada = 1")
    fun getTareasCompletadas(): List<Tarea>

    @Query("SELECT * FROM tareas WHERE terminada = 0")
    fun getTareasProgramadas(): List<Tarea>

    @Update
    suspend fun updateTarea(tarea: Tarea)

    @Delete
    suspend fun deleteTarea(tarea: Tarea)

    @Query("UPDATE tareas set terminada = :terminada WHERE id = :id ")
    suspend fun updateTareaTerminada(id: Int, terminada: Boolean)
}
