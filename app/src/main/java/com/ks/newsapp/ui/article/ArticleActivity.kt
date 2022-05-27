package com.ks.newsapp.ui.article

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ks.newsapp.R
import com.ks.newsapp.data.models.Article
import com.ks.newsapp.databinding.ActivityArticleBinding

class ArticleActivity : AppCompatActivity() {

    private val TAG = "NAPPArticleActivity"

    private lateinit var binding: ActivityArticleBinding
    private val viewModel: ArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.article = intent.getSerializableExtra("article") as Article

        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        binding.title.text = viewModel.article.title
        Glide.with(binding.root).load(viewModel.article.urlToImage).into(binding.imageView)
        binding.content.text = viewModel.article.content
        if(viewModel.article.author.isNullOrEmpty()) binding.author.visibility = View.GONE
        else binding.author.text = viewModel.article.author
        binding.date.text = viewModel.article.publishedAt.replace('T', ' ').trimEnd('Z')
        binding.source.text = viewModel.article.source.name
    }
}