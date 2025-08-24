package br.com.bruxel.postitapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.bruxel.postitapp.model.Note
import br.com.bruxel.postitapp.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val repo: NoteRepository) : ViewModel() {
    /**
     * Notas ativas, arquivadas e deletadas expostas como StateFlow para consumo reativo na UI.
     */
    val activeNotes: StateFlow<List<Note>> = repo.activeNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val archivedNotes: StateFlow<List<Note>> = repo.archivedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val deletedNotes: StateFlow<List<Note>> = repo.deletedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Insere uma nova nota no banco de dados.
     */
    fun insertNote(note: Note) = viewModelScope.launch {
        repo.insert(note)
    }

    /**
     * Alterna o status de arquivamento da nota.
     */
    fun toggleArchive(note: Note) = viewModelScope.launch {
        repo.toggleArchive(note)
    }

    /**
     * Marca a nota como deletada.
     */
    fun deleteNote(note: Note) = viewModelScope.launch {
        repo.delete(note)
    }

    /**
     * Restaura uma nota deletada.
     */
    fun restoreNote(note: Note) = viewModelScope.launch {
        repo.restore(note)
    }

    fun getNote(noteId: Int): StateFlow<Note?> = repo.getNoteById(noteId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    suspend fun getNoteImmediate(noteId: Int): Note? = repo.getNoteImmediate(noteId)

    fun saveNote(note: Note) = viewModelScope.launch {
        if (note.id == 0) repo.insert(note) else repo.update(note)
    }

    val allCategories: StateFlow<List<String>> = repo.getAllNotes()
        .map { notes ->
            val seen = linkedMapOf<String, String>()
            notes.forEach { n ->
                n.categories.forEach { raw ->
                    val t = raw.trim()
                    if (t.isNotEmpty()) {
                        val key = t.lowercase()
                        if (!seen.containsKey(key)) seen[key] = t
                    }
                }
            }
            // Mantém a ordem de primeira ocorrência conforme gravada
            seen.values.toList()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}