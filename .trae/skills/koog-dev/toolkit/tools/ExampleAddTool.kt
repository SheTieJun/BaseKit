package koog.dev.toolkit.tools

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.serialization.typeToken
import kotlinx.serialization.Serializable

@Serializable
data class AddArgs(
    val a: Int,
    val b: Int
)

object ExampleAddTool : SimpleTool<AddArgs>(
    argsType = typeToken<AddArgs>(),
    name = "add",
    description = "计算两个整数的和。输入：a,b；输出：a+b。"
) {
    override suspend fun execute(args: AddArgs): String {
        return (args.a + args.b).toString()
    }
}

