package com.ks.newsapp.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.newsapp.data.NewsRepository
import com.ks.newsapp.data.api.Resource
import com.ks.newsapp.data.models.Article
import com.ks.newsapp.data.models.Source
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    var country: String? = "us"
    var category: String? = null


    sealed class NewsEvent {
        class Success(val articles: List<Article>): NewsEvent()
        class Failure(val message: String): NewsEvent()
        object Empty: NewsEvent()
    }

    private val _getNewsEvent = MutableStateFlow<NewsEvent>(NewsEvent.Empty)
    val getNewsEvent: StateFlow<NewsEvent> = _getNewsEvent

    fun getNews() = viewModelScope.launch {
        when (val newsResponse = repository.getNews(country, category)) {
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