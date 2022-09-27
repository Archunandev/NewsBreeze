package com.greedy.newsbreeze.ui.activity.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.greedy.newsbreeze.databinding.ItemNewsListBinding
import com.greedy.newsbreeze.model.Article
import com.greedy.newsbreeze.util.Utility
import java.text.SimpleDateFormat

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(val binding: ItemNewsListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemNewsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        var article = differ.currentList[position]
        holder.binding.apply {
            newsTitle.text = article?.title
            newsDes.text = article?.description
            newsData.text = Utility.convertDate(article.publishedAt!!)
            Glide.with(newsImage.context)
                .load(article.urlToImage)
                .into(newsImage)

            saveImg.setOnClickListener {
                saveBc.setBackgroundColor(Color.parseColor("#CD4CAF50"))
                saveImg.visibility = View.GONE
                savedImg.visibility = View.VISIBLE
                onSaveClickListener?.let { it(article) }
            }

            savedImg.setOnClickListener {
                saveBc.setBackgroundColor(Color.parseColor("#929497"))
                saveImg.visibility = View.VISIBLE
                savedImg.visibility = View.GONE
                onUnSaveClickListener?.let { it(article) }
            }

            read.setOnClickListener {
                onItemClickListener?.let { it(article) }
            }

            saveBtn.setOnClickListener {
                saveBc.setBackgroundColor(Color.parseColor("#CD4CAF50"))
                saveBtn.text = "Saved"
                onSaveClickListener?.let { it(article) }
            }

        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null
    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    private var onSaveClickListener: ((Article) -> Unit)? = null
    fun setOnSaveClickListener(listener: (Article) -> Unit) {
        onSaveClickListener = listener
    }

    private var onUnSaveClickListener: ((Article) -> Unit)? = null
    fun setOnUnSaveClickListener(listener: (Article) -> Unit) {
        onUnSaveClickListener = listener
    }

}