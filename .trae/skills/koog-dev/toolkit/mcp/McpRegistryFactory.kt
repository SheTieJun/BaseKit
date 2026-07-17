package koog.dev.toolkit.mcp

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.mcp.McpServerInfo
import ai.koog.agents.mcp.McpToolRegistryProvider

object McpRegistryFactory {
    fun fromSse(
        serverUrl: String,
        name: String = "koog-client",
        version: String = "1.0.0"
    ): ToolRegistry {
        val transport = McpToolRegistryProvider.defaultSseTransport(serverUrl)
        return McpToolRegistryProvider.fromTransport(
            transport = transport,
            serverInfo = McpServerInfo(url = serverUrl),
            name = name,
            version = version
        )
    }

    fun fromStdioProcess(
        command: List<String>,
        serverInfoUrl: String,
        name: String = "koog-client",
        version: String = "1.0.0"
    ): ToolRegistry {
        val process = ProcessBuilder(command).start()
        val transport = McpToolRegistryProvider.defaultStdioTransport(process)
        return McpToolRegistryProvider.fromTransport(
            transport = transport,
            serverInfo = McpServerInfo(url = serverInfoUrl, command = command.firstOrNull()),
            name = name,
            version = version
        )
    }
}

