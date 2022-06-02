package com.ks.newsapp.ui.news

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ks.newsapp.R
import com.ks.newsapp.databinding.FragmentNewsBinding
import com.ks.newsapp.ui.adapters.ArticlesAdapter
import com.ks.newsapp.ui.adapters.ClickListener
import com.ks.newsapp.ui.article.ArticleActivity
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
            }
        }

        return binding.root
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