package br.com.bruxel.postitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.bruxel.postitapp.model.Note
import br.com.bruxel.postitapp.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val repository: NoteRepository) : ViewModel() {
    val activeNotes: Flow<List<Note>> = repository.activeNotes
    val archivedNotes: Flow<List<Note>> = repository.archivedNotes

    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.insert(note)
        }
    }

    fun toggleArchived(note: Note) {
        viewModelScope.launch {
            repository.setArchived(note.id, !note.isArchived)
        }
    }
}