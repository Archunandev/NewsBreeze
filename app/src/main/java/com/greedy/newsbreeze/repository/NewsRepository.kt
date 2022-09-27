package com.greedy.newsbreeze.repository

import com.greedy.newsbreeze.api.RetrofitInstance
import com.greedy.newsbreeze.db.ArticleDatabase
import com.greedy.newsbreeze.model.Article

class NewsRepository(val db: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode:String, pageNumber:Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    fun searchDataBse(searchQuery: String) = db.getArticleDao().searchDatabase(searchQuery)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    suspend fun searchNews(searchQuery: String) =
        RetrofitInstance.api.searchForNews(searchQuery)
}