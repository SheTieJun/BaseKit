package shetj.me.base.func.koog.tools

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.serialization.typeToken

class InspirationTool : SimpleTool<String>(
    argsType = typeToken<String>(),
    name = NAME,
    description = "当用户需要创作灵感时调用。输入：小说关键词（如“赛博朋克+修仙”）；输出：一个具有冲突感的剧情切入点。"
) {
    companion object {
        const val NAME = "inspiration"
    }

    override suspend fun execute(args: String): String {
        val topic = args.trim()
        if (topic.isEmpty()) return "请输入小说关键词"
        return buildString {
            append("冲突切入点：")
            append("当「")
            append(topic)
            append("」的世界规则被公开验证为假的那一刻，主角必须在24小时内决定：揭穿真相引发秩序崩塌，还是成为谎言的新维护者。")
        }
    }
}
