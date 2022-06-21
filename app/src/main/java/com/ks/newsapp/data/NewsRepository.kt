package com.ks.newsapp.data

import com.ks.newsapp.data.models.Article
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
        language: String?,
        page: Int
    ): Resource<NewsResponse>

    fun getSavedArticles(): Resource<List<Article>>

    fun getSavedArticlesCount(): Resource<Int>

    fun isArticleSaved(url: String): Resource<Boolean>

    fun saveArticle(article: Article): Resource<String>

    fun removeArticle(article: Article): Resource<String>
}