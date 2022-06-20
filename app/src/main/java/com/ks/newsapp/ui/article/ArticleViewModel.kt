package com.ks.newsapp.ui.article

import androidx.lifecycle.ViewModel
import com.couchbase.lite.*
import com.ks.newsapp.data.NewsRepository
import com.ks.newsapp.data.models.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    lateinit var article: Article

    fun saveArticle(): String? {
        return if(!isArticleAlreadySaved()) saveArticleToDatabase()
        else "The article is already saved in the database"
    }

    private fun isArticleAlreadySaved() = repository.isArticleSaved(article.url)

    private fun saveArticleToDatabase(): String? = repository.saveArticle(article)

    fun removeArticle(): String? = repository.removeArticle(article)
}