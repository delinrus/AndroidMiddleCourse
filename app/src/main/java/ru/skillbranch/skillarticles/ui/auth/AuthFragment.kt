package ru.skillbranch.skillarticles.ui.auth

import android.text.Spannable
import androidx.core.text.set
import androidx.fragment.app.viewModels
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.FragmentAuthBinding
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.ui.BaseFragment
import ru.skillbranch.skillarticles.ui.custom.spans.UnderlineSpan
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.auth.AuthState
import ru.skillbranch.skillarticles.viewmodels.auth.AuthViewModel

class AuthFragment() : BaseFragment<AuthState, AuthViewModel, FragmentAuthBinding>(R.layout.fragment_auth) {
    override val viewModel: AuthViewModel by viewModels()
    override val viewBinding: FragmentAuthBinding by viewBinding(FragmentAuthBinding::bind)

    override fun renderUi(data: AuthState) {
        val decorColor = requireContext().attrValue(R.attr.colorPrimary)
        with(viewBinding){
            tvPrivacy.setOnClickListener { viewModel.navigateToPrivacy() }
            tvRegister.setOnClickListener { viewModel.navigateToRegistration() }
            btnLogin.setOnClickListener { viewModel.handleLogin(
                etLogin.text.toString(),
                etPassword.text.toString()
            ) }

            (tvPrivacy.text as Spannable).let { it[0..it.length] = UnderlineSpan(decorColor) }
            (tvRegister.text as Spannable).let { it[0..it.length] = UnderlineSpan(decorColor) }
        }
    }

    override fun setupViews() {
        //handle input errors this (show error on text input layout)
    }
}