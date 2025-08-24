package br.com.bruxel.postitapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.bruxel.postitapp.model.Note
import br.com.bruxel.postitapp.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Int?,
    viewModel: NoteViewModel,
    onNavigateUp: () -> Unit
) {
    val loadedNote by (noteId?.let { viewModel.getNote(it).collectAsState() } ?: remember { mutableStateOf<Note?>(null) })

    var initialized by remember(noteId, loadedNote) { mutableStateOf(false) }

    var title by rememberSaveable(noteId) { mutableStateOf("") }
    var content by rememberSaveable(noteId) { mutableStateOf("") }
    var category by rememberSaveable(noteId) { mutableStateOf("") }
    var isPinned by rememberSaveable(noteId) { mutableStateOf(false) }
    var colorHex by rememberSaveable(noteId) { mutableStateOf("#FFCC00") }

    var titleError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(loadedNote, noteId) {
        if (!initialized) {
            val ln = loadedNote
            if (noteId != null && ln != null) {
                title = ln.title
                content = ln.content
                category = ln.category
                isPinned = ln.isPinned
                colorHex = ln.color
            }
            initialized = true
        }
    }

    val canSave = title.isNotBlank() || content.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null) "Nova Nota" else "Editar Nota") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        titleError = if (title.isBlank() && content.isBlank()) "Título ou conteúdo obrigatório" else null
                        if (!canSave) return@TextButton
                        val now = System.currentTimeMillis()
                        val base = loadedNote
                        val toSave = if (base != null) {
                            base.copy(
                                title = title,
                                content = content,
                                category = category,
                                color = colorHex,
                                isPinned = isPinned,
                                timestamp = now
                            )
                        } else {
                            Note(
                                id = 0,
                                title = title,
                                content = content,
                                color = colorHex,
                                category = category,
                                isPinned = isPinned,
                                isArchived = false,
                                isDeleted = false,
                                timestamp = now
                            )
                        }
                        viewModel.saveNote(toSave)
                        onNavigateUp()
                    }, enabled = canSave) {
                        Text("Salvar")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; if (titleError != null) titleError = null },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError != null,
                supportingText = { if (titleError != null) Text(titleError!!) }
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Conteúdo") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoria") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Cor", style = MaterialTheme.typography.titleMedium)
            androidx.compose.foundation.layout.FlowRow {
                listOf(
                    "#FFCC00", "#FF8A80", "#80D8FF", "#CCFF90", "#FFD180", "#EA80FC",
                    "#A7FFEB", "#FFFF8D", "#CFD8DC", "#B39DDB"
                ).forEach { hex ->
                    val c = parseHexColor(hex)
                    val isSelected = hex.equals(colorHex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp, bottom = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .clickable { colorHex = hex }
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fixar nota")
                Switch(checked = isPinned, onCheckedChange = { isPinned = it })
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
