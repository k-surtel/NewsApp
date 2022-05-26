package com.ks.newsapp.data

import com.ks.newsapp.data.api.NewsApi
import com.ks.newsapp.data.api.Resource
import com.ks.newsapp.data.models.NewsResponse
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi
) : NewsRepository {

    private val TAG = "NAPP NewsRepositoryImpl"

    override suspend fun getNews(): Resource<NewsResponse> {
        return try {
            val response = newsApi.getNews()
            val result = response.body()

            if (response.isSuccessful && result != null) Resource.Success(result)
            else Resource.Error(response.message())

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}