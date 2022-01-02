package ru.skillbranch.skillarticles.ui.articles

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.FragmentArticlesBinding
import ru.skillbranch.skillarticles.ui.BaseFragment
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.ArticlesState
import ru.skillbranch.skillarticles.viewmodels.ArticlesViewModel
import ru.skillbranch.skillarticles.viewmodels.articles.ArticleItem

class ArticlesFragment : BaseFragment<ArticlesState, ArticlesViewModel, ru.skillbranch.skillarticles.databinding.FragmentArticlesBinding>(R.layout.fragment_articles) {
    override val viewModel: ArticlesViewModel by viewModels()
    override val viewBinding: FragmentArticlesBinding by viewBinding(FragmentArticlesBinding::bind)
    private var articlesAdapter: ArticlesAdapter? = null

    override fun renderUi(data: ArticlesState) {

    }

    override fun setupViews() {
        articlesAdapter = ArticlesAdapter(
            onClick = { articleItem -> viewModel.navigateToArticle(articleItem) },
            onToggleBookmark = { articleItem: ArticleItem, isChecked: Boolean ->
                viewModel.checkBookmark(articleItem, isChecked)
            }
        )

        viewBinding.rvArticles.apply {
            adapter = articlesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        articlesAdapter = null
    }

    override fun observeViewModelData() {
        viewModel.articles.observe(viewLifecycleOwner){
            articlesAdapter?.submitList(it)
        }
    }
}