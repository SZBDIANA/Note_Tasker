package com.example.note_tasker.data



class OfflineMultimediaNotaRepository(private val multimediaNotaDAO: MultimediaNotaDAO): MultimediaNotaRepository {
    override fun getAllMultimediaNotas(notaId:Int): List<MultimediaNota> = multimediaNotaDAO.getMultimediaNotasForNota(notaId)
    override suspend fun insertMultimediaNota(multimediaNota: MultimediaNota) = multimediaNotaDAO.insertMultimediaNota(multimediaNota)
}