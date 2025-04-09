package br.com.bruxel.postitapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.bruxel.postitapp.model.Note

@Composable
fun NoteListScreen(
    notes: List<Note>,
    onArchive: ((Note) -> Unit)? = null,
    onDelete: ((Note) -> Unit)? = null,
    onRestore: ((Note) -> Unit)? = null
) {
    LazyColumn {
        items(notes) { note ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = note.title, style = MaterialTheme.typography.titleLarge)
                        Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
                        Text(text = note.id.toString(), style = MaterialTheme.typography.bodyMedium)
                    }

                    Row {
                        when {
                            onArchive != null -> {
                                IconButton(onClick = { onArchive(note) }) {
                                    Icon(
                                        imageVector = if (note.isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                                        contentDescription = "Arquivar"
                                    )
                                }
                            }

                            onRestore != null -> {
                                IconButton(onClick = { onRestore(note) }) {
                                    Icon(Icons.Default.Restore, contentDescription = "Restaurar")
                                }
                            }
                        }

                        if (onDelete != null) {
                            IconButton(onClick = { onDelete(note) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Deletar")
                            }
                        }
                    }
                }
            }
        }
    }
}