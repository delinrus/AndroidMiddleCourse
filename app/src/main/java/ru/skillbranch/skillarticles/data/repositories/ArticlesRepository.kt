package ru.skillbranch.skillarticles.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.*
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.extensions.toArticleItem
import ru.skillbranch.skillarticles.viewmodels.articles.ArticleItem

class ArticlesRepository(
    private val local: LocalDataHolder = LocalDataHolder,
    private val network: NetworkDataHolder = NetworkDataHolder
) {
    fun findArticles(): LiveData<List<ArticleItem>> = local.findArticles()
    fun makeArticleDataStore() = ArticlesDataSource(local = local)

    @ExperimentalPagingApi
    fun makeArticlesMediator() = ArticlesMediator(network = network, local = local)
}


@ExperimentalPagingApi
class ArticlesMediator(
    val network: NetworkDataHolder,
    val local: LocalDataHolder
) : RemoteMediator<Int, ArticleItem>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleItem>
    ): MediatorResult {

        return try {
            when (loadType) {
                LoadType.REFRESH -> {

                    //initial load
                    val articles = network.loadArticles(null, state.config.pageSize)
                    local.insertArticles(articles.map { it.toArticleItem() })
                    Log.e(
                        "ArticlesRepository",
                        "initial load from network ${articles.size}"
                    )
                    MediatorResult.Success(endOfPaginationReached = false)
                }
                LoadType.PREPEND -> {
                    MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {

                    val lastItem = state.lastItemOrNull()
                    val articles =
                        network.loadArticles(lastItem?.id?.toInt()?.inc(), state.config.pageSize)
                    local.insertArticles(articles.map { it.toArticleItem() })
                    Log.e(
                        "ArticlesRepository",
                        "APPEND load from network ${articles.size}"
                    )
                    MediatorResult.Success(endOfPaginationReached = articles.isEmpty())
                }
            }
        } catch (t: Throwable) {
            MediatorResult.Error(t)
        }
    }
}

class ArticlesDataSource(val local: LocalDataHolder) : PagingSource<Int, ArticleItem>() {

    init {
        local.attachDataSource(this)
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleItem>): Int? {
        val anchorPosition = state.anchorPosition
            ?: return null //visible viewHolder position or null in initial load
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null //loaded page
        val size = state.config.pageSize

        val nextKey = anchorPage.nextKey
        val prevKey = anchorPage.prevKey
        val pageKey = prevKey?.plus(size) ?: nextKey?.minus(size) //if prev == null -> initial load

        Log.w(
            "GET_REFRESH_KEY",
            "anchorPosition$anchorPosition, offset:$pageKey prev:$prevKey, next$nextKey"
        )
        return pageKey
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleItem> {
        val pageKey = params.key ?: 0 //offset
        val pageSize = params.loadSize  //limit

        return try {

            val articles = local.loadArticles(pageKey, pageSize)
            val prevKey = if (pageKey > 0) pageKey.minus(pageSize) else null
            val nextKey = if (articles.isNotEmpty()) pageKey.plus(pageSize) else null

            Log.w(
                "LOAD",
                "load from local articles:${articles.size} offset:$pageKey limit:$pageSize prev:$prevKey next:$nextKey"
            )
            LoadResult.Page(
                data = articles,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (t: Throwable) {
            Log.e("ArticleRepository", "ERROR ${t.message}")
            LoadResult.Error(t)
        }
    }

}