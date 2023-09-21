package shetj.me.base.utils

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ListMultimap
import com.google.common.collect.Lists
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit.MINUTES
import java.util.function.Predicate
import java.util.stream.Collectors


/**
 *
 * Guava 测试
 */
@Suppress("unused")
class GuavaKit {

    fun cache() {
        val fileCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, MINUTES)
            .build(object : CacheLoader<String, String>() {
                override fun load(key: String): String {
                    return File(key).readText(StandardCharsets.UTF_8)
                }
            })

        try {
            val content = fileCache["/path/to/file"]
        } catch (e: ExecutionException) {
            // 处理异常...
        }
    }


    //Multimaps.synchronizedListMultimap() 方法来获取一个线程安全的视图。
    fun collections() {
        val multimap: ListMultimap<String, Int> = ArrayListMultimap.create()
        // 添加键值对
        multimap.put("key", 1)
        multimap.put("key", 2)
        multimap.put("key", 3)
        // 获取键对应的所有值
        val values = multimap["key"] // 返回 [1, 2, 3]
        // 删除键对应的某个值
        multimap.remove("key", 2) // values 现在是 [1, 3]
        // 删除键对应的所有值
        multimap.removeAll("key") // values 现在是 []


        val multimapKt = mutableMapOf<String, MutableList<Int>>()

        multimapKt.getOrPut("key") { mutableListOf() }.add(1)
        multimapKt.getOrPut("key") { mutableListOf() }.add(2)

        println(multimapKt["key"])  // 输出：[1, 2]

    }

    //ImmutableMap 保证了元素的插入顺序。也就是说，当你遍历 Map 时，元素的顺序是按照你插入的顺序。
    fun immutablemap(){
        // 使用of方法创建一个小的ImmutableMap
        val map = ImmutableMap.of("key1", 1, "key2", 2, "key3", 3)
        // 使用builder方法创建一个大的ImmutableMap
        val largeMap: ImmutableMap<String, Int> = ImmutableMap.builder<String, Int>()
            .put("key1", 1)
            .put("key2", 2)
            .build()
    }

    fun funcAndP(){
        val intList: List<Int> = Lists.newArrayList(1, 2, 3, 4, 5)
        val predicate: Predicate<Int> = Predicate<Int> { t -> t > 3 }
        val filteredList = intList.stream().filter(predicate::test).collect(Collectors.toList()) // [4, 5]

    }
}