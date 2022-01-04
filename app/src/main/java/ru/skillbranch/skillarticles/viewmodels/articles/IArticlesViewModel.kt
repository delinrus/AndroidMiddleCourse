package ru.skillbranch.skillarticles.viewmodels.articles


interface IArticlesViewModel {
    fun navigateToArticle(articleItem: ArticleItem)
    fun checkBookmark(articleItem: ArticleItem, checked: Boolean)
}