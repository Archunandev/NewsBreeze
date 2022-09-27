package com.greedy.newsbreeze.ui.activity.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.greedy.newsbreeze.databinding.ItemSavedListBinding
import com.greedy.newsbreeze.model.Article
import com.greedy.newsbreeze.util.Utility

class SavedNewsAdapter : RecyclerView.Adapter<SavedNewsAdapter.SavedArticleViewHolder>() {

    inner class SavedArticleViewHolder(val binding: ItemSavedListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedArticleViewHolder {
        return SavedArticleViewHolder(
            ItemSavedListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SavedArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.binding.apply {
            tag.text = article?.content
            savedTitle.text = article?.title
            savedDate.text = Utility.convertDate(article?.publishedAt!!)
            savedAuthor.text = article.author
            Glide.with(savedImage.context)
                .load(article.urlToImage)
                .into(savedImage)

            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(article) }
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

}