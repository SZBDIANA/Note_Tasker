package com.example.note_tasker.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.TypeConverters

import java.util.Date

@Entity(tableName = "tareas")
@TypeConverters(Converters::class)
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "titulo", defaultValue = "Sin título") val titulo: String = "Sin título",
    @ColumnInfo(name = "descripcion") val descripcion: String?,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "hora") val hora: String,
    @ColumnInfo(name = "terminada") val terminada: Boolean = false,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Date = Date(),
    @ColumnInfo(name = "multimedia") val multimedia: String? // Nueva columna para multimedia
)

