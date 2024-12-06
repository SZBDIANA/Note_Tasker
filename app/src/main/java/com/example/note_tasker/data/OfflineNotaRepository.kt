package com.example.note_tasker.data


class OfflineNotaRepository(private val notaDAO: NotaDAO): NotaRepository {
    override fun getAllNotas(): List<Nota> = notaDAO.getAllNotas()
    override fun getNotaById(id: Int): Nota? = notaDAO.getNotaById(id)
    override suspend fun insertNota(nota: Nota) = notaDAO.insertNota(nota)
    override suspend fun updateNota(nota: Nota)= notaDAO.updateNota(nota)
    override suspend fun deleteNota(nota: Nota)= notaDAO.deleteNota(nota)
    //override suspend fun obtenerUltimoIdNota(): Int? = notaDAO.obtenerUltimoIdNota()
}