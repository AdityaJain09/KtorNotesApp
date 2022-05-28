package com.stark.routes

import com.stark.data.*
import com.stark.data.model.Note
import com.stark.data.request.AddOwnerRequest
import com.stark.data.request.DeleteNoteRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.noteRoutes() {
    route("/note") {

        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                val notes = getNotes(email)
                call.respond(HttpStatusCode.OK, notes)
            }
        }
    }

    route("/notes/new") {
        authenticate {
            post {
                val note = try {
                    call.receive<Note>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (createOrUpdateNote(note)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Conflict)
                }
            }
        }
    }

    route("/delete") {
        authenticate {
            delete {
                val email = call.principal<UserIdPrincipal>()!!.name
                val req = try {
                    call.receive<DeleteNoteRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                if (deleteNote(email, req.id)) {
                    call.respond(HttpStatusCode.OK, "Note Deleted Successfully.")
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }

    route("/addOwner") {
        authenticate {
            post {
                val req = try {
                    call.receive<AddOwnerRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                if (!checkIfUserExist(req.ownerId)) {
                    call.respond(HttpStatusCode.NotFound,
                    "No User Found with this email")
                    return@post
                }

                if (isOwnerOfNote(req.id, req.ownerId)) {
                    call.respond(HttpStatusCode.OK,
                    "You are already an owner of this note")
                    return@post
                }
                if (addOwnerToNote(req.ownerId, req.id)) {
                    call.respond(HttpStatusCode.OK, "You are now also the owner of this note")
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}