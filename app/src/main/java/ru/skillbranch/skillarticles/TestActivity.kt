package ru.skillbranch.skillarticles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import ru.skillbranch.skillarticles.databinding.ActivityTestBinding
import ru.skillbranch.skillarticles.ui.article.CommentAdapter
import ru.skillbranch.skillarticles.ui.article.LoadStateItemsAdapter

class TestActivity : AppCompatActivity() {
    private val viewModel by viewModels<TestViewModel>()
    private lateinit var viewBinding: ActivityTestBinding
    private val commentsAdapter = CommentAdapter({})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        with(viewBinding.rvTest) {
            adapter = commentsAdapter.withLoadStateHeaderAndFooter(
                header = LoadStateItemsAdapter(commentsAdapter::retry),
                footer = LoadStateItemsAdapter(commentsAdapter::retry)
            )
            commentsAdapter.addLoadStateListener { loadState ->
                if(loadState.refresh == LoadState.Loading) {
                    viewBinding.progress.isVisible = true
                    isVisible = false
                }else{
                    viewBinding.progress.isVisible = false
                    isVisible = true
                }
            }
            layoutManager = LinearLayoutManager(this@TestActivity)
        }

        with(viewBinding) {
            btnDo.setOnClickListener {
                commentsAdapter.refresh()
            }
        }

        viewModel.commentPager.observe(this){
            commentsAdapter.submitData(lifecycle, it)
        }
    }
}