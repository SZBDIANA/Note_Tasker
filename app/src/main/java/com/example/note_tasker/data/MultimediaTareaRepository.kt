package com.example.note_tasker.data

import com.example.note_tasker.data.MultimediaTarea

interface MultimediaTareaRepository {
    fun getAllMultimediaTareas(tareaId: Int): List<MultimediaTarea>
    suspend fun insertMultimediaTarea(multimediaTarea: MultimediaTarea)
}