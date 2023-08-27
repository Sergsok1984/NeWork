package ru.sokolov_diplom.nework.adapter

import android.media.MediaPlayer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SeekBar
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.sokolov_diplom.nework.databinding.ItemPostBinding
import ru.sokolov_diplom.nework.R
import ru.sokolov_diplom.nework.dto.AttachmentType
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.util.formatDateTime


interface OnPostInteractionListener {
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onLike(post: Post)
    fun onWatchVideo(post: Post)
}

class PostsAdapter(
    private val onPostInteractionListener: OnPostInteractionListener,
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onPostInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val onPostInteractionListener: OnPostInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    private var mp: MediaPlayer? = null

    fun bind(post: Post) {

        binding.apply {
            if (post.attachment != null) {
                when (post.attachment.type) {
                    AttachmentType.IMAGE -> imageAttachment.visibility = View.VISIBLE
                    AttachmentType.AUDIO -> audioGroup.visibility = View.VISIBLE
                    AttachmentType.VIDEO -> video.visibility = View.VISIBLE
                }
            } else {
                imageAttachment.visibility = View.GONE
                audioGroup.visibility = View.GONE
                video.visibility = View.VISIBLE
            }

            imageAttachment.visibility =
                if (post.attachment != null && post.attachment.type == AttachmentType.IMAGE) View.VISIBLE else View.GONE

            audioGroup.visibility =
                if (post.attachment != null && post.attachment.type == AttachmentType.AUDIO) View.VISIBLE else View.GONE

            video.visibility =
                if (post.attachment != null && post.attachment.type == AttachmentType.VIDEO) View.VISIBLE else View.GONE

            Glide.with(authorAvatar)
                .load(post.authorAvatar)
                .error(R.drawable.ic_person_24)
                .timeout(10_000)
                .circleCrop()
                .into(authorAvatar)

            author.text = itemView.context.getString(
                R.string.author_job,
                post.author,
                post.authorJob ?: itemView.context.resources.getString(R.string.null_job)
            )
            published.text = formatDateTime(post.published)
            content.text = post.content

            if (post.link != null) {
                link.visibility = View.VISIBLE
                link.text = itemView.context.getString(R.string.get_link, post.link)
            } else link.visibility = View.GONE

            post.attachment?.apply {
                Glide.with(imageAttachment)
                    .load(this.url)
                    .error(R.drawable.ic_error_24)
                    .timeout(10_000)
                    .into(imageAttachment)
            }

            playButton.setOnClickListener {
                if (mp == null) {
                    mp = MediaPlayer.create(it.context, post.attachment?.url?.toUri())

                    audioBar.max = mp!!.duration
                    val handler = Handler()
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            try {
                                audioBar.progress = mp!!.currentPosition
                                handler.postDelayed(this, 1000)
                            } catch (e: Exception) {
                                audioBar.progress = 0
                            }
                        }
                    }, 0)

                    mp?.start()
                }
            }

            pauseButton.setOnClickListener {
                if (mp != null) mp?.pause()
            }

            stopButton.setOnClickListener {
                if (mp != null) {
                    mp?.stop()
                    mp?.reset()
                    mp?.release()
                    mp = null
                }
            }

            audioBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) mp?.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            video.setOnClickListener {
                onPostInteractionListener.onWatchVideo(post)
            }

            like.isChecked = post.likedByMe
            like.text = "${post.likeOwnerIds.size}"

            like.setOnClickListener {
                onPostInteractionListener.onLike(post)
            }

            menu.isVisible = post.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onPostInteractionListener.onRemove(post)
                                true
                            }

                            R.id.editContent -> {
                                onPostInteractionListener.onRemove(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
