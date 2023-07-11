package ru.netology.nmedia.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.ItemSeparatorTimeBinding
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.view.load
import java.util.*

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onAttachment(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is TimeSeparator -> R.layout.item_separator_time
            null -> error("unknown view type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }
            R.layout.card_ad -> {
                val binding = CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            R.layout.item_separator_time -> {
                val binding = ItemSeparatorTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TimeViewHolder(binding)
            }
            else -> error("unknown view type: $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is TimeSeparator -> (holder as? TimeViewHolder)?.bind(item)
            null -> error("unknown view type")
        }
    }
}

class AdViewHolder(
    private val binding: CardAdBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ad: Ad) {
        binding.apply {
            image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
        }
    }
}

class TimeViewHolder(
    private val binding: ItemSeparatorTimeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(timeSeparator: TimeSeparator) {
        val resource = when (timeSeparator.term) {
            TimeSeparator.Term.TODAY -> "Today"
            TimeSeparator.Term.YESTERDAY -> "Yesterday"
            TimeSeparator.Term.LONG_AGO -> "Long ago"
        }

        binding.timeSeparator.text = resource
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = getDateString(post.published.toEpochSecond())
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"
            menu.isVisible = post.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            Glide.with(binding.avatar)
                .load("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
                .timeout(6_000)
                .circleCrop()
                .into(binding.avatar)

            if (post.attachment != null) {
                Glide.with(binding.attachment)
                    .load("${BuildConfig.BASE_URL}/media/${post.attachment.url}")
                    .timeout(6_000)
                    .into(binding.attachment)
                attachment.visibility = View.VISIBLE
            }

            binding.attachment.setOnClickListener {
                onInteractionListener.onAttachment(post)
            }
        }
    }

    private fun getDateString(time: Long) : String = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH).format(time * 1000L)
}

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}
