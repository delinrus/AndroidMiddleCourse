package ru.skillbranch.skillarticles.viewmodels.auth

import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.data.repositories.AuthRepository
import ru.skillbranch.skillarticles.ui.auth.AuthFragmentDirections
import ru.skillbranch.skillarticles.viewmodels.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.NavCommand
import ru.skillbranch.skillarticles.viewmodels.VMState

class AuthViewModel(savedStateHandle: SavedStateHandle) : BaseViewModel<AuthState>(AuthState(), savedStateHandle) {
    fun navigateToPrivacy() {
        val action = AuthFragmentDirections.actionAuthFragmentToPrivacyPolicyFragment()
        navigate(NavCommand.Action(action))
    }

    fun navigateToRegistration() {
        val action = AuthFragmentDirections.actionAuthFragmentToRegistrationFragment()
        navigate(NavCommand.Action(action))
    }

    fun handleLogin(login: String, password: String) {
        repository.login(login, password)
        //TODO navigate
    }


    private val repository: AuthRepository = AuthRepository()
}

data class AuthState(val inputErrors: Map<String, String> = emptyMap()) : VMState