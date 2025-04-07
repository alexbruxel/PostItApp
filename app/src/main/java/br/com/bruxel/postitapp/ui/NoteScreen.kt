package br.com.bruxel.postitapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import br.com.bruxel.postitapp.model.Note
import br.com.bruxel.postitapp.viewmodel.NoteViewModel

@Composable
fun NoteScreen(viewModel: NoteViewModel = ViewModelProvider(LocalContext.current as ComponentActivity).get(NoteViewModel::class.java)) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = {
            viewModel.addNote(Note(title = "Nova Nota", content = "Conteúdo", color = "#FFCC00", category = "Trabalho", timestamp = System.currentTimeMillis()))
        }) {
            Text("Adicionar Nota")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(notes) { note ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = note.title, style = MaterialTheme.typography.titleLarge)
                        Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}