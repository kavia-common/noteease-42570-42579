package org.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import org.example.app.data.Note
import org.example.app.data.NoteRepository

// LIST

data class ListUiState(
    val query: String = "",
    val notes: List<Note> = emptyList(),
    val isRefreshing: Boolean = false
)

// PUBLIC_INTERFACE
class ListViewModel(private val repo: NoteRepository) : ViewModel() {
    private val query = MutableStateFlow("")
    private val refreshing = MutableStateFlow(false)

    val uiState: StateFlow<ListUiState> =
        combine(repo.notes(), query, refreshing) { notes, q, r ->
            val filtered = if (q.isBlank()) notes else notes.filter {
                it.title.contains(q, ignoreCase = true) || it.content.contains(q, ignoreCase = true)
            }
            ListUiState(query = q, notes = filtered, isRefreshing = r)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ListUiState())

    fun onQueryChange(value: String) = query.update { value }
    fun refresh() {
        refreshing.update { true }
        repo.refresh()
        refreshing.update { false }
    }

    companion object {
        // PUBLIC_INTERFACE
        fun factory(repo: NoteRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { ListViewModel(repo) }
        }
    }
}

// EDITOR

data class EditorUiState(
    val id: String? = null,
    val title: String = "",
    val content: String = "",
    val titleError: String? = null
)

// PUBLIC_INTERFACE
class EditorViewModel(
    private val repo: NoteRepository,
    private val noteId: String?
) : ViewModel() {
    private val _ui = MutableStateFlow(EditorUiState())

    val uiState: StateFlow<EditorUiState> get() = _ui

    init {
        if (noteId != null) {
            repo.get(noteId)?.let { n ->
                _ui.update { it.copy(id = n.id, title = n.title, content = n.content, titleError = null) }
            }
        }
    }

    fun onTitleChange(v: String) = _ui.update { it.copy(title = v, titleError = null) }
    fun onContentChange(v: String) = _ui.update { it.copy(content = v) }

    fun save(): Boolean {
        val current = _ui.value
        if (current.title.isBlank()) {
            _ui.update { it.copy(titleError = "Title is required") }
            return false
        }
        val note = Note(
            id = current.id ?: "",
            title = current.title.trim(),
            content = current.content.trim()
        )
        val id = if (current.id == null) {
            // new -> repository will assign UUID if blank, but our Note model creates random only in default argument
            // For consistency, pass empty id to trigger replacement logic: we'll handle UUID by creating a new one beforehand
            val created = note.copy(id = java.util.UUID.randomUUID().toString())
            repo.upsert(created)
        } else {
            repo.upsert(note)
        }
        return true
    }

    fun delete() {
        val id = _ui.value.id ?: return
        repo.delete(id)
    }

    companion object {
        // PUBLIC_INTERFACE
        fun factory(repo: NoteRepository, noteId: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer { EditorViewModel(repo, noteId) }
        }
    }
}

// DETAIL

data class DetailUiState(
    val note: Note? = null
)

// PUBLIC_INTERFACE
class DetailViewModel(
    private val repo: NoteRepository,
    private val id: String
) : ViewModel() {
    private val _ui = MutableStateFlow(DetailUiState(note = repo.get(id)))
    val uiState: StateFlow<DetailUiState> get() = _ui

    fun delete() {
        repo.delete(id)
    }

    fun share() {
        // placeholder hook to integrate with OS share later
    }

    companion object {
        // PUBLIC_INTERFACE
        fun factory(repo: NoteRepository, id: String): ViewModelProvider.Factory = viewModelFactory {
            initializer { DetailViewModel(repo, id) }
        }
    }
}
