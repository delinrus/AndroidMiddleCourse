package ru.skillbranch.skillarticles.viewmodels

import androidx.annotation.UiThread
import androidx.lifecycle.*

abstract class BaseViewModel<T>(initState: T) : ViewModel() {
    protected val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    //not null current state
    protected val currentState
        get() = state.value!!

    /***
     * лямбда выражение принимает в качестве аргумента лямбду в которую передается текущее состояние
     * и она возвращает модифицированное состояние, которое присваивается текущему состоянию
     */
    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updateState: T = update(currentState)
        state.value = updateState
    }

    /***
     * более компактная форма записи observe принимает последним аргументом лямбда выражение обрабатывающее
     * изменение текущего состояния
     */
    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    /***
     * функция принимает источник даных и лямбда выажение обрабатывающее поступающие данные
     * лямбда принимает новые данные и текущее состояние, изменяет его и возвращает
     * модифицированное состояние устанавливается как текущее
     */
    protected fun<S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newValue : S, currentState:T) -> T?
    ) {
        state.addSource(source){
            state.value = onChanged(it, currentState) ?: return@addSource
        }
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