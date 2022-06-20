package com.ks.newsapp.ui.news

import com.ks.newsapp.MainCoroutineRule
import com.ks.newsapp.data.FakeNewsRepository
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

    @Before
    fun before() {
        newsViewModel = NewsViewModel(FakeNewsRepository())
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
    fun `get news`() {
        newsViewModel.loadNews()
        // todo
    }
}