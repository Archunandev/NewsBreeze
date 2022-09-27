package com.greedy.newsbreeze.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.greedy.newsbreeze.model.Article
import java.util.concurrent.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article):Long


    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Query("SELECT * FROM articles WHERE title LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article) : Int

}