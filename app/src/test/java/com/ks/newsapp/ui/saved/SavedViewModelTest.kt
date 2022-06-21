package com.ks.newsapp.ui.saved

import com.ks.newsapp.data.FakeNewsRepository
import com.ks.newsapp.data.Resource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SavedViewModelTest {

    private lateinit var viewModel: SavedViewModel
    private lateinit var fakeNewsRepository: FakeNewsRepository

    @Before
    fun before() {
        fakeNewsRepository = FakeNewsRepository()
        viewModel = SavedViewModel(fakeNewsRepository)
    }

    @Test
    fun `get saved articles, returns articles list`() {
        assertEquals(1, viewModel.getSavedArticles().data!!.size)
    }

    @Test
    fun `get saved articles couchbase error, returns error resource`() {
        fakeNewsRepository.shouldReturnCouchbaseError(true)
        assert(viewModel.getSavedArticles() is Resource.Error)
    }

    @Test
    fun `check database changes no count change, returns false resource`() {
        assertEquals(false, viewModel.checkDatabaseChanges(1).data)
    }

    @Test
    fun `check database changes (db changed), returns true resource`() {
        assertEquals(true, viewModel.checkDatabaseChanges(2).data)
    }

    @Test
    fun `check database changes, couchbase exception`() {
        fakeNewsRepository.shouldReturnCouchbaseError(true)
        assert(viewModel.checkDatabaseChanges(1) is Resource.Error)
    }
}