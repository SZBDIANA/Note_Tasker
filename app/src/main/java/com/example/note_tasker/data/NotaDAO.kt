package com.example.note_tasker.data
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotaDAO {
    @Insert
    suspend fun insertNota(nota: Nota)

    @Query("SELECT * FROM notas")
    fun getAllNotas(): List<Nota>

    @Query("SELECT * FROM notas WHERE id = :id")
    fun getNotaById(id: Int): Nota

    //@Query("SELECT id FROM notas ORDER BY id DESC LIMIT 1")
    //suspend fun obtenerUltimoIdNota(): Int?

    @Update
    suspend fun updateNota(nota: Nota)

    @Delete
    suspend fun deleteNota(nota: Nota)
}
