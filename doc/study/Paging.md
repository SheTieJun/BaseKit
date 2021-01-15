1. 构建数据来源
   1. 使用room
   2. 其他
2. 构造 Pager 和 PagingData
   ```
         Pager(PagingConfig(
                // 每页显示的数据的大小。对应 PagingSource 里 LoadParams.loadSize
                pageSize = 20,

                // 预刷新的距离，距离最后一个 item 多远时加载数据
                prefetchDistance = 3,

                // 初始化加载数量，默认为 pageSize * 2
                initialLoadSize = 40,

                // 一次应在内存中保存的最大数据
                maxSize = 200
        )
        ) {
            saverDB.searchSaver()
        }
   ```
3. 构造自定义 PagingDataAdapter
4. 关联数据
```
    launch {
                get<Pager<Int, Saver>>().flow.collectLatest {
                    main {
                        adapter.submitData(it)
                    }
                }
            }
```
