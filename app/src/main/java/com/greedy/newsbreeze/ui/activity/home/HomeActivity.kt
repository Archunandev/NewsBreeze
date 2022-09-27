package com.greedy.newsbreeze.ui.activity.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.greedy.newsbreeze.R
import com.greedy.newsbreeze.databinding.ActivityMainBinding
import com.greedy.newsbreeze.db.ArticleDatabase
import com.greedy.newsbreeze.repository.NewsRepository
import com.greedy.newsbreeze.ui.activity.detail.DetailActivity
import com.greedy.newsbreeze.ui.activity.saved.SavedActivity
import com.greedy.newsbreeze.ui.activity.saved.SavedNewsAdapter
import com.greedy.newsbreeze.ui.viewmodel.NewsViewModel
import com.greedy.newsbreeze.ui.viewmodel.NewsViewModelProviderFactory
import com.greedy.newsbreeze.util.Constants.Companion.QUERY_PAGE_SIZE
import com.greedy.newsbreeze.util.Resource

class HomeActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    lateinit var newsAdapter: NewsAdapter
    lateinit var savedAdapter: SavedNewsAdapter

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        performRequest()
        performSearch()
        setUpRecyclerView()

        activityMainBinding.discoverSearchET.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var searchedText = activityMainBinding.discoverSearchET.text.toString().trim()
                if (searchedText.toString().isNotEmpty())
                    viewModel.searchNews(searchedText)
                    return@OnEditorActionListener true
            }
            false
        })
    }

    private fun performSearch() {
        viewModel.searchNews.observe(this, Observer { response ->

            when (response) {

                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(this, "An error occured: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }

        })
    }

    private fun performRequest() {

        viewModel.breakingNews.observe(this, Observer { response ->

            when (response) {

                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(this, "An error occured: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        })
    }

    private fun hideProgressBar() {
        activityMainBinding.newsProgress.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        activityMainBinding.newsProgress.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getBreakingNews("in")
                isScrolling = false
            } else {
                activityMainBinding.topNewsRv.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }


    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        savedAdapter = SavedNewsAdapter()
        activityMainBinding.topNewsRv.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
        }
        newsAdapter.setOnItemClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("article", it)
            startActivity(intent)
        }

        newsAdapter.setOnSaveClickListener {
            viewModel.saveArticle(it)
        }

        newsAdapter.setOnUnSaveClickListener {
            viewModel.deleteArticle(it)
            savedAdapter.differ.currentList.remove(it)
        }

        activityMainBinding.toggleSaved.setOnClickListener {
            val intent = Intent(this, SavedActivity::class.java)
            startActivity(intent)
        }


    }

}