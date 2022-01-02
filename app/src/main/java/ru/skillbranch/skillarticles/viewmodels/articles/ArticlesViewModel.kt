package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.viewmodels.articles.ArticleItem

class ArticlesViewModel(savedStateHandle: SavedStateHandle) : BaseViewModel<ArticlesState>(ArticlesState(), savedStateHandle) {
    fun navigateToArticle(articleItem: ArticleItem) {
    }

    fun checkBookmark(articleItem: ArticleItem, checked: Boolean) {
    }

    private val repository: ArticlesRepository = ArticlesRepository()
    val articles: LiveData<List<ArticleItem>> = repository.findArticles()
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