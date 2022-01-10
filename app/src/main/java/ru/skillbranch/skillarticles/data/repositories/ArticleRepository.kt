package ru.skillbranch.skillarticles.data.repositories

import android.util.Log
import androidx.core.math.MathUtils.clamp
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.skillbranch.skillarticles.data.*
import ru.skillbranch.skillarticles.data.network.res.CommentRes

interface IArticleRepository {
    fun loadArticleContent(articleId: String): LiveData<List<MarkdownElement>?>
    fun getArticle(articleId: String): LiveData<ArticleData?>
    fun loadArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?>
    fun getAppSettings(): LiveData<AppSettings>
    fun updateSettings(appSettings: AppSettings)
    fun updateArticlePersonalInfo(info: ArticlePersonalInfo)
    fun makeCommentDataSource(articleId: String): CommentsDataSource
}

class ArticleRepository(
    private val local: LocalDataHolder = LocalDataHolder,
    private val network: NetworkDataHolder = NetworkDataHolder,
    private val prefs: PrefManager = PrefManager()
) : IArticleRepository {

    override fun loadArticleContent(articleId: String): LiveData<List<MarkdownElement>?> {
        return network.loadArticleContent(articleId) //5s delay from network
            .map { str -> str?.let { MarkdownParser.parse(it) } } //Transformation.map extension for LiveData
    }

    override fun getArticle(articleId: String): LiveData<ArticleData?> {
        return local.findArticle(articleId) //2s delay from db
    }

    override fun loadArticlePersonalInfo(articleId: String): LiveData<ArticlePersonalInfo?> {
        return local.findArticlePersonalInfo(articleId) //1s delay from db
    }

    override fun getAppSettings(): LiveData<AppSettings> = prefs.settings //from preferences

    override fun updateSettings(appSettings: AppSettings) {
        prefs.isBigText = appSettings.isBigText
        prefs.isDarkMode = appSettings.isDarkMode
    }

    override fun updateArticlePersonalInfo(info: ArticlePersonalInfo) {
        local.updateArticlePersonalInfo(info)
    }

    override fun makeCommentDataSource(articleId: String) = CommentsDataSource(articleId, network)
}

class CommentsDataSource(
    val articleId: String,
    val network: NetworkDataHolder,
) : PagingSource<Int, CommentRes>() {

    override val jumpingSupported = true //default

    override fun getRefreshKey(state: PagingState<Int, CommentRes>): Int? {
        val anchorPosition = state.anchorPosition
            ?: return null //visible viewHolder position or null in initial load
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null //loaded page
        val size = state.config.pageSize

        val nextKey = anchorPage.nextKey
        val prevKey = anchorPage.prevKey
        val pageKey = prevKey?.plus(size) ?: nextKey?.minus(size) //if prev ==null -> initial load

        Log.w(
            "GET_REFRESH_KEY",
            "anchorPosition$pageKey, offset:$pageKey prev:$prevKey, next$nextKey"
        )
        return pageKey
    }

    //for placeholders
/*    override fun getRefreshKey(state: PagingState<Int, CommentRes>): Int? {
        //if placeholders
        val anchorPosition = state.anchorPosition
            ?: return null //visible viewHolder position or null in initial load

        val pageKey = clamp(anchorPosition, 0, totalComments)

        Log.w(
            "GET_REFRESH_KEY",
            "offset:$pageKey"
        )
        return pageKey
    }*/

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentRes> {
        val pageKey = params.key ?: 0 //offset
        val pageSize = params.loadSize  //limit

        return try {
            val comments = network.loadComments(articleId, pageKey, pageSize)
            val prevKey = if (pageKey>0) pageKey.minus(pageSize) else null
            val nextKey = if (comments.isNotEmpty()) pageKey.plus(pageSize) else null


            Log.e(
                "LOAD",
                "load from network comments:${comments.size} offset:$pageKey limit:$pageSize prev:$prevKey next:$nextKey"
            )
            LoadResult.Page(
                data = comments,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }
}