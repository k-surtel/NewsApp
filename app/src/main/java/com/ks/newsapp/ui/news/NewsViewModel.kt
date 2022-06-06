package com.ks.newsapp.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.newsapp.data.Feed
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

    var currentFeed = Feed.TOP_NEWS

    var country: String? = "us"
    var category: String? = null
    var topKeywords: String? = null

    var keywords: String? = null
    var domains: String? = null
    var from: String? = null
    var fromTimestamp: Long? = null
    var to: String? = null
    var toTimestamp: Long? = null
    var language: String? = null

    private val _getNewsEvent = MutableStateFlow<NewsEvent>(NewsEvent.Empty)
    val getNewsEvent: StateFlow<NewsEvent> = _getNewsEvent


    fun validateTopNews(): Boolean {
        return !(country.isNullOrBlank() && category.isNullOrBlank() && topKeywords.isNullOrBlank())
    }

    fun validateAllNews(): Boolean {
        return !(keywords.isNullOrBlank() && domains.isNullOrBlank())
    }

    fun getNews(feed: Feed) = viewModelScope.launch {
        refreshNewsEvent()
        val keywords = if(feed == Feed.TOP_NEWS) topKeywords else keywords

        when (val newsResponse = repository.getNews(
            feed = feed,
            country = country,
            category = category,
            keywords = keywords,
            domains = domains,
            from = from,
            to = to,
            language = language
        )) {
            is Resource.Error -> {
                if(newsResponse.message.isNullOrBlank())
                    _getNewsEvent.value = NewsEvent.Failure("An unknown error occured")
                else _getNewsEvent.value = NewsEvent.Failure(newsResponse.message)
            }
            is Resource.Success -> {
                _getNewsEvent.value = NewsEvent.Success(newsResponse.data!!.articles)
            }
        }
    }

    private fun refreshNewsEvent() { _getNewsEvent.value = NewsEvent.Empty }

    sealed class NewsEvent {
        class Success(val articles: List<Article>): NewsEvent()
        class Failure(val message: String): NewsEvent()
        object Empty: NewsEvent()
    }
}