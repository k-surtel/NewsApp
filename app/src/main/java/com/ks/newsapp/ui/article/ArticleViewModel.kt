package com.ks.newsapp.ui.article

import androidx.lifecycle.ViewModel
import com.couchbase.lite.*
import com.ks.newsapp.data.NewsRepository
import com.ks.newsapp.data.Resource
import com.ks.newsapp.data.models.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    lateinit var article: Article

    fun saveArticle(): String? {
        val isArticleSaved = isArticleAlreadySaved()
        return if(isArticleSaved is Resource.Error) isArticleSaved.message
        else if(!isArticleSaved.data!!) saveArticleToDatabase()
        else "The article is already saved in the database"
    }

    private fun isArticleAlreadySaved() = repository.isArticleSaved(article.url)

    private fun saveArticleToDatabase(): String? {
        val saveRequest = repository.saveArticle(article)
        return if(saveRequest is Resource.Error) saveRequest.message
        else null
    }

        fun removeArticle(): String? {
            val removeRequest = repository.removeArticle(article)
            return if(removeRequest is Resource.Error) removeRequest.message
            else null
    }
}