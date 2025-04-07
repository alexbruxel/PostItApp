package br.com.bruxel.postitapp.repository

import br.com.bruxel.postitapp.data.NoteDao
import br.com.bruxel.postitapp.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }
}