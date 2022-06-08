package com.ks.newsapp.ui.saved

import android.util.Log
import androidx.lifecycle.ViewModel
import com.couchbase.lite.*
import com.ks.newsapp.data.models.Article
import com.ks.newsapp.data.models.Source
import com.ks.newsapp.ui.adapters.ArticlesAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "NAPP SavedViewModel"

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val database: Database
) : ViewModel() {

    private val itemsCount = database.count

    fun getSavedArticles(): List<Article> {
        try {
            val query = QueryBuilder.select(
                SelectResult.property("author"),
                SelectResult.property("content"),
                SelectResult.property("description"),
                SelectResult.property("publishedAt"),
                SelectResult.property("source"),
                SelectResult.property("title"),
                SelectResult.property("url"),
                SelectResult.property("urlToImage"),
                SelectResult.expression(Meta.id)
            ).from(DataSource.database(database))

            val results = query.execute()
            val articles = mutableListOf<Article>()

            results.forEach { articles.add(documentToArticle(it)) }
            return articles.toList()

        } catch (e: CouchbaseLiteException) {
            //TODO
        }
        return listOf()
    }

    private fun documentToArticle(document: Result): Article {
        val source = Source(
            id = document.getDictionary("source")?.getString("id") ?: "",
            name = document.getDictionary("source")?.getString("name") ?: ""
        )

        return Article(
            author = document.getString("author") ?: "",
            content = document.getString("content") ?: "",
            description = document.getString("description") ?: "",
            publishedAt = document.getString("publishedAt") ?: "",
            title = document.getString("title") ?: "",
            url = document.getString("url") ?: "",
            urlToImage = document.getString("urlToImage") ?: "",
            source = source,
            id = document.getString("id")
        )
    }

    fun checkDatabaseChanges(adapter: ArticlesAdapter) {
        if(adapter.currentList.size != itemsCount.toInt()) {
            adapter.submitList(getSavedArticles())
        }
    }
}