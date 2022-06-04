package com.ks.newsapp.ui.news

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.ks.newsapp.R
import com.ks.newsapp.data.Feed
import com.ks.newsapp.databinding.FragmentNewsBinding
import com.ks.newsapp.ui.adapters.ArticlesAdapter
import com.ks.newsapp.ui.adapters.ClickListener
import com.ks.newsapp.ui.article.ArticleActivity
import com.ks.newsapp.ui.news.Utils.mapCategory
import com.ks.newsapp.ui.news.Utils.mapCountry
import com.ks.newsapp.ui.news.Utils.mapLanguage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private val TAG = "NAPP NewsFragment"

    private lateinit var binding: FragmentNewsBinding
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        binding = FragmentNewsBinding.inflate(inflater)
        populateArticlesList()
        handleFeedSourceChange()
        handleTopNewsFilter()
        setupAllNewsFilter()

        return binding.root
    }

    private fun populateArticlesList() {
        val adapter = ArticlesAdapter(ClickListener {
            val intent = Intent(activity, ArticleActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            intent.putExtras(bundle)
            startActivity(intent)
        })

        binding.recyclerView.adapter = adapter
        viewModel.getNews(viewModel.currentFeed)
        binding.refreshLayout.setOnRefreshListener { viewModel.getNews(viewModel.currentFeed) }

        lifecycleScope.launchWhenStarted {
            viewModel.getNewsEvent.collect {
                when (it) {
                    is NewsViewModel.NewsEvent.Success -> {
                        binding.refreshLayout.isRefreshing = false
                        adapter.submitList(it.articles)
                    }
                    is NewsViewModel.NewsEvent.Failure -> {
                        binding.refreshLayout.isRefreshing = false
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }

                viewModel.newsReceived()
            }
        }
    }

    private fun handleFeedSourceChange() {
        binding.feedButtonGroup.check(R.id.top_news_button)
        binding.feedButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(!isChecked) return@addOnButtonCheckedListener
            when(checkedId) {
                R.id.top_news_button -> {
                    binding.allNewsLayout.visibility = View.GONE
                    binding.topNewsLayout.visibility = View.VISIBLE
                }
                R.id.all_news_button -> {
                    binding.topNewsLayout.visibility = View.GONE
                    binding.allNewsLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun handleTopNewsFilter() {
        setupCountriesSpinner()
        setupCategoriesSpinner()

        binding.topNewsApplyFilterButton.setOnClickListener {
            collectTopNewsData()
            if(viewModel.validateTopNews()) viewModel.getNews(viewModel.currentFeed)
            else Snackbar.make(binding.root, R.string.top_news_no_params, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setupCountriesSpinner() {
        val countriesList = resources.getStringArray(R.array.countries_array).asList()
        val countriesAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, countriesList)
        (binding.topCountrySpinner as? AutoCompleteTextView)?.setAdapter(countriesAdapter)
    }

    private fun setupCategoriesSpinner() {
        val categoriesList = resources.getStringArray(R.array.categories_array).asList()
        val categoriesAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, categoriesList)
        (binding.topCategorySpinner as? AutoCompleteTextView)?.setAdapter(categoriesAdapter)
    }

    private fun collectTopNewsData() {
        viewModel.currentFeed = Feed.TOP_NEWS
        viewModel.country = mapCountry(binding.topCountrySpinner.text.toString())
        viewModel.category = mapCategory(binding.topCategorySpinner.text.toString())
        viewModel.topKeywords = binding.topKeywords.text.toString()
    }

    private fun setupAllNewsFilter() {
        setupDomainsHint()
        setupDatePickers()
        setupLanguagesSpinner()

        binding.allNewsApplyFilterButton.setOnClickListener {
            collectAllNewsData()
            if(viewModel.validateAllNews()) viewModel.getNews(viewModel.currentFeed)
            else Snackbar.make(binding.root, R.string.all_news_no_params, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setupDomainsHint() {
        binding.domains.setOnFocusChangeListener { _, b ->
            if(b) binding.domainsHint.visibility = View.VISIBLE
            else binding.domainsHint.visibility = View.INVISIBLE
        }
    }

    private fun setupDatePickers() {
        binding.from.setOnFocusChangeListener { _, b -> if(b) { showFromDatePicker() } }
        binding.from.setOnClickListener { showFromDatePicker() }
        binding.from.setOnLongClickListener { clearFromDate() }
        binding.to.setOnFocusChangeListener { _, b -> if(b) { showToDatePicker() } }
        binding.to.setOnClickListener { showToDatePicker() }
        binding.to.setOnLongClickListener { clearToDate() }
    }

    private fun setupLanguagesSpinner() {
        val languagesList = resources.getStringArray(R.array.languages_array).asList()
        val languagesAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, languagesList)
        (binding.languageSpinner as? AutoCompleteTextView)?.setAdapter(languagesAdapter)
    }

    private fun collectAllNewsData() {
        viewModel.currentFeed = Feed.ALL_NEWS
        viewModel.keywords = binding.keywords.text.toString()
        viewModel.domains = binding.domains.text.toString()
        viewModel.language = mapLanguage(binding.languageSpinner.text.toString())
    }

    private fun getDatePicker(selection: Long?): MaterialDatePicker<Long> {
        return MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(selection)
            .build()
    }

    private fun showFromDatePicker() {
        val datePicker = getDatePicker(viewModel.fromTimestamp)
        datePicker.addOnPositiveButtonClickListener { setFromDate(it) }
        datePicker.show(activity!!.supportFragmentManager, "tag");
    }

    private fun showToDatePicker() {
        val datePicker = getDatePicker(viewModel.toTimestamp)
        datePicker.addOnPositiveButtonClickListener { setToDate(it) }
        datePicker.show(activity!!.supportFragmentManager, "tag");
    }

    private fun setFromDate(date: Long) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date

        val day = if(cal.get(Calendar.DAY_OF_MONTH) < 10) "0${cal.get(Calendar.DAY_OF_MONTH)}" else cal.get(Calendar.DAY_OF_MONTH).toString()
        val month = if(cal.get(Calendar.MONTH)+1 < 10) "0${cal.get(Calendar.MONTH)+1}" else (cal.get(Calendar.MONTH)+1).toString()
        val dateString = "${cal.get(Calendar.YEAR)}-$month-$day"

        viewModel.fromTimestamp = date
        viewModel.from = dateString
        binding.from.setText(dateString)
    }

    private fun setToDate(date: Long) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date

        val day = if(cal.get(Calendar.DAY_OF_MONTH) < 10) "0${cal.get(Calendar.DAY_OF_MONTH)}" else cal.get(Calendar.DAY_OF_MONTH).toString()
        val month = if(cal.get(Calendar.MONTH)+1 < 10) "0${cal.get(Calendar.MONTH)+1}" else (cal.get(Calendar.MONTH)+1).toString()
        val dateString = "${cal.get(Calendar.YEAR)}-$month-$day"

        viewModel.toTimestamp = date
        viewModel.to = dateString
        binding.to.setText(dateString)
    }

    private fun clearFromDate(): Boolean {
        viewModel.fromTimestamp = null
        viewModel.from = null
        binding.from.setText("")
        return true
    }

    private fun clearToDate(): Boolean {
        viewModel.toTimestamp = null
        viewModel.to = null
        binding.to.setText("")
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.filter) {
            if(!binding.drawerLayout.isDrawerOpen(binding.filterDrawer))
                binding.drawerLayout.openDrawer(binding.filterDrawer)
            else binding.drawerLayout.closeDrawer(binding.filterDrawer)
        }
        return true
    }
}