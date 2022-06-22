package com.ks.newsapp.ui.saved

import androidx.lifecycle.ViewModel
import com.ks.newsapp.data.NewsRepository
import com.ks.newsapp.data.Resource
import com.ks.newsapp.data.models.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    fun getSavedArticles(): Resource<List<Article>> = repository.getSavedArticles()

    fun checkDatabaseChanges(listSize: Int): Resource<Boolean> {
        val savedArticlesCount = repository.getSavedArticlesCount()
        if(savedArticlesCount is Resource.Error) return Resource.Error(savedArticlesCount.message!!)
        if(listSize != savedArticlesCount.data) { return Resource.Success(true) }
        return Resource.Success(false)
    }
}