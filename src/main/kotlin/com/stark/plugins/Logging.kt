package com.stark.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.netty.handler.logging.LogLevel
import org.slf4j.event.Level


fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
    }
}