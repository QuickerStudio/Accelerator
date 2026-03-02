package com.english.accelerator.data

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val groupId: Int? = null  // 关联的分组 ID
)

object NoteManager {
    private val notes = mutableListOf<Note>()
    private var nextId = 1

    fun getAllNotes(): List<Note> {
        return notes.filter { it.title.isNotEmpty() || it.content.isNotEmpty() }
            .sortedByDescending { it.timestamp }
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

    fun updateNoteGroup(noteId: Int, groupId: Int?) {
        val index = notes.indexOfFirst { it.id == noteId }
        if (index != -1) {
            notes[index] = notes[index].copy(groupId = groupId)
        }
    }

    fun getNotesByGroup(groupId: Int?): List<Note> {
        return notes.filter {
            (it.title.isNotEmpty() || it.content.isNotEmpty()) && it.groupId == groupId
        }.sortedByDescending { it.timestamp }
    }

    fun deleteNote(id: Int) {
        notes.removeAll { it.id == id }
    }

    fun getNoteById(id: Int): Note? {
        return notes.find { it.id == id }
    }
}

// 笔记分组数据模型
data class NoteGroup(
    val id: Int,
    val name: String,
    val timestamp: Long = System.currentTimeMillis()
)

object NoteGroupManager {
    private val groups = mutableListOf<NoteGroup>()
    private var nextId = 1

    fun getAllGroups(): List<NoteGroup> {
        return groups.sortedByDescending { it.timestamp }
    }

    fun addGroup(name: String): NoteGroup {
        val group = NoteGroup(
            id = nextId++,
            name = name
        )
        groups.add(group)
        return group
    }

    fun deleteGroup(id: Int) {
        groups.removeAll { it.id == id }
        // 同时移除该分组下所有笔记的分组关联
        NoteManager.getAllNotes().forEach { note ->
            if (note.groupId == id) {
                NoteManager.updateNoteGroup(note.id, null)
            }
        }
    }

    fun getGroupById(id: Int): NoteGroup? {
        return groups.find { it.id == id }
    }
}
