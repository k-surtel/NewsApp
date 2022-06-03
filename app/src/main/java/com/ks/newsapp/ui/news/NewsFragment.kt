package com.ks.newsapp.ui.news

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.ks.newsapp.R
import com.ks.newsapp.databinding.FragmentNewsBinding
import com.ks.newsapp.ui.adapters.ArticlesAdapter
import com.ks.newsapp.ui.adapters.ClickListener
import com.ks.newsapp.ui.article.ArticleActivity
import com.ks.newsapp.ui.news.Utils.mapCategory
import com.ks.newsapp.ui.news.Utils.mapCountry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private val TAG = "NAPP NewsFragment"

    private lateinit var binding: FragmentNewsBinding
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        binding = FragmentNewsBinding.inflate(inflater)
        setupArticlesList()
        setupArticlesSourceChange()
        setupTopNewsFilter()

        return binding.root
    }

    private fun setupArticlesList() {
        val adapter = ArticlesAdapter(ClickListener {
            val intent = Intent(activity, ArticleActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            intent.putExtras(bundle)
            startActivity(intent)
        })

        binding.recyclerView.adapter = adapter
        viewModel.getNews()
        binding.refreshLayout.setOnRefreshListener { viewModel.getNews() }

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
                //viewModel.newsReceived()
            }
        }
    }

    private fun setupArticlesSourceChange() {
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

    private fun setupTopNewsFilter() {
        val countriesList = resources.getStringArray(R.array.countries_array).asList()
        val countriesAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, countriesList)
        (binding.countrySpinner as? AutoCompleteTextView)?.setAdapter(countriesAdapter)

        val categoriesList = resources.getStringArray(R.array.categories_array).asList()
        val categoriesAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, categoriesList)
        (binding.categorySpinner as? AutoCompleteTextView)?.setAdapter(categoriesAdapter)

        binding.topNewsApplyFilterButton.setOnClickListener {
            viewModel.topCountry = mapCountry(binding.countrySpinner.text.toString())
            viewModel.topCategory = mapCategory(binding.categorySpinner.text.toString())
            viewModel.topKeywords = binding.keywords.text.toString()

            if(viewModel.isTopNewsDataValid()) viewModel.getNews()
            else Snackbar.make(binding.root, R.string.top_news_no_params, Snackbar.LENGTH_LONG).show()
        }
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