package br.com.bruxel.postitapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.bruxel.postitapp.model.Note
import br.com.bruxel.postitapp.viewmodel.NoteViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NoteScreen(viewModel: NoteViewModel = hiltViewModel()) {
    val activeNotes by viewModel.activeNotes.collectAsState(initial = emptyList())
    val archivedNotes by viewModel.archivedNotes.collectAsState(initial = emptyList())
    val deletedNotes by viewModel.deletedNotes.collectAsState(initial = emptyList())
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Button(onClick = {
            viewModel.insertNote(
                Note(
                    title = "Nova Nota",
                    content = "Conteúdo",
                    color = "#FFCC00",
                    category = "Trabalho",
                    timestamp = System.currentTimeMillis()
                )
            )
        }) {
            Text("Adicionar Nota")
        }

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

        when (selectedTab) {
            0 -> NoteListScreen(activeNotes, onArchive = viewModel::toggleArchive, onDelete = viewModel::deleteNote)
            1 -> NoteListScreen(archivedNotes, onArchive = viewModel::toggleArchive, onDelete = viewModel::deleteNote)
            2 -> NoteListScreen(deletedNotes, onRestore = viewModel::restoreNote)
        }
    }
}
