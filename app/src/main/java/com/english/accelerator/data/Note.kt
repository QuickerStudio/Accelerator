package com.english.accelerator.data

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

object NoteManager {
    private val notes = mutableListOf<Note>()
    private var nextId = 1

    fun getAllNotes(): List<Note> {
        return notes.filter { it.title.isNotEmpty() || it.content.isNotEmpty() }
    }

    fun addNote(title: String, content: String): Note {
        val note = Note(
            id = nextId++,
            title = title,
            content = content
        )
        notes.add(note)
        return note
    }

    fun updateNote(id: Int, title: String, content: String) {
        val index = notes.indexOfFirst { it.id == id }
        if (index != -1) {
            notes[index] = notes[index].copy(
                title = title,
                content = content,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    fun deleteNote(id: Int) {
        notes.removeAll { it.id == id }
    }

    fun getNoteById(id: Int): Note? {
        return notes.find { it.id == id }
    }
}
