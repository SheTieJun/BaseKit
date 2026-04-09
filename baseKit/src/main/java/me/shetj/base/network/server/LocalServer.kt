package me.shetj.base.network.server

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.shetj.base.ktx.logI

/**
 * 简易 Ktor 本地服务器
 * 可用于在 Android 端提供简单的接口服务、调试或数据桩
 */
object LocalServer {

    private var engine: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

    /**
     * 启动本地服务器
     * @param port 端口号，默认 8080
     * @param configureRoutes 路由配置闭包
     */
    fun start(port: Int = 8080, configureRoutes: Application.() -> Unit = { defaultRoutes() }) {
        if (engine != null) {
            "LocalServer is already running on port $port".logI("LocalServer")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                engine = embeddedServer(Netty, port = port) {
                    // 安装内容协商，支持 JSON 自动序列化
                    install(ContentNegotiation) {
                        json(Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        })
                    }

                    // 跨域配置 (CORS)，允许所有主机访问
                    install(CORS) {
                        anyHost()
                    }

                    // 配置路由
                    configureRoutes()
                }.start(wait = false)
                
                "LocalServer started successfully on http://localhost:$port".logI("LocalServer")
            } catch (e: Exception) {
                "Failed to start LocalServer: ${e.message}".logI("LocalServer")
                e.printStackTrace()
            }
        }
    }

    /**
     * 停止本地服务器
     */
    fun stop() {
        engine?.stop(1000, 2000)
        engine = null
        "LocalServer stopped".logI("LocalServer")
    }

    /**
     * 默认的基础路由配置示例
     */
    private fun Application.defaultRoutes() {
        routing {
            // 简单的 GET 接口
            get("/") {
                call.respondText("Hello from Ktor Local Server running on Android!")
            }

            get("/ping") {
                call.respond(mapOf("status" to "ok", "message" to "pong"))
            }

            // 简单的 POST 接口示例
            post("/echo") {
                val receiveText = call.receiveText()
                call.respondText("Server received: $receiveText", status = HttpStatusCode.OK)
            }
        }
    }
}
