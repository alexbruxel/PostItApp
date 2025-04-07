package br.com.bruxel.postitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import br.com.bruxel.postitapp.data.NoteDatabase
import br.com.bruxel.postitapp.repository.NoteRepository
import br.com.bruxel.postitapp.ui.NoteScreen
import br.com.bruxel.postitapp.viewmodel.NoteViewModel
import br.com.bruxel.postitapp.viewmodel.NoteViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteScreen()
        }

        // Obtenha uma instância do seu repositório (e quaisquer outras dependências)
        val noteRepository = NoteRepository(NoteDatabase.getDatabase(this).noteDao()) // Ajuste com base na sua configuração

        // Crie uma instância da sua fábrica, passando as dependências
        val factory = NoteViewModelFactory(noteRepository)

        // Obtenha o ViewModel usando a fábrica
        noteViewModel = ViewModelProvider(this, factory).get(NoteViewModel::class.java)
    }
}