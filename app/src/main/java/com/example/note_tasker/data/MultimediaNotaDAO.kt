package com.example.note_tasker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.note_tasker.data.MultimediaNota

@Dao
interface MultimediaNotaDAO {
    @Insert
    suspend fun insertMultimediaNota(multimediaNota: MultimediaNota)

    @Query("SELECT * FROM multimedia_notas WHERE notaId = :notaId")
    fun getMultimediaNotasForNota(notaId: Int): List<MultimediaNota>
}
