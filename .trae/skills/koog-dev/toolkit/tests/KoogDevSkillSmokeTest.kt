package koog.dev.toolkit.tests

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.serialization.typeToken
import kotlinx.serialization.Serializable

object KoogDevSkillSmokeTest {

    @Serializable
    data class AddArgs(
        val a: Int,
        val b: Int
    )

    object AddTool : SimpleTool<AddArgs>(
        argsType = typeToken<AddArgs>(),
        name = "add",
        description = "计算两个整数的和"
    ) {
        override suspend fun execute(args: AddArgs): String {
            return (args.a + args.b).toString()
        }
    }

    fun buildPrompt() = prompt("smoke") {
        system("你是一个助手。")
        user("你好")
        toolCall(
            id = "t1",
            tool = "calculator",
            args = """{"operation":"add","a":5,"b":3}"""
        )
        toolResult(
            id = "t1",
            tool = "calculator",
            output = "8"
        )
    }

    fun buildRegistry(): ToolRegistry {
        return ToolRegistry {
            tools(AddTool)
        }
    }
}

