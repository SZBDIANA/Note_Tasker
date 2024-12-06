package com.example.note_tasker.data



class OfflineTareaRepository(private val tareaDAO: TareaDAO): TareaRepository {
    override fun getAllTarea(): List<Tarea> =  tareaDAO.getAllTareas()
    override fun getTareaById(id: Int): Tarea? = tareaDAO.getTareaById(id)
    override fun getTareasByDate(fecha: String): List<Tarea> = tareaDAO.getTareasByDate(fecha)
    override suspend fun insertTarea(tarea: Tarea) = tareaDAO.insertTarea(tarea)
    override suspend fun updateTarea(tarea: Tarea) = tareaDAO.updateTarea(tarea)
    override suspend fun deleteTarea(tarea: Tarea) = tareaDAO.deleteTarea(tarea)
    override suspend fun updateTareaTerminada(id: Int, terminada: Boolean) = tareaDAO.updateTareaTerminada(id, terminada)
    override fun getTareasCompletadas(): List<Tarea> = tareaDAO.getTareasCompletadas()
    override fun getTareasProgramadas(): List<Tarea> = tareaDAO.getTareasProgramadas()
}