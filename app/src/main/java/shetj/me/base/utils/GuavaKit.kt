package shetj.me.base.utils

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.Weigher
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
        //基于容量回收、定时回收和基于引用回收。
        val fileCache = CacheBuilder.newBuilder()
            .maximumSize(1000)//基于容量回收
            .maximumWeight(10000)//基于容量回收
            .weigher(Weigher<String, String> { key, value -> value.length })
            .expireAfterWrite(10, MINUTES)//定时回收
            .build(object : CacheLoader<String, String>() {
                override fun load(key: String): String {
                    return File(key).readText(StandardCharsets.UTF_8)
                }
            })

        /*
        * expireAfterAccess(long, TimeUnit)：缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。
        * expireAfterWrite(long, TimeUnit)：缓存项在给定时间内没有被写访问（创建或覆盖），则回收。如果认为缓存数据总是在固定时候后变得陈旧不可用，这种回收方式是可取的。
        *
        * 通过使用弱引用的键、或弱引用的值、或软引用的值，Guava Cache可以把缓存设置为允许垃圾回收：
        * CacheBuilder.weakKeys()：使用弱引用存储键。当键没有其它（强或软）引用时，缓存项可以被垃圾回收。因为垃圾回收仅依赖恒等式（==），使用弱引用键的缓存用==而不是equals比较键。
        * CacheBuilder.weakValues()：使用弱引用存储值。当值没有其它（强或软）引用时，缓存项可以被垃圾回收。因为垃圾回收仅依赖恒等式（==），使用弱引用值的缓存用==而不是equals比较值。
        * CacheBuilder.softValues()：使用软引用存储值。软引用只有在响应内存需要时，才按照全局最近最少使用的顺序回收。考虑到使用软引用的性能影响，我们通常建议使用更有性能预测性的缓存大小限定（见上文，基于容量回收）。使用软引用值的缓存同样用==而不是equals比较值。
        * 个别清除：Cache.invalidate(key)
        * 批量清除：Cache.invalidateAll(keys)
        * 清除所有缓存项：Cache.invalidateAll()
         */

        try {
            val content = fileCache["/path/to/file"]

            val content2 = fileCache.get("/path/to/file") {
                File("/path/to/file").readText(StandardCharsets.UTF_8)
            }
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
    fun immutablemap() {
        // 使用of方法创建一个小的ImmutableMap
        val map = ImmutableMap.of("key1", 1, "key2", 2, "key3", 3)
        // 使用builder方法创建一个大的ImmutableMap
        val largeMap: ImmutableMap<String, Int> = ImmutableMap.builder<String, Int>()
            .put("key1", 1)
            .put("key2", 2)
            .build()
    }

    fun funcAndP() {
        val intList: List<Int> = Lists.newArrayList(1, 2, 3, 4, 5)
        val predicate: Predicate<Int> = Predicate<Int> { t -> t > 3 }
        val filteredList = intList.stream().filter(predicate::test).collect(Collectors.toList()) // [4, 5]

    }
}