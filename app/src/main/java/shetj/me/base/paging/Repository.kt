/*
 * MIT License
 *
 * Copyright (c) 2021 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package shetj.me.base.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow
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
    private val testUrl = "https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json"

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataBean> {
        return try {
            val page = params.key ?: 1 // set page 1 as default
            val pageSize = params.loadSize
            val repoResponse =  KCHttpV3.get<ResultMusic>(testUrl){
                this.cacheKey = "testUrl"
                this.cacheTime = 10
                this.cacheMode = CacheMode.ONLY_NET
                this.repeatNum = 10
                this.timeout = 5000L
            }
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
