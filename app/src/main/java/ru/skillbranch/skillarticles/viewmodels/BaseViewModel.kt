package ru.skillbranch.skillarticles.viewmodels

import androidx.annotation.UiThread
import androidx.lifecycle.*

abstract class BaseViewModel<T>(initState: T) : ViewModel() {
    protected val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    //not null current state
    protected val currentstate
        get() = state.value!!

    /***
     * лямбда выражение принимает в качестве аргумента лямбду в которую передается текущее состояние
     * и она возвращает модифицированное состояние, которое присваивается текущему состоянию
     */
    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updateState: T = update(currentstate)
        state.value = updateState
    }

    /***
     *более компактная форма записи observe принимает последним аргументом лямбда выражение обрабатывающее
     *изменение текущего состояния
     */
    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }
}

class ViewModelFactory(private val params: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(params) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}