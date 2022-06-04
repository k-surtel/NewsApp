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

        binding.buttonViewInBrowser.setOnClickListener { openArticleUrl() }
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

    private fun openArticleUrl() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(viewModel.article.url)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}