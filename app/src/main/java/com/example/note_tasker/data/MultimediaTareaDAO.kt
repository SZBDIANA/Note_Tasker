package com.example.note_tasker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MultimediaTareaDAO {
    @Insert
    suspend fun insertMultimediaTarea(multimediaTarea: MultimediaTarea)

    @Query("SELECT * FROM multimedia_tareas WHERE tareaId = :tareaId")
    fun getMultimediaTareasForTarea(tareaId: Int): List<MultimediaTarea>
}
