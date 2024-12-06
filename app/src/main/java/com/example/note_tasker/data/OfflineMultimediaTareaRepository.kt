package com.example.note_tasker.data


class OfflineMultimediaTareaRepository(private val multimediaTareaDAO: MultimediaTareaDAO): MultimediaTareaRepository {
    override fun getAllMultimediaTareas(tareaId: Int): List<MultimediaTarea> = multimediaTareaDAO.getMultimediaTareasForTarea(tareaId)
    override suspend fun insertMultimediaTarea(multimediaTarea: MultimediaTarea) = multimediaTareaDAO.insertMultimediaTarea(multimediaTarea)

}