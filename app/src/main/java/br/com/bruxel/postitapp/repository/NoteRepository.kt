package br.com.bruxel.postitapp.repository

import br.com.bruxel.postitapp.data.NoteDao
import br.com.bruxel.postitapp.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(private val dao: NoteDao) {
    val activeNotes = dao.getActiveNotes()
    val archivedNotes = dao.getArchivedNotes()
    val deletedNotes = dao.getDeletedNotes()

    suspend fun toggleArchive(note: Note) = dao.setArchived(note.id, !note.isArchived)

    suspend fun delete(note: Note) = dao.setDeleted(note.id, true)
    suspend fun restore(note: Note) = dao.setDeleted(note.id, false)

    fun getNoteById(id: Int): Flow<Note?> = dao.getNoteById(id)

    suspend fun save(note: Note) = dao.saveNote(note)
    suspend fun insert(note: Note) = dao.insert(note)
    suspend fun update(note: Note) = dao.update(note)

    fun getAllNotes(): Flow<List<Note>> = dao.getAll()
}