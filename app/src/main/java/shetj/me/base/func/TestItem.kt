package shetj.me.base.func

/**
 * 用于 SelectTracker 功能测试的数据类
 * @param id 唯一标识符，用于 SelectionTracker
 * @param title 标题
 * @param description 描述信息
 * @param type 项目类型，用于视觉区分
 */
data class TestItem(
    val id: Long,
    val title: String,
    val description: String,
    val type: ItemType
) {
    /**
     * 项目类型枚举
     */
    enum class ItemType(val displayName: String, val colorRes: Int) {
        NORMAL("普通", android.R.color.holo_blue_light),
        IMPORTANT("重要", android.R.color.holo_orange_light),
        URGENT("紧急", android.R.color.holo_red_light),
        COMPLETED("已完成", android.R.color.holo_green_light)
    }

    companion object {
        /**
         * 创建测试数据
         */
        fun createTestData(): List<TestItem> {
            return listOf(
                TestItem(0L, "测试项目 1", "这是第一个测试项目，用于验证基本选择功能", ItemType.NORMAL),
                TestItem(1L, "测试项目 1", "这是第一个测试项目，用于验证基本选择功能", ItemType.NORMAL),
                TestItem(2L, "重要任务", "这是一个重要的任务项目，需要特别关注", ItemType.IMPORTANT),
                TestItem(3L, "紧急处理", "紧急需要处理的项目，优先级最高", ItemType.URGENT),
                TestItem(4L, "已完成项目", "这个项目已经完成，可以作为参考", ItemType.COMPLETED),
                TestItem(5L, "测试项目 2", "第二个普通测试项目", ItemType.NORMAL),
                TestItem(6L, "会议安排", "重要的会议安排，不能错过", ItemType.IMPORTANT),
                TestItem(7L, "Bug 修复", "紧急需要修复的 Bug", ItemType.URGENT),
                TestItem(8L, "文档编写", "已完成的文档编写任务", ItemType.COMPLETED),
                TestItem(9L, "测试项目 3", "第三个测试项目，用于多选测试", ItemType.NORMAL),
                TestItem(10L, "代码审查", "重要的代码审查任务", ItemType.IMPORTANT),
                TestItem(11L, "系统维护", "紧急的系统维护工作", ItemType.URGENT),
                TestItem(12L, "培训完成", "已完成的培训任务", ItemType.COMPLETED),
                TestItem(13L, "测试项目 4", "第四个测试项目", ItemType.NORMAL),
                TestItem(14L, "项目评估", "重要的项目评估工作", ItemType.IMPORTANT),
                TestItem(15L, "安全检查", "紧急的安全检查任务", ItemType.URGENT)
            )
        }
    }
}