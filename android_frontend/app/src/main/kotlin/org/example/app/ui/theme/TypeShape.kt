package org.example.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.unit.dp

val Typography = Typography()

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)
```

Explanation: Add data models and repository with in-memory singleton and Flows.
````write file="noteease-42570-42579/android_frontend/app/src/main/kotlin/org/example/app/data/Models.kt"
package org.example.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

// PUBLIC_INTERFACE
data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// PUBLIC_INTERFACE
interface NoteRepository {
    /** Get stream of notes, ordered by updatedAt desc */
    fun notes(): StateFlow<List<Note>>

    /** Get a single note by id, or null if not found */
    fun get(id: String): Note?

    /** Create or update note; returns id */
    fun upsert(note: Note): String

    /** Delete note by id */
    fun delete(id: String)

    /** Simple refresh trigger - no-op for in-memory but useful for later persistence */
    fun refresh()
}

/**
 * In-memory repository singleton implementing NoteRepository.
 * Uses StateFlow to emit changes to observers.
 */
// PUBLIC_INTERFACE
object InMemoryNoteRepository : NoteRepository {
    private val state = MutableStateFlow<List<Note>>(emptyList())

    override fun notes(): StateFlow<List<Note>> = state.asStateFlow()

    override fun get(id: String): Note? = state.value.firstOrNull { it.id == id }

    override fun upsert(note: Note): String {
        val existing = state.value.firstOrNull { it.id == note.id }
        val now = System.currentTimeMillis()
        val newNote = if (existing == null) {
            note.copy(createdAt = now, updatedAt = now)
        } else {
            note.copy(createdAt = existing.createdAt, updatedAt = now)
        }
        state.update { list ->
            val without = list.filterNot { it.id == newNote.id }
            (without + newNote).sortedByDescending { it.updatedAt }
        }
        return newNote.id
    }

    override fun delete(id: String) {
        state.update { it.filterNot { n -> n.id == id } }
    }

    override fun refresh() {
        // no-op for memory; re-emit to notify
        state.update { it.toList() }
    }
}
