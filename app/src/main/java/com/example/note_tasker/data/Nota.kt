package com.example.note_tasker.data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date


@Entity(tableName = "notas")
@TypeConverters(Converters::class)
data class Nota(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "titulo", defaultValue = "Sin título") val titulo: String = "Sin título",
    @ColumnInfo(name = "descripcion") val descripcion: String?,
    @ColumnInfo(name = "hora_creacion") val horaCreacion: Date = Date(), // Hora de creación predeterminada
    @ColumnInfo(name = "multimedia") val multimedia: String? // Nueva columna para multimedia
)

