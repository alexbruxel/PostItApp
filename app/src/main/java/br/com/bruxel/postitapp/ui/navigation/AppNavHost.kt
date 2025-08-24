package br.com.bruxel.postitapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.bruxel.postitapp.viewmodel.NoteViewModel
import br.com.bruxel.postitapp.model.Note

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val vm: NoteViewModel = hiltViewModel()
            br.com.bruxel.postitapp.ui.NoteHomeScreen(
                viewModel = vm,
                onAddClick = { navController.navigate("editor") },
                onOpenEditor = { note: Note -> navController.navigate("editor/${note.id}") }
            )
        }
        composable(route = "editor") {
            val vm: NoteViewModel = hiltViewModel()
            br.com.bruxel.postitapp.ui.NoteEditorScreen(
                noteId = null,
                viewModel = vm,
                onNavigateUp = { navController.popBackStack() }
            )
        }
        composable(
            route = "editor/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vm: NoteViewModel = hiltViewModel()
            val id = backStackEntry.arguments?.getInt("noteId")
            br.com.bruxel.postitapp.ui.NoteEditorScreen(
                noteId = id,
                viewModel = vm,
                onNavigateUp = { navController.popBackStack() }
            )
        }
    }
}
