package org.kat

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.kat.controllers.ItemController
import java.util.concurrent.TimeUnit

fun Application.module() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
        }
        gson {
            setPrettyPrinting()
        }
    }

    install(DefaultHeaders)

    install(CallLogging)

    routing {

        val controller = ItemController(org.kat.items)

        get("/") {
            call.respond(mapOf("messages" to messages))
        }
        route(path = "api/item") {
            get(path = "/{id}") {
                call.respond(mapOf("item" to controller.getItem(call.parameters["id"]!!.toInt())))
            }
        }
    }
}

class KtorApp(port: Int) {

    private val server = embeddedServer(factory = Netty, port = port, module = Application::module)

    fun start() {
        server.start(wait = true)
    }

    fun stop() {
        server.stop(gracePeriod = 0, timeout = 0, timeUnit = TimeUnit.SECONDS)
    }
}

//class JavalinApp(private val port: Int) {
//
//    private val controller = ItemController(items)
//
//    fun init(): Javalin {
//
//        val app = Javalin.create().apply {
//            port(port)
//            exception(Exception::class.java) { e, _ -> e.printStackTrace() }
//        }.start()
//
//        app.get("/") { ctx -> ctx.result(resultString = "Hello World") }
//
//        app.routes {
//            path("api") {
//                path("item") {
//                    path(":id") {
//                        get { ctx -> controller.getItem(ctx) }
//                    }
//                }
//            }
//        }
//        return app
//    }
//}


fun main(args: Array<String>) {
    //JavalinApp(port = 7000).init()
    KtorApp(port = 8000).start()
}