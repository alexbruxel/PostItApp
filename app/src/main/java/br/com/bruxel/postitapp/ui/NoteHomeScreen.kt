package br.com.bruxel.postitapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.bruxel.postitapp.model.Note
import br.com.bruxel.postitapp.viewmodel.NoteViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteHomeScreen(
    viewModel: NoteViewModel,
    onAddClick: () -> Unit,
    onOpenEditor: (Note) -> Unit
) {
    val activeNotes by viewModel.activeNotes.collectAsState(initial = emptyList())
    val archivedNotes by viewModel.archivedNotes.collectAsState(initial = emptyList())
    val deletedNotes by viewModel.deletedNotes.collectAsState(initial = emptyList())
    val allCategories by viewModel.allCategories.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableIntStateOf(0) }

    // Estado de filtro por categoria
    val selectedFilters = remember { mutableStateListOf<String>() }
    var filterQuery by remember { mutableStateOf("") }
    var useAnd by remember { mutableStateOf(false) } // false = OR (padrão), true = AND
    var filtersExpanded by remember { mutableStateOf(false) }

    fun List<Note>.applyCategoryFilter(): List<Note> {
        if (selectedFilters.isEmpty()) return this
        return if (useAnd) {
            // AND: nota deve conter TODAS as categorias selecionadas
            this.filter { n ->
                val noteCats = n.categories.map { it.lowercase() }
                selectedFilters.all { f -> noteCats.any { it.equals(f, ignoreCase = true) } }
            }
        } else {
            // OR: nota entra se possuir QUALQUER categoria selecionada
            this.filter { n -> n.categories.any { c -> selectedFilters.any { it.equals(c, ignoreCase = true) } } }
        }
    }

    val sourceNotes = when (selectedTab) {
        0 -> activeNotes
        1 -> archivedNotes
        else -> deletedNotes
    }
    val filteredNotes = remember(sourceNotes, selectedFilters, useAnd) { sourceNotes.applyCategoryFilter() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Nota")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp)
        ) {
            // Card expansível de filtros
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Filtros", style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { filtersExpanded = !filtersExpanded }) {
                            Icon(
                                imageVector = if (filtersExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = if (filtersExpanded) "Recolher filtros" else "Expandir filtros"
                            )
                        }
                    }
                    AnimatedVisibility(visible = filtersExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Busca e filtros por categoria
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = filterQuery,
                                    onValueChange = { filterQuery = it },
                                    label = { Text("Buscar categoria") },
                                    modifier = Modifier.weight(1f)
                                )
                                val canAdd = filterQuery.isNotBlank() && selectedFilters.none { it.equals(filterQuery, true) }
                                Button(enabled = canAdd, onClick = {
                                    val q = filterQuery.trim()
                                    if (q.isNotEmpty() && selectedFilters.none { it.equals(q, true) }) {
                                        selectedFilters.add(q)
                                        filterQuery = ""
                                    }
                                }) { Text("Adicionar") }
                            }

                            // Seletor AND/OR
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Combinar:", style = MaterialTheme.typography.titleSmall)
                                FilterChip(selected = !useAnd, onClick = { useAnd = false }, label = { Text("OR") })
                                FilterChip(selected = useAnd, onClick = { useAnd = true }, label = { Text("AND") })
                            }

                            val suggestions = remember(allCategories, filterQuery, selectedFilters) {
                                allCategories
                                    .filter { it.isNotBlank() }
                                    .filter { s -> filterQuery.isBlank() || s.contains(filterQuery, ignoreCase = true) }
                                    .filter { s -> selectedFilters.none { it.equals(s, ignoreCase = true) } }
                                    .take(20)
                            }
                            if (suggestions.isNotEmpty()) {
                                Text("Sugestões", style = MaterialTheme.typography.titleSmall)
                                androidx.compose.foundation.layout.FlowRow {
                                    suggestions.forEach { s ->
                                        AssistChip(onClick = {
                                            if (selectedFilters.none { it.equals(s, true) }) selectedFilters.add(s)
                                        }, label = { Text(s) })
                                        Spacer(Modifier.width(8.dp))
                                    }
                                }
                            }

                            if (selectedFilters.isNotEmpty()) {
                                Text("Selecionadas", style = MaterialTheme.typography.titleSmall)
                                androidx.compose.foundation.layout.FlowRow {
                                    selectedFilters.forEach { f ->
                                        FilterChip(selected = true, onClick = { selectedFilters.remove(f) }, label = { Text(f) })
                                        Spacer(Modifier.width(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Ativas", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Arquivadas", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                    Text("Lixeira", modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            NoteListScreen(
                notes = filteredNotes,
                onArchive = viewModel::toggleArchive,
                onDelete = viewModel::deleteNote,
                onRestore = viewModel::restoreNote,
                onNoteClick = onOpenEditor
            )
        }
    }
}
