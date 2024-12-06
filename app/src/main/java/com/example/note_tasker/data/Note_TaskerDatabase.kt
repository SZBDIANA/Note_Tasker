package com.example.note_tasker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.note_tasker.data.Tarea
import com.example.note_tasker.data.TareaDAO

@Database(entities = [Tarea::class, Nota::class, MultimediaTarea::class, MultimediaNota::class], version = 4, exportSchema = false)
abstract class Note_TaskerDatabase : RoomDatabase() {

    abstract fun tareaDAO(): TareaDAO
    abstract fun notaDAO(): NotaDAO
    abstract fun multimediaTareaDAO(): MultimediaTareaDAO
    abstract fun multimediaNotaDAO(): MultimediaNotaDAO

    companion object {
        @Volatile
        private var INSTANCE: Note_TaskerDatabase? = null

        fun getDatabase(context: Context): Note_TaskerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Note_TaskerDatabase::class.java,
                    "note_tasker_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
