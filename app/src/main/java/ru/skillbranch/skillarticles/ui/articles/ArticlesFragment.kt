package ru.skillbranch.skillarticles.ui.articles

import androidx.fragment.app.viewModels
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.FragmentArticlesBinding
import ru.skillbranch.skillarticles.ui.BaseFragment
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.ArticlesState
import ru.skillbranch.skillarticles.viewmodels.ArticlesViewModel

class ArticlesFragment : BaseFragment<ArticlesState, ArticlesViewModel, ru.skillbranch.skillarticles.databinding.FragmentArticlesBinding>(R.layout.fragment_articles) {
    override val viewModel: ArticlesViewModel by viewModels()
    override val viewBinding: FragmentArticlesBinding by viewBinding(FragmentArticlesBinding::bind)

    override fun renderUi(data: ArticlesState) {
    }

    override fun setupViews() {
    }

}