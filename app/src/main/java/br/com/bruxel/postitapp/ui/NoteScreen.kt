package br.com.bruxel.postitapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
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
fun NoteScreen(viewModel: NoteViewModel = ViewModelProvider(LocalContext.current as ComponentActivity)[NoteViewModel::class.java]) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())
    val selectedNotes = remember { mutableStateListOf<Int>() }

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

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(notes) { note ->
                val isChecked = selectedNotes.contains(note.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            if (isChecked) {
                                selectedNotes.remove(note.id)
                            } else {
                                selectedNotes.add(note.id)
                            }
                        },
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
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedNotes.add(note.id)
                                } else {
                                    selectedNotes.remove(note.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}