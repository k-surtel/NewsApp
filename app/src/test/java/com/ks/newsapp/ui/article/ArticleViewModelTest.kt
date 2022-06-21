package com.ks.newsapp.ui.article

import com.ks.newsapp.data.FakeNewsRepository
import com.ks.newsapp.data.models.Article
import com.ks.newsapp.data.models.Source
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ArticleViewModelTest {

    private lateinit var viewModel: ArticleViewModel
    private lateinit var fakeNewsRepository: FakeNewsRepository

    @Before
    fun before() {
        fakeNewsRepository = FakeNewsRepository()
        viewModel = ArticleViewModel(fakeNewsRepository)
        viewModel.article = Article("", "", "", "", Source("", ""), "", "", "", "")
    }

    @Test
    fun `save article, returns couchbase exception`() {
        fakeNewsRepository.shouldReturnCouchbaseError(true)
        assertNotNull(viewModel.saveArticle())
        assertNotEquals("The article is already saved in the database", viewModel.saveArticle())
    }

    @Test
    fun `save article that is already saved, returns already saved message`() {
        fakeNewsRepository.shouldArticleBeAlreadySaved(true)
        assertEquals("The article is already saved in the database", viewModel.saveArticle())
    }

    @Test
    fun `save article successful, returns null`() {
        assertNull(viewModel.saveArticle())
    }

    @Test
    fun `remove article successful, returns null`() {
        assertNull(viewModel.removeArticle())
    }

    @Test
    fun `remove article couchbase exception, returns error message`() {
        fakeNewsRepository.shouldReturnCouchbaseError(true)
        assertNotNull(viewModel.removeArticle())
    }
}