package br.com.bruxel.postitapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.bruxel.postitapp.model.Note
import br.com.bruxel.postitapp.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorSheet(
    noteId: Int?,
    onDismissRequest: () -> Unit,
    onSaved: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val loadedNoteState: State<Note?> = noteId?.let { id ->
        viewModel.getNote(id).collectAsState()
    } ?: run {
        remember { mutableStateOf<Note?>(null) }
    }
    val loadedNote = loadedNoteState.value

    // Sugestões de categorias reaproveitáveis
    val allCategories by viewModel.allCategories.collectAsState()

    var initialized by remember(noteId, loadedNote) { mutableStateOf(false) }

    var title by rememberSaveable(noteId) { mutableStateOf("") }
    var content by rememberSaveable(noteId) { mutableStateOf("") }
    val selectedCategories = remember(noteId) { mutableStateListOf<String>() }
    var newCategory by rememberSaveable(noteId) { mutableStateOf("") }
    var color by rememberSaveable(noteId) { mutableStateOf("#FFCC00") }
    var isPinned by rememberSaveable(noteId) { mutableStateOf(false) }

    val categorySuggestions by remember(allCategories, newCategory, selectedCategories) {
        mutableStateOf(
            allCategories
                .filter { it.isNotBlank() }
                .filter { s -> newCategory.isBlank() || s.contains(newCategory, ignoreCase = true) }
                .filter { s -> selectedCategories.none { it.equals(s, ignoreCase = true) } }
                .take(20)
        )
    }

    LaunchedEffect(loadedNote, noteId) {
        if (!initialized) {
            if (noteId != null && loadedNote != null) {
                title = loadedNote.title
                content = loadedNote.content
                selectedCategories.clear(); selectedCategories.addAll(loadedNote.categories)
                color = loadedNote.color
                isPinned = loadedNote.isPinned
            }
            initialized = true
        }
    }

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = if (noteId == null) "Nova Nota" else "Editar Nota", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Conteúdo") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Categorias (múltipla seleção)
            Text("Categorias", style = MaterialTheme.typography.titleMedium)
            androidx.compose.foundation.layout.FlowRow {
                selectedCategories.forEach { c ->
                    FilterChip(
                        selected = true,
                        onClick = { selectedCategories.remove(c) },
                        label = { Text(c) }
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newCategory,
                    onValueChange = { newCategory = it },
                    label = { Text("Nova categoria") },
                    modifier = Modifier.weight(1f)
                )
                Button(enabled = newCategory.isNotBlank(), onClick = {
                    val c = newCategory.trim()
                    if (c.isNotEmpty() && !selectedCategories.any { it.equals(c, true) }) {
                        selectedCategories.add(c)
                        newCategory = ""
                    }
                }) { Text("Adicionar") }
            }
            if (categorySuggestions.isNotEmpty()) {
                Text("Sugestões", style = MaterialTheme.typography.titleSmall)
                androidx.compose.foundation.layout.FlowRow {
                    categorySuggestions.forEach { s ->
                        AssistChip(
                            onClick = {
                                if (selectedCategories.none { it.equals(s, true) }) {
                                    selectedCategories.add(s)
                                }
                            },
                            label = { Text(s) }
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Cor (hex)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fixar nota")
                Switch(checked = isPinned, onCheckedChange = { isPinned = it })
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(modifier = Modifier.weight(1f), onClick = onDismissRequest) {
                    Text("Cancelar")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotBlank() || content.isNotBlank(),
                    onClick = {
                        val now = System.currentTimeMillis()
                        val base = loadedNote
                        val toSave = if (base != null) {
                            base.copy(
                                title = title,
                                content = content,
                                categories = selectedCategories.toList(),
                                color = color,
                                isPinned = isPinned,
                                timestamp = now
                            )
                        } else {
                            Note(
                                id = 0,
                                title = title,
                                content = content,
                                color = color,
                                categories = selectedCategories.toList(),
                                isPinned = isPinned,
                                isArchived = false,
                                isDeleted = false,
                                timestamp = now
                            )
                        }
                        viewModel.saveNote(toSave)
                        onSaved()
                    }
                ) {
                    Text("Salvar")
                }
            }
        }
    }
}
