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

    var initialized by remember(noteId, loadedNote) { mutableStateOf(false) }

    var title by rememberSaveable(noteId) { mutableStateOf("") }
    var content by rememberSaveable(noteId) { mutableStateOf("") }
    var category by rememberSaveable(noteId) { mutableStateOf("") }
    var color by rememberSaveable(noteId) { mutableStateOf("#FFCC00") }
    var isPinned by rememberSaveable(noteId) { mutableStateOf(false) }

    LaunchedEffect(loadedNote, noteId) {
        if (!initialized) {
            if (noteId != null && loadedNote != null) {
                title = loadedNote.title
                content = loadedNote.content
                category = loadedNote.category
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

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoria") },
                modifier = Modifier.fillMaxWidth()
            )

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
                                category = category,
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
                                category = category,
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
