package com.greedy.newsbreeze.repository

import com.greedy.newsbreeze.api.RetrofitInstance

class NewsRepository() {

    suspend fun getBreakingNews(countryCode:String, pageNumber:Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
}