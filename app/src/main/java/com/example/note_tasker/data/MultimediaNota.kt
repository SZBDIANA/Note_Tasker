package com.example.note_tasker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "multimedia_notas",
    foreignKeys = [ForeignKey(
        entity = Nota::class,
        parentColumns = ["id"],
        childColumns = ["notaId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class MultimediaNota(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val notaId: Int,
    val url: String
)
