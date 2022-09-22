package com.greedy.newsbreeze.api

import com.greedy.newsbreeze.model.NewsResponse
import com.greedy.newsbreeze.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "in",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apikey: String = API_KEY
    ): Response<NewsResponse>


    @GET("v2/top-headlines")
    suspend fun getTechNews(
        @Query("sources")
        source: String = "techcrunch",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apikey: String = API_KEY
    ): Response<NewsResponse>


    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apikey: String = API_KEY
    ): Response<NewsResponse>

}