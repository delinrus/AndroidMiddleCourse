package ru.skillbranch.skillarticles.ui.auth

import android.text.Spannable
import androidx.core.text.set
import androidx.fragment.app.viewModels
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.FragmentRegistrationBinding
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.ui.BaseFragment
import ru.skillbranch.skillarticles.ui.custom.spans.UnderlineSpan
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.auth.RegisterViewModel
import ru.skillbranch.skillarticles.viewmodels.auth.RegistrationState

class RegistrationFragment : BaseFragment<RegistrationState, RegisterViewModel, FragmentRegistrationBinding>(
    R.layout.fragment_registration){
    override val viewModel: RegisterViewModel by viewModels()
    override val viewBinding: FragmentRegistrationBinding by viewBinding(FragmentRegistrationBinding::bind)

    override fun renderUi(data: RegistrationState) {
    }

    override fun setupViews() {
        val decorColor = requireContext().attrValue(R.attr.colorPrimary)
        with(viewBinding){
            tvPrivacy.setOnClickListener { viewModel.navigateToPrivacy() }
            (tvPrivacy.text as Spannable).let { it[0..it.length] = UnderlineSpan(decorColor) }
        }
    }
}