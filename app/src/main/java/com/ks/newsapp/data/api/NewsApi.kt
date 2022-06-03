package com.ks.newsapp.data.api

import com.ks.newsapp.data.api.Utils.API_KEY
import com.ks.newsapp.data.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("/v2/everything")
    suspend fun getNews(
        @Query("apiKey") key: String = API_KEY,
        @Query("q") keyword: String? = null,
        @Query("page") page: Int = 1
    ): Response<NewsResponse>

    @GET("/v2/top-headlines")
    suspend fun getTopNews(
        @Query("apiKey") key: String = API_KEY,
        @Query("country") country: String?,
        @Query("category") category: String?,
        @Query("q") keywords: String?,
        @Query("page") page: Int = 1
    ): Response<NewsResponse>
}