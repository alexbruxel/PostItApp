package br.com.bruxel.postitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.bruxel.postitapp.model.Note
import br.com.bruxel.postitapp.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoteViewModel @Inject constructor(private val repository: NoteRepository) : ViewModel() {
    val allNotes: Flow<List<Note>> = repository.allNotes

    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.insert(note)
        }
    }
}