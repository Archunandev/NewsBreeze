package com.greedy.newsbreeze.ui.activity.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.greedy.newsbreeze.R
import com.greedy.newsbreeze.databinding.ActivityMainBinding
import com.greedy.newsbreeze.repository.NewsRepository
import com.greedy.newsbreeze.ui.viewmodel.NewsViewModel
import com.greedy.newsbreeze.ui.viewmodel.NewsViewModelProviderFactory

class HomeActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        val newsRepository = NewsRepository()
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        activityMainBinding.discoverSearchET.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var searchedText = activityMainBinding.discoverSearchET.text.toString().trim()
                if (searchedText.toString().isNotEmpty())
                // performSearch()
                    return@OnEditorActionListener true
            }
            false
        })
    }
}