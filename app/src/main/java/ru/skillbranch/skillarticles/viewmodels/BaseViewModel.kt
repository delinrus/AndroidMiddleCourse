package ru.skillbranch.skillarticles.viewmodels

import androidx.annotation.UiThread
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T>(initState: T) : ViewModel() {
    protected val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    //not null current state
    protected val currentstate
        get() = state.value!!

    //лямбда выражение принимает в качестве аргумента лямбду в которую передается текущее состояние
    // и она возвращает модифицированное состояние, которое присваивается текущему состоянию
    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updateState: T = update(currentstate)
        state.value = updateState
    }
}