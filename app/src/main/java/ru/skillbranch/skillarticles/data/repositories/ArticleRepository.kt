package ru.skillbranch.skillarticles.data.repositories

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
    fun makeCommentDataSource(articleId: String) : CommentsDataSource
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
    val network: NetworkDataHolder
) : PagingSource<Int, CommentRes>(){
    override fun getRefreshKey(state: PagingState<Int, CommentRes>): Int? {
        //TODO implement later
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentRes> {
        val pageKey = params.key //offset
        val pageSize = params.loadSize  //limit

        return try {
            val comments = network.loadComments(articleId, pageKey, pageSize)
            val prevKey = pageKey?.minus(pageSize)
            val nextKey = if(comments.isNotEmpty()) pageKey?.plus(pageSize) else null
            LoadResult.Page(data = comments, prevKey = prevKey, nextKey = nextKey)

        }catch (t:Throwable) {
            LoadResult.Error(t)
        }
    }
}