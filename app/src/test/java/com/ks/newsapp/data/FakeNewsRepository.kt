package com.ks.newsapp.data

import com.ks.newsapp.data.models.Article
import com.ks.newsapp.data.models.NewsResponse
import com.ks.newsapp.data.models.Source

class FakeNewsRepository : NewsRepository {

    private var shouldReturnNetworkError = false
    private var shouldReturnCouchbaseError = false
    private var shouldArticleBeAlreadySaved = false
    //private var articleCount = 0

    fun shouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    fun shouldReturnCouchbaseError(value: Boolean) {
        shouldReturnCouchbaseError = value
    }

    fun shouldArticleBeAlreadySaved(value: Boolean) {
        shouldArticleBeAlreadySaved = value
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
        else  {
            Resource.Success(NewsResponse(
                listOf(Article("", "", "", "",Source("", ""), "", "", "", "")),
                "ok",
                0
            ))
        }
    }

    override fun getSavedArticles(): Resource<List<Article>> {
        return if(shouldReturnCouchbaseError) Resource.Error("getSavedArticles ERROR")
        else Resource.Success(listOf(
            Article("", "", "", "",Source("", ""), "", "", "", "")
        ))
    }

    override fun getSavedArticlesCount(): Resource<Int> {
        return if(shouldReturnCouchbaseError) Resource.Error("getSavedArticlesCount ERROR")
        else Resource.Success(1)
    }

    override fun isArticleSaved(url: String): Resource<Boolean> {
        return if(shouldReturnCouchbaseError) Resource.Error("isArticleSaved ERROR")
        else if(shouldArticleBeAlreadySaved) Resource.Success(true)
        else Resource.Success(false)
    }

    override fun saveArticle(article: Article): Resource<String> {
        return if(shouldReturnCouchbaseError) Resource.Error("saveArticle ERROR")
        else Resource.Success("")
    }

    override fun removeArticle(article: Article): Resource<String> {
        return if(shouldReturnCouchbaseError) Resource.Error("removeArticle ERROR")
        else Resource.Success("")
    }

}