package com.ks.newsapp.data

import com.ks.newsapp.data.api.NewsApi
import com.ks.newsapp.data.api.Resource
import com.ks.newsapp.data.models.NewsResponse
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi
) : NewsRepository {

    private val TAG = "NAPP NewsRepositoryImpl"

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
        return try {
            val response = when(feed) {
                Feed.TOP_NEWS -> {
                    newsApi.getTopNews(
                        country = country,
                        category = category,
                        keywords = keywords,
                        page = page
                    )
                }
                Feed.ALL_NEWS -> {
                    newsApi.getNews(
                        keywords = keywords,
                        domains = domains,
                        from = from,
                        to = to,
                        language = language,
                        page = page
                    )
                }
            }

            val result = response.body()

            if (response.isSuccessful && result != null) Resource.Success(result)
            else Resource.Error(response.message())

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}