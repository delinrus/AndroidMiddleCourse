package ru.skillbranch.skillarticles.ui.articles

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.FragmentArticlesBinding
import ru.skillbranch.skillarticles.ui.BaseFragment
import ru.skillbranch.skillarticles.ui.article.LoadStateItemsAdapter
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.articles.ArticleItem
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesState
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel

class ArticlesFragment :
    BaseFragment<ArticlesState, ArticlesViewModel, FragmentArticlesBinding>(R.layout.fragment_articles), IArticlesView {
    override val viewModel: ArticlesViewModel by activityViewModels()
    override val viewBinding: FragmentArticlesBinding by viewBinding(FragmentArticlesBinding::bind)
    private var articlesAdapter: ArticlesAdapter? = null

    override fun renderUi(data: ArticlesState) {
        //TODO implement me later
    }

    override fun setupViews() {
        articlesAdapter = ArticlesAdapter(::onArticleClick,::onToggleBookmark)

        with(viewBinding){
            with(rvArticles){
                ArticlesAdapter(::onArticleClick, ::onToggleBookmark)
                    .also { articlesAdapter = it }
                    .run {
                        adapter = withLoadStateFooter(
                            footer = LoadStateItemsAdapter(::retry)
                        )
                        layoutManager = LinearLayoutManager(requireContext())
                        addItemDecoration(DividerItemDecoration(
                            requireContext(),
                            LinearLayoutManager.VERTICAL
                        ))

                        //add load state listener for show load progress
                        addLoadStateListener(::loadStateListener)
                    }
            }

            btnRetry.setOnClickListener {
                articlesAdapter?.retry()
            }

        }
    }

    fun loadStateListener(state: CombinedLoadStates){
        with(viewBinding){
            val  isLoading = state.refresh is LoadState.Loading //if loading data from network initial refresh
            val isError = state.refresh is LoadState.Error //network error initial loading
            val isSuccessfulLoad = !isLoading && !isError


            rvArticles.isVisible = isSuccessfulLoad
            progress.isVisible = isLoading
            groupErr.isVisible = isError
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        articlesAdapter = null
    }

    @ExperimentalPagingApi
    override fun observeViewModelData() {
        viewModel.articlesPager.observe(viewLifecycleOwner){
            articlesAdapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onArticleClick(articleItem: ArticleItem){
        viewModel.navigateToArticle(articleItem)
    }

    override fun onToggleBookmark(articleItem: ArticleItem, isChecked: Boolean){
        viewModel.checkBookmark(articleItem,isChecked)
    }

}