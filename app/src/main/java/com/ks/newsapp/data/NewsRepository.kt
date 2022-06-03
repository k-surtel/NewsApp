package com.ks.newsapp.data

import com.ks.newsapp.data.api.Resource
import com.ks.newsapp.data.models.NewsResponse

interface NewsRepository {

    suspend fun getNews(
        country: String?,
        category: String?
    ): Resource<NewsResponse>
}