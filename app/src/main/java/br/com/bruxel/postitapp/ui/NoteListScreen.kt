package br.com.bruxel.postitapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.bruxel.postitapp.model.Note

@Composable
fun NoteListScreen(
    notes: List<Note>,
    onToggleArchived: (Note) -> Unit
) {
    LazyColumn {
        items(notes) { note ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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

                    IconButton(onClick = { onToggleArchived(note) }) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Arquivar/Desarquivar",
                            tint = if (note.isArchived) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}