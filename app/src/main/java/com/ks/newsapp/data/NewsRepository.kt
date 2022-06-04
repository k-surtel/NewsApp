package com.ks.newsapp.data

import com.ks.newsapp.data.api.Resource
import com.ks.newsapp.data.models.NewsResponse

interface NewsRepository {

    suspend fun getNews(
        feed: Feed,
        country: String?,
        category: String?,
        keywords: String?,
        domains: String?,
        from: String?,
        to: String?,
        language: String?
    ): Resource<NewsResponse>
}