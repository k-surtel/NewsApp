package com.ks.newsapp.ui.saved

import androidx.lifecycle.ViewModel
import com.couchbase.lite.*
import com.ks.newsapp.data.NewsRepository
import com.ks.newsapp.data.models.Article
import com.ks.newsapp.data.models.Source
import com.ks.newsapp.ui.adapters.ArticlesAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    fun getSavedArticles(): List<Article> = repository.getSavedArticles()

    fun checkDatabaseChanges(adapter: ArticlesAdapter) {
        if(adapter.currentList.size != repository.getSavedArticlesCount()) {
            adapter.submitList(getSavedArticles())
        }
    }
}