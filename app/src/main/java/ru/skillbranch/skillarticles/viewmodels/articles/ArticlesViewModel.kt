package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavDirections
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.ui.articles.ArticlesFragmentDirections
import ru.skillbranch.skillarticles.viewmodels.articles.ArticleItem

class ArticlesViewModel(savedStateHandle: SavedStateHandle) : BaseViewModel<ArticlesState>(ArticlesState(), savedStateHandle) {
    private val repository: ArticlesRepository = ArticlesRepository()
    val articles: LiveData<List<ArticleItem>> = repository.findArticles()

    fun navigateToArticle(articleItem: ArticleItem) {
        val action: NavDirections = articleItem.run {
            ArticlesFragmentDirections.actionNavArticlesToArticleFragment(
                id, author, authorAvatar, category, categoryIcon, poster, title, date
            )
        }

        navigate(NavCommand.Action(action))

    }

    fun checkBookmark(articleItem: ArticleItem, checked: Boolean) {
    }


}

data class ArticlesState(
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val isLoading: Boolean = true,
    val isBookmark: Boolean = false,
    val selectedCategories: List<String> = emptyList(),
    val isHashtagSearch: Boolean = false,
    val tags: List<String> = emptyList(),
): VMState