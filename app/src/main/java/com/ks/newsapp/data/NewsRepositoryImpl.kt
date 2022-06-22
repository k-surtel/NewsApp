package com.ks.newsapp.data

import android.content.res.Resources
import com.couchbase.lite.*
import com.ks.newsapp.data.api.NewsApi
import com.ks.newsapp.data.models.Article
import com.ks.newsapp.data.models.NewsResponse
import com.ks.newsapp.data.models.Source
import javax.inject.Inject
import com.ks.newsapp.R

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
            Resource.Error(e.message ?: Resources.getSystem().getString(R.string.error_load_news))
        }
    }

    override fun getSavedArticles(): Resource<List<Article>> {
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
            return Resource.Success(articles.toList())

        } catch (e: CouchbaseLiteException) {
            return Resource.Error(e.message ?: Resources.getSystem().getString(R.string.error_load_database))
        }
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
            source = source
        )
    }

    override fun getSavedArticlesCount(): Resource<Int> {
        return try {
            Resource.Success(database.count.toInt())
        } catch (e: CouchbaseLiteException) {
            Resource.Error(e.message ?: Resources.getSystem().getString(R.string.error_load_database))
        }
    }

    override fun isArticleSaved(url: String): Resource<Boolean> {
        try {
            val results = QueryBuilder.select(SelectResult.property("url"))
                .from(DataSource.database(database)).execute()
            results.forEach {
                if(url == it.getString("url")) return Resource.Success(true)
            }
        } catch (e: CouchbaseLiteException) {
            return Resource.Error(e.message ?: Resources.getSystem().getString(R.string.error_load_database))
        }
        return Resource.Success(false)
    }

    override fun saveArticle(article: Article): Resource<String> {
        try {
            val isArticleSaved = isArticleSaved(article.url).data
            if (isArticleSaved != null && isArticleSaved)
                return Resource.Error(Resources.getSystem().getString(R.string.error_article_already_saved))

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
            return Resource.Success(document.id)

        } catch (e: CouchbaseLiteException) {
            return Resource.Error(e.message ?: Resources.getSystem().getString(R.string.error_save_data))
        }
    }

    override fun removeArticle(article: Article): Resource<String> {
        try {
            val document = findDocumentByUrl(article.url)
            document?.let {
                database.delete(it)
                return Resource.Success(it.id)
            }
            return Resource.Error(Resources.getSystem().getString(R.string.error_delete_no_article))
        } catch (e: CouchbaseLiteException) {
            return Resource.Error(e.message ?: Resources.getSystem().getString(R.string.error_delete_from_db))
        }
    }

    private fun findDocumentByUrl(url: String): Document? {
        val query = QueryBuilder.select(SelectResult.expression(Meta.id))
            .from(DataSource.database(database))
            .where(Expression.property("url").equalTo(Expression.string(url)))
            .execute()

        val docId = query.first().getString("id")
        docId?.let {
            return database.getDocument(it)
        }
        return null
    }
}