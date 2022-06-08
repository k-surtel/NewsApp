package com.ks.newsapp.ui.saved

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ks.newsapp.data.models.Article
import com.ks.newsapp.databinding.FragmentSavedBinding
import com.ks.newsapp.ui.adapters.ArticlesAdapter
import com.ks.newsapp.ui.adapters.ClickListener
import com.ks.newsapp.ui.article.ArticleActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedFragment : Fragment() {

    private lateinit var binding: FragmentSavedBinding
    private val viewModel: SavedViewModel by viewModels()
    private lateinit var adapter: ArticlesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentSavedBinding.inflate(inflater, container, false)

        adapter = ArticlesAdapter(ClickListener { goToArticle(it) })
        binding.recyclerView.adapter = adapter
        adapter.submitList(viewModel.getSavedArticles())

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkDatabaseChanges(adapter)
    }

    private fun goToArticle(article: Article) {
        val intent = Intent(activity, ArticleActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("article", article)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}