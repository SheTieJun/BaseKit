package koog.dev.toolkit.prompt

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.dsl.toolCall
import ai.koog.prompt.dsl.toolResult

object PromptTemplates {
    fun promptWithToolTrace() = prompt("tool-trace") {
        system("你是一个带工具的助手。")
        user("请计算 5+3。")
        toolCall(
            id = "calculator_tool_id",
            tool = "calculator",
            args = """{"operation":"add","a":5,"b":3}"""
        )
        toolResult(
            id = "calculator_tool_id",
            tool = "calculator",
            output = "8"
        )
    }
}

