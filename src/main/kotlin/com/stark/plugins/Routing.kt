package com.stark.plugins

import com.stark.data.checkIfUserExist
import com.stark.data.createUser
import com.stark.data.model.User
import com.stark.data.request.UserAuthRequest
import com.stark.data.response.Response
import com.stark.routes.loginRoute
import com.stark.routes.noteRoutes
import com.stark.routes.userRoute
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.locations.*
import io.ktor.server.application.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {

    install(Routing) {

    }
    install(Locations) {
    }

    routing {
        userRoute()
        loginRoute()
        noteRoutes()

        get<MyLocation> {
                call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
            }
            // Register nested routes
            get<Type.Edit> {
                call.respondText("Inside $it")
            }
            get<Type.List> {
                call.respondText("Inside $it")
            }
    }
}
@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")
@Location("/type/{name}") data class Type(val name: String) {
    @Location("/edit")
    data class Edit(val type: Type)

    @Location("/list/{page}")
    data class List(val type: Type, val page: Int)
}
