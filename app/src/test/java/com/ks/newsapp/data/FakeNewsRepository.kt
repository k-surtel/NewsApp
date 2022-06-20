package com.ks.newsapp.data

import com.ks.newsapp.data.api.Resource
import com.ks.newsapp.data.models.NewsResponse

class FakeNewsRepository : NewsRepository {

    private var shouldReturnNetworkError = false

    fun shouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun getNews(
        feed: Feed,
        country: String?,
        category: String?,
        keywords: String?,
        domains: String?,
        from: String?,
        to: String?,
        language: String?,
        page: Int
    ): Resource<NewsResponse> {
        return if(shouldReturnNetworkError) Resource.Error("")
        else Resource.Success(NewsResponse(listOf(), "ok", 0))
    }
}