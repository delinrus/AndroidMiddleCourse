package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.format

class ArticleViewModel(private val articleId: String) : BaseViewModel<ArticleState>(ArticleState()) {
    private val repository = ArticleRepository

    init {
        //subscribe on mutable data
        subscribeOnDataSource(getArticleData()){ article,state->
            article ?: return@subscribeOnDataSource null
            state.copy(
                shareLink = article.shareLink,
                title = article.title,
                category = article.category,
                categoryIcon = article.categoryIcon,
                date = article.date.format()
            )
        }

        subscribeOnDataSource(getArticleContent()){ content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy(
                isLoadingContent = false,
                content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()){ info,state ->
            info ?: return@subscribeOnDataSource null
            state.copy(
                isBookmark = info.isBookmark,
                isLike = info.isLike
            )
        }

        subscribeOnDataSource(repository.getAppSettings()){ settings,state ->
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }
    }

    //load text from network
    private fun getArticleContent(): LiveData<List<Any>?> {
        return repository.loadArticleContent(articleId)
    }

    //load data from db
    private fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    //load data from db
    private fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    //app settings
    fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    //personal article info
    fun handleBookmark() {
        //TODO implement me
    }

    fun handleLike() {
        //TODO implement me
    }

    //not implemented
    fun handleShare() {
        //TODO implement me
    }

    //session state
    fun handleToggleMenu() {
       updateState { it.copy(isShowMenu = !it.isShowMenu) }
    }

    fun handleSearchMode(isSearch: Boolean) {
        //TODO implement me
    }

    fun handleSearc(query: String?) {
        //TODO implement me
    }
}

data class ArticleState(
    val isAuth: Boolean = false, //пользователь авторизован
    val isLoadingContent: Boolean = true, //контент загружается
    val isLoadingReviews: Boolean = true, //отзывы загружаются
    val isLike: Boolean = false, //отмечено как Like
    val isBookmark: Boolean = false, //в закладках
    val isShowMenu: Boolean = false, //отображается меню
    val isBigText: Boolean = false, //шрифт увеличен
    val isDarkMode: Boolean = false, //темный режим
    val isSearch: Boolean = false, //режим поиска
    val searchQuery: String? = null, //поисковый запрос
    val searchResults: List<Pair<Int, Int>> = emptyList(), //результаты поиска (стартовая и конечная позиции)
    val searchPosition: Int = 0, //текущаяя позиция найденного результата
    val shareLink: String? = null, //ссылка Share
    val title: String? = null, //заголовок статьи
    val category: String? = null, //категория
    val categoryIcon: Any? = null, //иконка категории
    val date: String? = null, //дата публикации
    val author: Any? = null, //автор статьи
    val poster: String? = null, //обложка статьи
    val content: List<Any> = emptyList(), //контент
    val reviews: List<Any> = emptyList() //комметарии
)