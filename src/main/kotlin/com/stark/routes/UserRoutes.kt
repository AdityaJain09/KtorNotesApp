package com.stark.routes

import com.stark.data.checkIfUserExist
import com.stark.data.createUser
import com.stark.data.model.User
import com.stark.data.request.UserAuthRequest
import com.stark.data.response.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute() {
    route("/user") {
        post("/register") {
            val request = try {
                call.receive<UserAuthRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            if (!checkIfUserExist(request.email)) {
                if (createUser(User(request.email, request.password))) {
                    call.respond(HttpStatusCode.OK, Response(true, "User Created Successfully"))
                } else {
                    call.respond(HttpStatusCode.OK, Response(false, "Failed to create user"))
                }
            } else {
                call.respond(HttpStatusCode.OK, Response(false, "Email Already Exist"))
            }
        }
    }
}