package com.example.note_tasker.data

import android.content.Context


interface AppContainer {
    val  tareaRepository: TareaRepository
    val  notaRepository: NotaRepository
    val  multimediaTareaRepository: MultimediaTareaRepository
    val  multimediaNotaRepository: MultimediaNotaRepository
}
class AppContainerImpl(private val context: Context) : AppContainer {

    override val tareaRepository: TareaRepository by lazy {
        OfflineTareaRepository(Note_TaskerDatabase.getDatabase(context).tareaDAO())
    }
    override val notaRepository: NotaRepository by lazy {
        OfflineNotaRepository(Note_TaskerDatabase.getDatabase(context).notaDAO())
    }
    override val multimediaTareaRepository: MultimediaTareaRepository by lazy {
        OfflineMultimediaTareaRepository(Note_TaskerDatabase.getDatabase(context).multimediaTareaDAO())
    }
    override val multimediaNotaRepository: MultimediaNotaRepository by lazy {
        OfflineMultimediaNotaRepository(Note_TaskerDatabase.getDatabase(context).multimediaNotaDAO())
    }

}