package br.com.bruxel.postitapp.repository

import br.com.bruxel.postitapp.data.NoteDao
import br.com.bruxel.postitapp.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteDao: NoteDao) {
    val activeNotes: Flow<List<Note>> = noteDao.getActiveNotes()
    val archivedNotes: Flow<List<Note>> = noteDao.getArchivedNotes()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun setArchived(noteId: Int, isArchived: Boolean) {
        noteDao.setNoteArchived(noteId, isArchived)
    }
}