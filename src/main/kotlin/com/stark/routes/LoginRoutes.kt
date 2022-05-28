package com.stark.routes

import com.stark.data.authenticate
import com.stark.data.model.User
import com.stark.data.request.UserAuthRequest
import com.stark.data.response.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.loginRoute() {
        post("/login") {
            val request = try {
                call.receive<UserAuthRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val isCredentialsMatched = authenticate(User(email = request.email, password = request.password))
            if (isCredentialsMatched) {
                call.respond(HttpStatusCode.OK, Response(true, "Logged In Successfully"))
            } else {
                call.respond(HttpStatusCode.Unauthorized, Response(false, "Invalid Email or Password"))
            }
        }
}