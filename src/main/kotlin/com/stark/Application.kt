package com.stark

import com.google.gson.Gson
import com.stark.data.createUser
import com.stark.data.model.User
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.stark.plugins.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main() {
    embeddedServer(Netty, port = 8081, host = "127.0.0.1") {
        installModules()
        configureLogging()
        configureRouting()
        configureSerialization()
        configureSecurity()
    }.start(wait = true)
}

private fun Application.installModules() {
    install(DefaultHeaders)
}
