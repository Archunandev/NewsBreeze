package com.greedy.newsbreeze.ui.activity.detail

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.greedy.newsbreeze.databinding.ActivityDetailBinding
import com.greedy.newsbreeze.db.ArticleDatabase
import com.greedy.newsbreeze.model.Article
import com.greedy.newsbreeze.repository.NewsRepository
import com.greedy.newsbreeze.ui.viewmodel.NewsViewModel
import com.greedy.newsbreeze.ui.viewmodel.NewsViewModelProviderFactory
import com.greedy.newsbreeze.util.Utility

class DetailActivity : AppCompatActivity() {
    private lateinit var activityDetailBinding: ActivityDetailBinding

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(activityDetailBinding.root)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

      val article = intent.getSerializableExtra("article") as Article

        activityDetailBinding.apply {
            dateNews.text = Utility.convertDate(article.publishedAt!!)
            newsTitle.text = article.title
            authorName.text = article.author
            newsCat.text = article.title
            desDetail.text = article.content
            Glide.with(this@DetailActivity)
                .load(article.urlToImage)
                .into(bcNews)
            toolbar.setOnClickListener {
                onBackPressed()
            }

            saveDatail.setOnClickListener {
                saveDatail.setBackgroundColor(Color.parseColor("#929497"))
                viewModel.saveArticle(article)
            }

            saveNews.setOnClickListener {
                viewModel.saveArticle(article)
            }

        }
    }
}