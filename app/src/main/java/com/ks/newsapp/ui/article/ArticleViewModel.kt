package com.ks.newsapp.ui.article

import androidx.lifecycle.ViewModel
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
        return when(val saveRequest = repository.saveArticle(article)) {
            is Resource.Error -> saveRequest.message
            else -> null
        }
    }

    fun removeArticle(): String? {
        return when(val removeRequest = repository.removeArticle(article)) {
            is Resource.Error -> removeRequest.message
            else -> null
        }
    }
}