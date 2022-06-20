package com.ks.newsapp.data

import com.couchbase.lite.*
import com.ks.newsapp.data.api.NewsApi
import com.ks.newsapp.data.api.Resource
import com.ks.newsapp.data.models.Article
import com.ks.newsapp.data.models.NewsResponse
import com.ks.newsapp.data.models.Source
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi,
    private val database: Database
) : NewsRepository {

    override suspend fun getNews(
        feed: Feed,
        country: String?,
        category: String?,
        keywords: String?,
        domains: String?,
        from: String?,
        to: String?,
        language: String?,
        page: Int
    ): Resource<NewsResponse> {
        return try {
            val response = when(feed) {
                Feed.TOP_NEWS -> {
                    newsApi.getTopNews(
                        country = country,
                        category = category,
                        keywords = keywords,
                        page = page
                    )
                }
                Feed.ALL_NEWS -> {
                    newsApi.getNews(
                        keywords = keywords,
                        domains = domains,
                        from = from,
                        to = to,
                        language = language,
                        page = page
                    )
                }
            }

            val result = response.body()

            if (response.isSuccessful && result != null) Resource.Success(result)
            else Resource.Error(response.message())

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun getSavedArticles(): List<Article> {
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

        } catch (e: CouchbaseLiteException) { }
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

    override fun getSavedArticlesCount(): Int {
        return database.count.toInt()
    }

    override fun isArticleSaved(url: String): Boolean {
        try {
            val results = QueryBuilder.select(SelectResult.property("url"))
                .from(DataSource.database(database)).execute()
            results.forEach { if(url == it.getString("url")) return true }
        } catch (e: CouchbaseLiteException) { }
        return false
    }

    override fun saveArticle(article: Article): String? {
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

    override fun removeArticle(article: Article): String? {
        return try {
            val document = database.getDocument(article.id!!)
            document?.let { database.delete(it) }
            article.id = null
            null
        } catch (e: CouchbaseLiteException) { e.message }
    }
}