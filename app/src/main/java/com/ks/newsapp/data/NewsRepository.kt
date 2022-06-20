package com.ks.newsapp.data

import com.ks.newsapp.data.api.Resource
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

    fun getSavedArticles(): List<Article>

    fun getSavedArticlesCount(): Int

    fun isArticleSaved(url: String): Boolean

    fun saveArticle(article: Article): String?

    fun removeArticle(article: Article): String?
}