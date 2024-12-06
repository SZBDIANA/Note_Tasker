package com.example.note_tasker.data

import com.example.note_tasker.data.MultimediaNota

interface MultimediaNotaRepository {
    fun getAllMultimediaNotas(notaId: Int): List<MultimediaNota>
    suspend fun insertMultimediaNota(multimediaNota: MultimediaNota)
}