package com.ks.newsapp.ui.article

import androidx.lifecycle.ViewModel
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.Database
import com.couchbase.lite.MutableDictionary
import com.couchbase.lite.MutableDocument
import com.ks.newsapp.data.models.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val database: Database
) : ViewModel() {

    lateinit var article: Article

    fun saveArticle(): Boolean {
        try {
            val source = MutableDictionary()
                .setString("id", article.source.id)
                .setString("name", article.source.name)

            val document = MutableDocument()
                .setString("author", article.author)
                .setString("content", article.content)
                .setString("description", article.description)
                .setString("publishedAt", article.publishedAt)
                .setDictionary("source", source)
                .setString("title", article.title)
                .setString("url", article.url)
                .setString("urlToImage", article.urlToImage)

            database.save(document)
            article.id = document.id
            return true

        } catch (e: CouchbaseLiteException) { return false }
    }

    fun removeArticle(): Boolean {
        return try {
            val document = database.getDocument(article.id!!)
            document?.let { database.delete(it) }
            article.id = null
            true
        } catch (e: CouchbaseLiteException) { false }
    }
}