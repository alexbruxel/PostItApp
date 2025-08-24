package br.com.bruxel.postitapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.bruxel.postitapp.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM Note WHERE isArchived = 0 AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getActiveNotes(): Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE isArchived = 1 AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getArchivedNotes(): Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE isDeleted = 1 ORDER BY timestamp DESC")
    fun getDeletedNotes(): Flow<List<Note>>

    @Query("UPDATE Note SET isArchived = :value WHERE id = :noteId")
    suspend fun setArchived(noteId: Int, value: Boolean)

    @Query("UPDATE Note SET isDeleted = :value WHERE id = :noteId")
    suspend fun setDeleted(noteId: Int, value: Boolean)

    @Query("SELECT * FROM Note WHERE id = :noteId")
    fun getNoteById(noteId: Int): Flow<Note?>

    @Query("SELECT * FROM Note WHERE id = :noteId LIMIT 1")
    suspend fun getNoteImmediate(noteId: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("SELECT * FROM Note WHERE 1=1 ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Note>>
}
