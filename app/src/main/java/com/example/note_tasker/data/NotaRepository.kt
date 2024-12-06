package com.example.note_tasker.data

import com.example.note_tasker.data.Nota

interface NotaRepository {
    fun getAllNotas(): List<Nota>
    fun getNotaById(id: Int): Nota?
    suspend fun insertNota(nota: Nota)
    suspend fun updateNota(nota: Nota)
    suspend fun deleteNota(nota: Nota)
    //suspend fun obtenerUltimoIdNota():Int?
}