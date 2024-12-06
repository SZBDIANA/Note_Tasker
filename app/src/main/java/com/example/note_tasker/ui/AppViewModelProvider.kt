package com.example.note_tasker.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.note_tasker.Note_TaskerApplication
import com.example.note_tasker.viewmodel.TareasNotasViewModel


object AppViewModelProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    val Factory = viewModelFactory {
        initializer {
                TareasNotasViewModel(
                    Note_TaskerApplication().container.tareaRepository,
                    Note_TaskerApplication().container.notaRepository,
                    Note_TaskerApplication().container.multimediaNotaRepository,
                    Note_TaskerApplication().container.multimediaTareaRepository
                )
        }
    }
}
 fun CreationExtras.Note_TaskerApplication(): Note_TaskerApplication =
     (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Note_TaskerApplication)