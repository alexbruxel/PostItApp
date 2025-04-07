package br.com.bruxel.postitapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import br.com.bruxel.postitapp.model.Note
import br.com.bruxel.postitapp.viewmodel.NoteViewModel

@Composable
fun NoteScreen(viewModel: NoteViewModel = ViewModelProvider(LocalContext.current as ComponentActivity)[NoteViewModel::class.java]) {
    val activeNotes by viewModel.activeNotes.collectAsState(initial = emptyList())
    val archivedNotes by viewModel.archivedNotes.collectAsState(initial = emptyList())
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Button(onClick = {
            viewModel.addNote(
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
                Text("Notas Ativas", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Arquivadas", modifier = Modifier.padding(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTab) {
            0 -> NoteListScreen(notes = activeNotes, onToggleArchived = { viewModel.toggleArchived(it) })
            1 -> NoteListScreen(notes = archivedNotes, onToggleArchived = { viewModel.toggleArchived(it) })
        }
    }
}