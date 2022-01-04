package ru.skillbranch.skillarticles.viewmodels

import androidx.annotation.IdRes

interface IRootViewModel {
    fun topLevelNavigate(@IdRes resId: Int);
}