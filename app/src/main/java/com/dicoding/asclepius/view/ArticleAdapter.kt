package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemArticleBinding

class ArticleAdapter(private val articles: List<ArticlesItem>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(private val itemBinding: ItemArticleBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(article: ArticlesItem) {
            itemBinding.titleText.text = article.title ?: "No Title Available"
            itemBinding.subtitleText.text = article.description ?: "No Description Available"

            Glide.with(itemBinding.root.context)
                .load(article.urlToImage)
                .into(itemBinding.picturePreview)

            itemBinding.textOverlay.text = "Read more"

            itemBinding.root.setOnClickListener {
                article.url?.let { link ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    itemBinding.root.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemArticleBinding.inflate(inflater, parent, false)
        return ArticleViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentArticle = articles[position]
        holder.bind(currentArticle)
    }

    override fun getItemCount(): Int = articles.size
}