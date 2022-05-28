package com.stark.data

import com.stark.data.model.Note
import com.stark.data.model.User
import io.ktor.client.*
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

private val client = KMongo.createClient().coroutine
private val database = client.getDatabase("NotesDb")

private val usersCollection = database.getCollection<User>()
private val notesCollection = database.getCollection<Note>()

// Users Crud
suspend fun createUser(user: User): Boolean = usersCollection.insertOne(user).wasAcknowledged()

suspend fun checkIfUserExist(email: String): Boolean {
    return usersCollection.findOne(User::email eq email) != null
}

suspend fun authenticate(user: User): Boolean {
    return usersCollection.findOne(User::email eq user.email)?.let {
        it.password == user.password
    } ?: false
}

// Notes Crud
suspend fun createOrUpdateNote(note: Note): Boolean {
    val fetchedNote = notesCollection.findOneById(note.id)
    return fetchedNote?.let {
        notesCollection.updateOneById(fetchedNote.id, note).wasAcknowledged()
    } ?: notesCollection.insertOne(note).wasAcknowledged()
}

suspend fun getNotes(email: String): List<Note> {
    return notesCollection.find(Note::owners contains email).toList()
}

suspend fun isOwnerOfNote(noteId: String, ownerId: String): Boolean {
    val note = notesCollection.findOneById(noteId) ?: return false
    println("note inthis list = = == ${note.owners}")
    return ownerId in note.owners
}

suspend fun addOwnerToNote(ownerId: String, noteId: String): Boolean {
    val oldNote = notesCollection.findOne(Note::id eq noteId)
    return oldNote?.let { note ->
        notesCollection.updateOne(
            Note::id eq note.id, setValue(Note::owners, note.owners + ownerId)
        ).wasAcknowledged()
    } ?: false
}

suspend fun deleteNote(email: String, noteId: String): Boolean {
    val oldNote = notesCollection.findOne(Note::id eq noteId, Note::owners contains email )
    return oldNote?.let { note ->
        val isSingleUserOwnedNote = note.owners.size == 1
        if (isSingleUserOwnedNote) {
            return@let notesCollection.deleteOneById(note.id).wasAcknowledged()
        }
        val updatedOwners = note.owners - email
        val updatedNotes = notesCollection.updateOne(Note::id eq note.id, setValue(Note::owners, updatedOwners))
        updatedNotes.wasAcknowledged()
    } ?: false
}