package com.ks.newsapp.ui.article

import androidx.lifecycle.ViewModel
import com.couchbase.lite.*
import com.ks.newsapp.data.models.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val database: Database
) : ViewModel() {

    lateinit var article: Article

    fun saveArticle(): String? {
        return if(!isArticleAlreadySaved()) saveArticleToDatabase()
        else "The article is already saved in the database"
    }

    private fun isArticleAlreadySaved(): Boolean {
        try {
            val results = QueryBuilder.select(SelectResult.property("url"))
                .from(DataSource.database(database)).execute()
            results.forEach { if(article.url == it.getString("url")) return true }
        } catch (e: CouchbaseLiteException) { }
        return false
    }

    private fun saveArticleToDatabase(): String? {
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
            return null

        } catch (e: CouchbaseLiteException) { return e.message }
    }

    fun removeArticle(): String? {
        return try {
            val document = database.getDocument(article.id!!)
            document?.let { database.delete(it) }
            article.id = null
            null
        } catch (e: CouchbaseLiteException) { e.message }
    }
}