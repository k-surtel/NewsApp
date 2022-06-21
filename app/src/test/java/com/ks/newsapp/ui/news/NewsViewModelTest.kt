package com.ks.newsapp.ui.news

import com.ks.newsapp.MainCoroutineRule
import com.ks.newsapp.data.FakeNewsRepository
import com.ks.newsapp.data.NewsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewsViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var fakeNewsRepository: FakeNewsRepository

    @Before
    fun before() {
        fakeNewsRepository = FakeNewsRepository()
        newsViewModel = NewsViewModel(fakeNewsRepository)
    }

    @Test
    fun `validate top news, returns true`() {
        newsViewModel.category = "c"
        assertTrue(newsViewModel.validateTopNews())
    }

    @Test
    fun `validate top news with all fields null, returns false`() {
        newsViewModel.country = null
        newsViewModel.category = null
        newsViewModel.topKeywords = null
        assertFalse(newsViewModel.validateTopNews())
    }

    @Test
    fun `validate top news with all fields whitespaces, returns false`() {
        newsViewModel.country = "  "
        newsViewModel.category = "  "
        newsViewModel.topKeywords = "  "
        assertFalse(newsViewModel.validateTopNews())
    }

    @Test
    fun `validate all news, returns true`() {
        newsViewModel.keywords = "hello"
        assertTrue(newsViewModel.validateAllNews())
    }

    @Test
    fun `validate all news with all fields null, returns false`() {
        newsViewModel.keywords = null
        newsViewModel.domains = null
        assertFalse(newsViewModel.validateAllNews())
    }

    @Test
    fun `validate all news with all fields whitespaces, returns false`() {
        newsViewModel.keywords = "  "
        newsViewModel.domains = ""
        assertFalse(newsViewModel.validateAllNews())
    }

    @Test
    fun `get news network error, getNewsEvent is failure`() {
        fakeNewsRepository.shouldReturnNetworkError(true)
        newsViewModel.loadNews()
        assert(newsViewModel.getNewsEvent.value is NewsViewModel.NewsEvent.Failure)
    }

    @Test
    fun `get news, getNewsEvent is success with article received`() {
        newsViewModel.loadNews()
        assertNotNull((newsViewModel.getNewsEvent.value as NewsViewModel.NewsEvent.Success).articles)
    }

    @Test
    fun `get news repeatedly, getNewsEvent has full list of articles`() {
        newsViewModel.loadNews()
        newsViewModel.loadNextPage()
        newsViewModel.loadNextPage()
        assertEquals(3, (newsViewModel.getNewsEvent.value as NewsViewModel.NewsEvent.Success).articles.size)
    }

    @Test
    fun `get news list gets refreshed, doesn't add up articles`() {
        newsViewModel.loadNews()
        newsViewModel.loadNews()
        assertEquals(1, (newsViewModel.getNewsEvent.value as NewsViewModel.NewsEvent.Success).articles.size)
    }
}