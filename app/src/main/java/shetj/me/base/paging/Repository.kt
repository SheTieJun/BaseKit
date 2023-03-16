package shetj.me.base.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow
import me.shetj.base.ktx.logI
import me.shetj.base.network_coroutine.KCHttpV3
import me.shetj.base.network_coroutine.cache.CacheMode
import me.shetj.base.network_coroutine.getOrThrow
import shetj.me.base.bean.ResultMusic
import shetj.me.base.bean.ResultMusic.DataBean

object Repository {

    private const val PAGE_SIZE = 50


    fun getPagingData(): Flow<PagingData<DataBean>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { RepoPagingSource() }
        ).flow
    }

}


class RepoPagingSource() : PagingSource<Int, DataBean>() {
    private val testUrl = "https://"

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataBean> {
        return try {
            val page = params.key ?: 1 // set page 1 as default
            val pageSize = params.loadSize
            val repoResponse =  KCHttpV3.get<ResultMusic>(testUrl){
                this.cacheKey = "testUrl"
                this.cacheTime = 10
                this.cacheMode = CacheMode.CACHE_NET_DISTINCT
                this.repeatNum = 10
                this.timeout = 5000L
            }
            pageSize.toString().logI()
            if (repoResponse.isSuccess){
                val repoItems = repoResponse.getOrThrow().data
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (repoItems.isNotEmpty()) page + 1 else null
                LoadResult.Page(repoItems, prevKey, nextKey)
            }else{
                LoadResult.Error(Exception(repoResponse.exceptionOrNull()))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DataBean>): Int? = null

}
