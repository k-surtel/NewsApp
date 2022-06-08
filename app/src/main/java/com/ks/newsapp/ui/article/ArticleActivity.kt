package com.ks.newsapp.ui.article

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ks.newsapp.R
import com.ks.newsapp.data.models.Article
import com.ks.newsapp.databinding.ActivityArticleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding
    private val viewModel: ArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.article = intent.getSerializableExtra("article") as Article
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSupportActionBar()
        bindData()

        databaseOperationsButtonsVisibility(viewModel.article.id != null)

        binding.buttonViewInBrowser.setOnClickListener { openArticleUrl() }
        binding.buttonSave.setOnClickListener { saveToDatabase() }
        binding.buttonRemove.setOnClickListener { removeFromDatabase() }
    }

    private fun setupSupportActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        if (supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    private fun bindData() {
        binding.title.text = viewModel.article.title
        Glide.with(binding.root).load(viewModel.article.urlToImage).into(binding.imageView)
        binding.content.text = viewModel.article.content
        if(viewModel.article.author.isNullOrEmpty()) binding.author.visibility = View.GONE
        else binding.author.text = viewModel.article.author
        binding.date.text = viewModel.article.publishedAt.replace('T', ' ').trimEnd('Z')
        binding.source.text = viewModel.article.source.name
    }

    private fun databaseOperationsButtonsVisibility(isSaved: Boolean) {
        if(isSaved) {
            binding.buttonSave.visibility = View.GONE
            binding.buttonRemove.visibility = View.VISIBLE
        } else {
            binding.buttonSave.visibility = View.VISIBLE
            binding.buttonRemove.visibility = View.GONE
        }
    }

    private fun openArticleUrl() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(viewModel.article.url)
        startActivity(intent)
    }

    private fun saveToDatabase() {
        if(viewModel.saveArticle()) databaseOperationsButtonsVisibility(true)
    }

    private fun removeFromDatabase() {
        if(viewModel.removeArticle()) databaseOperationsButtonsVisibility(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}