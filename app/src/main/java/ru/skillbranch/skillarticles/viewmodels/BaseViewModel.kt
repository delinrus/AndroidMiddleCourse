package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T>(initState: T) : ViewModel() {
    protected val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    //not null current state
    protected val currentstate
        get() = state.value!!
}