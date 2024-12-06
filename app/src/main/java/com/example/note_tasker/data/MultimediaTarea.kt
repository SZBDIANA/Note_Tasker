package com.example.note_tasker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "multimedia_tareas",
    foreignKeys = [ForeignKey(
        entity = Tarea::class,
        parentColumns = ["id"],
        childColumns = ["tareaId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class MultimediaTarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tareaId: Int,
    val url: String
)
