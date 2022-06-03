package com.ks.newsapp.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.newsapp.data.NewsRepository
import com.ks.newsapp.data.api.Resource
import com.ks.newsapp.data.models.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    var topCountry: String? = "us"
    var topCategory: String? = null
    var topKeywords: String? = null

    fun isTopNewsDataValid(): Boolean {
        return !(topCountry.isNullOrBlank() && topCategory.isNullOrBlank() && topKeywords.isNullOrBlank())
    }

    var domains: String? = null
    var keywords: String? = null
    var excludeDomains: String? = null
    var from: String? = null
    var to: String? = null
    var language: String? = null

    sealed class NewsEvent {
        class Success(val articles: List<Article>): NewsEvent()
        class Failure(val message: String): NewsEvent()
        object Empty: NewsEvent()
    }

    private val _getNewsEvent = MutableStateFlow<NewsEvent>(NewsEvent.Empty)
    val getNewsEvent: StateFlow<NewsEvent> = _getNewsEvent

    fun getNews() = viewModelScope.launch {
        when (val newsResponse = repository.getNews(topCountry, topCategory, topKeywords)) {
            is Resource.Error -> {
                _getNewsEvent.value = NewsEvent.Failure(newsResponse.message!!)
            }
            is Resource.Success -> {
                _getNewsEvent.value = NewsEvent.Success(newsResponse.data!!.articles)
            }
        }
    }

    fun newsReceived() {
        _getNewsEvent.value = NewsEvent.Empty
    }
}