package ru.skillbranch.skillarticles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.skillbranch.skillarticles.databinding.ActivityTestBinding
import ru.skillbranch.skillarticles.ui.article.CommentAdapter

class TestActivity : AppCompatActivity() {
    private val viewModel by viewModels<TestViewModel>()
    private lateinit var viewBinding: ActivityTestBinding
    private val commentsAdapter = CommentAdapter({})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        with(viewBinding.rvTest) {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(this@TestActivity)

        }

        with(viewBinding) {
            btnDo.setOnClickListener {
                // DO Something with list
            }
        }

        viewModel.commentPager.observe(this){
            commentsAdapter.submitData(lifecycle, it)
        }
    }
}