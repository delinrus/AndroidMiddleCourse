package ru.skillbranch.skillarticles.viewmodels.auth

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavOptions
import ru.skillbranch.skillarticles.MainFlowDirections
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.AuthRepository
import ru.skillbranch.skillarticles.ui.auth.AuthFragmentDirections
import ru.skillbranch.skillarticles.viewmodels.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.NavCommand
import ru.skillbranch.skillarticles.viewmodels.RootViewModel
import ru.skillbranch.skillarticles.viewmodels.VMState

class AuthViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<AuthState>(AuthState(), savedStateHandle) {
    private val intentDestination: Int? = savedStateHandle["intent_destination"]

    init {
        Log.e("AuthViewModel", "init viewmodel ${this::class.simpleName} ${this.hashCode()}")
    }

    fun navigateToPrivacy() {
        val options = NavOptions.Builder()
            .setEnterAnim(R.animator.nav_default_enter_anim)
            .setExitAnim(R.animator.nav_default_exit_anim)
            .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
            .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
        navigate(NavCommand.Builder(R.id.page_privacy, null, options.build()))
    }

    fun navigateToRegistration() {
        val action = AuthFragmentDirections.actionAuthFragmentToRegistrationFragment()
        navigate(NavCommand.Action(action))
    }

    fun handleLogin(login: String, password: String) {
        repository.login(login, password)
        navigate(NavCommand.Action(MainFlowDirections.finishLogin()))
        intentDestination?.let {
            if (it != -1 && RootViewModel.privateDestinations.contains(it)) {
                val options = NavOptions.Builder()
                    .setEnterAnim(R.animator.nav_default_enter_anim)
                    .setExitAnim(R.animator.nav_default_exit_anim)
                    .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
                navigate(NavCommand.Builder(it, options = options.build()))
            }
        }
    }


    private val repository: AuthRepository = AuthRepository()
}

data class AuthState(val inputErrors: Map<String, String> = emptyMap()) : VMState