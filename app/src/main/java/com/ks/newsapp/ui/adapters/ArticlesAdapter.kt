package com.ks.newsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ks.newsapp.data.models.Article
import com.ks.newsapp.databinding.ItemArticleBinding

class ArticlesAdapter(private val clickListener: ClickListener) :
    ListAdapter<Article, ArticlesAdapter.ViewHolder>(DecksDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), clickListener)

    class ViewHolder private constructor(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article, clickListener: ClickListener) {
            binding.article = article
            //binding.clickListener = clickListener
            binding.executePendingBindings()

            Glide.with(binding.root).load(article.urlToImage).into(binding.image)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemArticleBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class DecksDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.url == newItem.url
    }
    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}

class ClickListener(val clickListener: (article: Article) -> Unit) {
    fun onClick(article: Article) = clickListener(article)
}