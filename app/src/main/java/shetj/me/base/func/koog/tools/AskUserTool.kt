package shetj.me.base.func.koog.tools

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.serialization.typeToken
import kotlinx.serialization.Serializable
import shetj.me.base.func.koog.askuser.AskUserGateway

@Serializable
data class AskUserArgs(
    @property:LLMDescription("向用户展示的问题")
    val question: String,
    @property:LLMDescription("可选项列表；为空表示自由输入")
    val options: List<String> = emptyList()
)

class AskUserTool(
    private val gateway: AskUserGateway
) : SimpleTool<AskUserArgs>(
    argsType = typeToken<AskUserArgs>(),
    name = "AskUser",
    description = "当信息不足时必须调用该工具向用户追问；不得自行猜测。工具会等待用户选择或输入后返回结果。"
) {

    override suspend fun execute(args: AskUserArgs): String {
        val question = args.question.trim()
        if (question.isEmpty()) return ""
        return gateway.ask(question = question, options = args.options)
    }
}

