package com.ks.newsapp.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.couchbase.lite.*
import com.ks.newsapp.data.api.NewsApi
import com.ks.newsapp.data.models.Article
import com.ks.newsapp.data.models.Source
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NewsRepositoryImplTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val testDbDirectory = File(context.filesDir, "testDb")

    @Inject
    lateinit var newsApi: NewsApi
    private lateinit var database: Database
    private lateinit var newsRepository: NewsRepositoryImpl

    @Before
    fun before() {
        hiltRule.inject()
        database = initiateDatabase()
        newsRepository = NewsRepositoryImpl(newsApi, database)
    }

    private fun initiateDatabase(): Database {
        if(!testDbDirectory.exists()) testDbDirectory.mkdir()
        CouchbaseLite.init(context)
        val cfg = DatabaseConfigurationFactory.create()
        cfg.directory = testDbDirectory.absolutePath
        return Database("testDb", cfg)
    }

    @After
    fun after() {
        database.delete()
        if(testDbDirectory.exists()) testDbDirectory.delete()
    }

    @Test
    fun getSavedArticles_returnsArticlesList() {
        val document = MutableDocument().setString("author", "a").setString("content", "c")
            .setString("description", "c").setString("title", "c").setString("url", "c")
        database.save(document)
        assertEquals(database.count.toInt(), newsRepository.getSavedArticles().data?.size)
    }

    @Test
    fun getSavedArticlesCountAfterInsert_returnsDocsCount() {
        database.save(MutableDocument())
        assertEquals(database.count.toInt(), newsRepository.getSavedArticlesCount().data!!)
    }

    @Test
    fun isArticleSaved_returnsTrue() {
        val urlAddress = "TEST_URL"
        database.save(MutableDocument().setString("url", urlAddress))
        assertTrue(newsRepository.isArticleSaved(urlAddress).data!!)
    }

    @Test
    fun isArticleSaved_returnsFalse() {
        database.save(MutableDocument().setString("url", "TEST_URL"))
        assertFalse(newsRepository.isArticleSaved("TEST2").data!!)
    }

    @Test
    fun saveArticle_articleInDatabase() {
        val article = Article("a", "c", "", "", Source("", ""), "t", "TEST_URL", "")
        val docId = newsRepository.saveArticle(article)

        assertEquals(article.url, database.getDocument(docId.data!!)?.getString("url"))
    }

    @Test
    fun saveArticleTwice_oneArticleInDatabase() {
        val article = Article("a", "c", "", "", Source("", ""), "t", "TEST_URL", "")
        newsRepository.saveArticle(article)
        newsRepository.saveArticle(article)

        assertEquals(1, database.count)
    }

    @Test
    fun removeArticle_databaseIsEmpty() {
        val document = MutableDocument()
            .setString("author", "a")
            .setString("content", "c")
            .setString("description", "c")
            .setString("title", "c")
            .setString("url", "c")
        database.save(document)
        val article = Article("a", "c", "c", "", Source("", ""), "c", "c", "", document.id)
        newsRepository.removeArticle(article)

        assertNull(database.getDocument(document.id))
    }

    @Test
    fun removeArticleWithDifferentId() {
        val document = MutableDocument()
            .setString("author", "a")
            .setString("content", "c")
            .setString("description", "c")
            .setString("title", "c")
            .setString("url", "c")
        database.save(document)
        val article = Article("a", "c", "c", "", Source("", ""), "c", "c", "", "some_id")
        newsRepository.removeArticle(article)

        assertNotNull(database.getDocument(document.id))
    }
}