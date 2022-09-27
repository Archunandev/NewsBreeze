package com.greedy.newsbreeze.ui.activity.saved

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.greedy.newsbreeze.databinding.ActivitySavedBinding
import com.greedy.newsbreeze.db.ArticleDatabase
import com.greedy.newsbreeze.repository.NewsRepository
import com.greedy.newsbreeze.ui.activity.detail.DetailActivity
import com.greedy.newsbreeze.ui.viewmodel.NewsViewModel
import com.greedy.newsbreeze.ui.viewmodel.NewsViewModelProviderFactory

class SavedActivity : AppCompatActivity() {

    private lateinit var activitySavedBinding: ActivitySavedBinding

    lateinit var savedAdapter: SavedNewsAdapter

    lateinit var viewModel: NewsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySavedBinding = ActivitySavedBinding.inflate(layoutInflater)
        setContentView(activitySavedBinding.root)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        activitySavedBinding.serchSaved.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var searchedText = activitySavedBinding.serchSaved.text.toString().trim()
                if (searchedText.toString().isNotEmpty())
                    viewModel.searchSaved(searchedText).observe(this, Observer {
                        savedAdapter.differ.submitList(it)
                    })
                return@OnEditorActionListener true
            }
            false
        })

        activitySavedBinding.toolbar.setOnClickListener {
            onBackPressed()
        }


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = savedAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(
                    activitySavedBinding.root,
                    "Successfully deleted article",
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(activitySavedBinding.topSavedRv)
        }


        viewModel.getSavedArticles().observe(this, Observer { articles ->
            savedAdapter.differ.submitList(articles)
        })

        setUpRecyclerView()

    }

    private fun setUpRecyclerView() {
        savedAdapter = SavedNewsAdapter()
        activitySavedBinding.topSavedRv.apply {
            adapter = savedAdapter
            layoutManager = LinearLayoutManager(context)
        }
        savedAdapter.setOnItemClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("article", it)
            startActivity(intent)
        }
    }
}