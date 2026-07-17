package shetj.me.base.func.koog

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.serialization.typeToken
import kotlinx.serialization.Serializable
import org.junit.Assert.assertNotNull
import org.junit.Test

class KoogDevSkillSmokeTest {

    @Serializable
    data class AddArgs(
        val a: Int,
        val b: Int
    )

    private object AddTool : SimpleTool<AddArgs>(
        argsType = typeToken<AddArgs>(),
        name = "add",
        description = "计算两个整数的和"
    ) {
        override suspend fun execute(args: AddArgs): String {
            return (args.a + args.b).toString()
        }
    }

    @Test
    fun promptDsl_builds() {
        val p = prompt("smoke") {
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
        assertNotNull(p)
    }

    @Test
    fun toolRegistry_builds() {
        val registry = ToolRegistry {
            tools(AddTool)
        }
        assertNotNull(registry)
    }
}

