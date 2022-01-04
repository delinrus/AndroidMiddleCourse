package ru.skillbranch.skillarticles.viewmodels.auth

interface IAuthViewModel {
    fun navigateToPrivacy()
    fun navigateToRegistration()
    fun handleLogin(login: String, password: String)
    fun handleRegistration(name: String, login: String, password: String)
    fun resetErrors()
}