package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.data.repositories.RootRepository

class RootViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<RootState>(RootState(), savedStateHandle) {
    private val repository: RootRepository = RootRepository()
    //for live data update
    private val selfObserver = Observer<RootState> { state -> /*do nothing*/ }

    init {
        subscribeOnDataSource(repository.isAuth()) { isAuth, currentState ->
            currentState.copy(isAuth = isAuth)
        }
        state.observeForever(selfObserver)
    }

    override fun onCleared() {
        super.onCleared()
        state.removeObserver(selfObserver)
    }
}

data class RootState(val isAuth: Boolean = false) : VMState