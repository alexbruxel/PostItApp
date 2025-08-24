package br.com.bruxel.postitapp.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.bruxel.postitapp.model.Note

@Composable
fun NoteListScreen(
    notes: List<Note>,
    onArchive: ((Note) -> Unit)? = null,
    onDelete: ((Note) -> Unit)? = null,
    onRestore: ((Note) -> Unit)? = null,
    onNoteClick: ((Note) -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
    ) {
        items(notes) { note ->
            NoteCard(
                note = note,
                onArchive = onArchive,
                onDelete = onDelete,
                onRestore = onRestore,
                onClick = onNoteClick
            )
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onArchive: ((Note) -> Unit)? = null,
    onDelete: ((Note) -> Unit)? = null,
    onRestore: ((Note) -> Unit)? = null,
    onClick: ((Note) -> Unit)? = null
) {
    val baseColor = remember(note.color) { parseHexColor(note.color) }
    val containerColor = remember(baseColor) { baseColor.copy(alpha = 0.14f) }

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke(note) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
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
                Text(text = "ID: ${note.id}", style = MaterialTheme.typography.bodySmall)
            }
            Row {
                if (onArchive != null) {
                    IconButton(onClick = { onArchive(note) }) {
                        Icon(
                            imageVector = if (note.isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                            contentDescription = if (note.isArchived) "Desarquivar nota" else "Arquivar nota"
                        )
                    }
                }
                if (onRestore != null) {
                    IconButton(onClick = { onRestore(note) }) {
                        Icon(Icons.Default.Restore, contentDescription = "Restaurar nota")
                    }
                }
                if (onDelete != null) {
                    IconButton(onClick = { onDelete(note) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Deletar nota")
                    }
                }
            }
        }
    }
}

private fun parseHexColor(hex: String): Color = try {
    val clean = hex.removePrefix("#")
    val intVal = clean.toLong(16)
    when (clean.length) {
        6 -> Color(
            red = ((intVal shr 16) and 0xFF) / 255f,
            green = ((intVal shr 8) and 0xFF) / 255f,
            blue = (intVal and 0xFF) / 255f
        )
        8 -> Color(
            alpha = ((intVal shr 24) and 0xFF) / 255f,
            red = ((intVal shr 16) and 0xFF) / 255f,
            green = ((intVal shr 8) and 0xFF) / 255f,
            blue = (intVal and 0xFF) / 255f
        )
        else -> Color(0xFFFFCC00)
    }
} catch (_: Throwable) { Color(0xFFFFCC00) }
