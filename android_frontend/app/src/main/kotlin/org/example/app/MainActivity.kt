package org.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.example.app.data.InMemoryNoteRepository
import org.example.app.data.NoteRepository
import org.example.app.ui.NoteDetailScreen
import org.example.app.ui.NoteEditorScreen
import org.example.app.ui.NotesListScreen
import org.example.app.ui.theme.NoteEaseTheme
import org.example.app.viewmodel.DetailViewModel
import org.example.app.viewmodel.EditorViewModel
import org.example.app.viewmodel.ListViewModel

class MainActivity : ComponentActivity() {
    private val repository: NoteRepository = InMemoryNoteRepository // singleton

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteEaseTheme {
                val nav = rememberNavController()

                NavHost(
                    navController = nav,
                    startDestination = "list"
                ) {
                    composable(
                        route = "list",
                        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
                        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) }
                    ) {
                        val vm: ListViewModel = viewModel(factory = ListViewModel.factory(repository))
                        val state by vm.uiState.collectAsState()
                        NotesListScreen(
                            state = state,
                            onQueryChange = vm::onQueryChange,
                            onRefresh = vm::refresh,
                            onAdd = { nav.navigate("edit") },
                            onOpen = { id -> nav.navigate("detail/$id") }
                        )
                    }

                    composable(
                        route = "edit",
                        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
                        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) }
                    ) {
                        val vm: EditorViewModel = viewModel(factory = EditorViewModel.factory(repository, null))
                        val ui by vm.uiState.collectAsState()
                        NoteEditorScreen(
                            ui = ui,
                            onTitleChange = vm::onTitleChange,
                            onContentChange = vm::onContentChange,
                            onSave = {
                                if (vm.save()) {
                                    nav.popBackStack()
                                }
                            },
                            onDelete = null,
                            onNavigateBack = { nav.popBackStack() }
                        )
                    }

                    composable(
                        route = "edit/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")
                        val vm: EditorViewModel = viewModel(factory = EditorViewModel.factory(repository, id))
                        val ui by vm.uiState.collectAsState()
                        NoteEditorScreen(
                            ui = ui,
                            onTitleChange = vm::onTitleChange,
                            onContentChange = vm::onContentChange,
                            onSave = {
                                if (vm.save()) {
                                    nav.popBackStack()
                                }
                            },
                            onDelete = {
                                vm.delete()
                                nav.popBackStack()
                            },
                            onNavigateBack = { nav.popBackStack() }
                        )
                    }

                    composable(
                        route = "detail/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")!!
                        val vm: DetailViewModel = viewModel(factory = DetailViewModel.factory(repository, id))
                        val state by vm.uiState.collectAsState()
                        NoteDetailScreen(
                            state = state,
                            onEdit = { nav.navigate("edit/$id") },
                            onDelete = {
                                vm.delete()
                                nav.popBackStack()
                            },
                            onShare = vm::share // placeholder
                        )
                    }
                }
            }
        }
    }
}
