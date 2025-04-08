package br.com.bruxel.postitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.bruxel.postitapp.model.Note
import br.com.bruxel.postitapp.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val repo: NoteRepository) : ViewModel() {
    val activeNotes = repo.activeNotes
    val archivedNotes = repo.archivedNotes
    val deletedNotes = repo.deletedNotes

    fun addNote(note: Note) = viewModelScope.launch {
        repo.insert(note)
    }

    fun toggleArchive(note: Note) = viewModelScope.launch {
        repo.toggleArchive(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        repo.delete(note)
    }

    fun restoreNote(note: Note) = viewModelScope.launch {
        repo.restore(note)
    }
}