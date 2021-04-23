package ru.skillbranch.skillarticles.viewmodels

import ru.skillbranch.skillarticles.data.repositories.ArticleRepository

class ArticleViewModel(articleId: String) : BaseViewModel<ArticleState>(ArticleState()) {
    private val repository = ArticleRepository

    init {
        //TODO subs
    }

    fun handleNightMode() {
        //TODO implement me
    }

    fun handleUpText() {
        //TODO implement me
    }

    fun handleDownText() {
        //TODO implement me
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