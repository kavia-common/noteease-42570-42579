package org.example.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.app.data.Note
import org.example.app.ui.theme.Amber500
import org.example.app.ui.theme.Blue500
import org.example.app.ui.theme.Background
import org.example.app.ui.theme.Surface
import org.example.app.viewmodel.DetailUiState
import org.example.app.viewmodel.EditorUiState
import org.example.app.viewmodel.ListUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormat = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())

// PUBLIC_INTERFACE
@Composable
fun NotesListScreen(
    state: ListUiState,
    onQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onAdd: () -> Unit,
    onOpen: (String) -> Unit
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("NoteEase", color = Color.White) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Blue500
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdd,
                containerColor = Amber500,
                contentColor = Color(0xFF1F2937),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Blue500.copy(alpha = 0.05f), Background)
                    )
                )
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search notes") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )
            Spacer(Modifier.height(8.dp))

            if (state.notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notes yet.\nTap + to create your first note.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(state.notes, key = { it.id }) { note ->
                        NoteCard(note = note, onClick = { onOpen(note.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteCard(note: Note, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(note.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(
                note.content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF6B7280)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                dateFormat.format(Date(note.updatedAt)),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

// PUBLIC_INTERFACE
@Composable
fun NoteEditorScreen(
    ui: EditorUiState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: (() -> Unit)?,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(if (ui.id == null) "New Note" else "Edit Note", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (onDelete != null) {
                        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White) }
                    }
                    IconButton(onClick = onSave) { Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.White) }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Blue500)
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Blue500.copy(alpha = 0.05f), Background))
                )
                .padding(inner)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = ui.title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                isError = ui.titleError != null,
                supportingText = { ui.titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = ui.content,
                onValueChange = onContentChange,
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.large
            )
        }
    }
}

// PUBLIC_INTERFACE
@Composable
fun NoteDetailScreen(
    state: DetailUiState,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val note = state.note
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Note", color = Color.White) },
                actions = {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White) }
                    IconButton(onClick = onShare) { Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White) }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Blue500)
            )
        }
    ) { inner ->
        if (note == null) {
            Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                Text("Note not found", color = MaterialTheme.colorScheme.error)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Blue500.copy(alpha = 0.05f), Background)))
                    .padding(inner)
                    .padding(16.dp)
            ) {
                Text(note.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Updated ${dateFormat.format(Date(note.updatedAt))}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF6B7280)
                )
                Spacer(Modifier.height(16.dp))
                Text(note.content, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
