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
import androidx.activity.compose.BackHandler

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
    val selectedCategories = remember(noteId) { mutableStateListOf<String>() }
    var newCategory by rememberSaveable(noteId) { mutableStateOf("") }
    var isPinned by rememberSaveable(noteId) { mutableStateOf(false) }
    var colorHex by rememberSaveable(noteId) { mutableStateOf("#FFCC00") }

    var titleError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(loadedNote, noteId) {
        if (!initialized) {
            val ln = loadedNote
            if (noteId != null && ln != null) {
                title = ln.title
                content = ln.content
                selectedCategories.clear(); selectedCategories.addAll(ln.categories)
                isPinned = ln.isPinned
                colorHex = ln.color
            }
            initialized = true
        }
    }

    fun listsDiffer(a: List<String>, b: List<String>): Boolean {
        if (a.size != b.size) return true
        return a.sorted() != b.sorted()
    }

    val isDirty by remember(title, content, selectedCategories.toList(), isPinned, colorHex, loadedNote, noteId) {
        mutableStateOf(
            if (noteId == null) {
                title.isNotBlank() || content.isNotBlank() || selectedCategories.isNotEmpty() || isPinned || colorHex != "#FFCC00"
            } else {
                val ln = loadedNote
                ln != null && (
                    title != ln.title ||
                        content != ln.content ||
                        listsDiffer(selectedCategories, ln.categories) ||
                        isPinned != ln.isPinned ||
                        !colorHex.equals(ln.color, ignoreCase = true)
                )
            }
        )
    }

    var showExitConfirm by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        if (isDirty) showExitConfirm = true else onNavigateUp()
    }

    val canSave = title.isNotBlank() || content.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null) "Nova Nota" else "Editar Nota") },
                navigationIcon = {
                    IconButton(onClick = { if (isDirty) showExitConfirm = true else onNavigateUp() }) {
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
                                categories = selectedCategories.toList(),
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
                                categories = selectedCategories.toList(),
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

            // Categorias (múltipla seleção)
            Text("Categorias", style = MaterialTheme.typography.titleMedium)
            FlowRowCategories(
                selected = selectedCategories,
                onRemove = { selectedCategories.remove(it) }
            )
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

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text("Descartar alterações?") },
            text = { Text("Você possui alterações não salvas. Deseja descartar?") },
            confirmButton = {
                TextButton(onClick = { showExitConfirm = false; onNavigateUp() }) {
                    Text("Descartar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) {
                    Text("Continuar editando")
                }
            }
        )
    }
}

@Composable
private fun FlowRowCategories(
    selected: List<String>,
    onRemove: (String) -> Unit
) {
    androidx.compose.foundation.layout.FlowRow {
        selected.forEach { c ->
            FilterChip(
                selected = true,
                onClick = { onRemove(c) },
                label = { Text(c) }
            )
            Spacer(Modifier.width(8.dp))
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
