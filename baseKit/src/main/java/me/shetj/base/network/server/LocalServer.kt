package me.shetj.base.network.server

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticFiles
import java.io.File
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
     * @param localDirPath 需要对外提供访问的本地文件夹绝对路径
     */
    fun start(port: Int = 8080, localDirPath: String? = null) {
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
                    routing {
                        // 简单的状态检查接口
                        get("/ping") {
                            call.respond(mapOf("status" to "ok", "message" to "pong"))
                        }

                        // 如果传入了本地文件夹路径，则配置静态文件服务
                        if (!localDirPath.isNullOrEmpty()) {
                            val localDir = File(localDirPath)
                            if (localDir.exists() && localDir.isDirectory) {
                                // 将根路径 "/" 映射到传入的本地文件夹
                                // 访问 http://localhost:8080/filename 即可下载该文件夹下的文件
                                staticFiles("/", localDir) {
                                    // 默认返回 index.html（如果存在）
                                    default("index.html")
                                }
                                "Serving static files from: ${localDir.absolutePath}".logI("LocalServer")
                            } else {
                                "Warning: Local directory '$localDirPath' does not exist or is not a directory".logI("LocalServer")
                            }
                        } else {
                            // 默认路由
                            get("/") {
                                call.respondText("Hello from Ktor Local Server running on Android!")
                            }
                        }
                    }
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
}
