package ru.skillbranch.skillarticles

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import ru.skillbranch.skillarticles.data.network.res.CommentRes
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository

class TestViewModel() : ViewModel() {
    private val repository = ArticleRepository()
    val commentPager: LiveData<PagingData<CommentRes>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            prefetchDistance = 3*20 //default
            //initialLoadSize = 4*20 //default
        ),
        pagingSourceFactory = {
            repository.makeCommentDataSource("0")
        }
    )
        .liveData
        .cachedIn(viewModelScope)
}