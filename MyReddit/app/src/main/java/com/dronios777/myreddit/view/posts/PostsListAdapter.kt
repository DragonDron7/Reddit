package com.dronios777.myreddit.view.posts

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dronios777.myreddit.R
import com.dronios777.myreddit.SHARE
import com.dronios777.myreddit.data.model.posts.SubredditPostsResponse.Data.Posts
import com.dronios777.myreddit.databinding.PostsItemBinding
import com.dronios777.myreddit.state.ClickPost

class PostsListAdapter(private val onClick: (ClickPost, Posts) -> Unit) :
    PagingDataAdapter<Posts, PostsListAdapter.PostsViewHolder>(DiffUtilCallback()) {

    private var mediaController: MediaController? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {

        return PostsViewHolder(
            PostsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            item?.let {

                settingVote(holder, item)//настройка кнопок отвечающих за голосование
                share(holder, item)//настройка кнопки поделиться (share)

                videoPost.visibility = View.GONE
                ivHyperlink.visibility = View.GONE
                imagePost.visibility = View.GONE

                subNamePref.text = item.postData?.subNamePref
                postAuthor.text = "u/${item.postData?.author}"
                postTitle.text = item.postData?.title
                postDescription.text = item.postData?.description
                countComments.text = item.postData?.numComments
                rating.text = item.postData?.ups
                //----------------------------------

                if (item.postData?.post_hint == null) { //если нет подсказки, проверяем на gif
                    if (item.postData?.simple_medial_url != null && item.postData.simple_medial_url.contains(
                            "gif",
                            ignoreCase = true
                        )
                    ) {
                        imagePost.visibility = View.VISIBLE
                        Glide
                            .with(imagePost.context).asGif()
                            .load(item.postData.simple_medial_url)
                            .centerCrop()
                            .into(imagePost)
                    }
                } else { //видео, ссылка или что-то другое указывается в подсказке (post_hint)
                    when (item.postData.post_hint.toString().trim().lowercase()) {
                        "image" -> {
                            Glide
                                .with(imagePost.context)
                                .load(item.postData.simple_medial_url)
                                .centerCrop()
                                .into(imagePost)
                            imagePost.visibility = View.VISIBLE
                        }
                        "link" -> {
                            ivHyperlink.isClickable = true
                            ivHyperlink.movementMethod = LinkMovementMethod.getInstance()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                ivHyperlink.text = Html.fromHtml(
                                    item.postData.simple_medial_url,
                                    Html.FROM_HTML_MODE_LEGACY
                                )
                            }
                            ivHyperlink.visibility = View.VISIBLE
                        }
                        "hosted:video" -> {
                            val hls: String?
                            hls = item.postData.secure_media?.reddit_video?.hls_url.toString()
                                .trim().substring(0)
                                .substringBefore("?")
                            var video: String? = null
                            if (item.postData.secure_media?.reddit_video?.complex_media_url != null
                            )
                                video =
                                    item.postData.secure_media.reddit_video.complex_media_url.toString()
                                        .trim().substring(0)
                                        .substringBefore("?")

                            if (video != null) {
                                videoPost.apply {
                                    visibility = View.VISIBLE
                                    if (mediaController == null) {
                                        // создание объекта класса медиаконтроллера
                                        mediaController = MediaController(videoPost.context)
                                        mediaController!!.setAnchorView(videoPost)
                                    }
                                    setVideoURI(hls.toUri())
                                    seekTo(1)
                                    setMediaController(mediaController)
                                    requestFocus()
                                    start()
                                    //зацикливаю воспроизведение видео
                                    setOnCompletionListener { start() }
                                }
                            } else {
                                videoPost.visibility = View.GONE
                                mediaController = null
                            }
                        }
                    }
                }

                message.setOnClickListener {
                    onClick(ClickPost.COMMENTS, item)
                }
                postAuthor.setOnClickListener {
                    onClick(ClickPost.POST_AUTHOR, item)
                }

            }
        }
    }

    private fun share(holder: PostsViewHolder, item: Posts) {
        with(holder.binding) {
            share.setOnClickListener { v: View ->
                ShareCompat.IntentBuilder(v.context)
                    .setType("text/plain")
                    .setChooserTitle("Share URL")
                    .setText(
                        "https://www.reddit.com${item.postData?.permalink}$SHARE"
                    )
                    .startChooser()
            }
        }
    }

    private fun settingVote(holder: PostsViewHolder, item: Posts) {
        with(holder.binding) {
            item.let {
                val likes = item.postData?.likes

                when (item.postData?.likes) {
                    false -> { //уже проголосовали (-1)
                        plus.setImageResource(R.drawable.arrow_up_black)
                        minus.setImageResource(R.drawable.arrow_down_blue)
                    }
                    true -> { //уже проголосовали (+1)
                        plus.setImageResource(R.drawable.arrow_up_red)
                        minus.setImageResource(R.drawable.arrow_down_black)
                    }
                    null -> {//нет голоса (0)
                        plus.setImageResource(R.drawable.arrow_up_black)
                        minus.setImageResource(R.drawable.arrow_down_black)
                    }
                }

                //клик (поднять рейтинг поста)
                plus.setOnClickListener {
                    when (likes) {
                        null -> onClick(ClickPost.VOTE_ADD, item) //+1
                        true -> onClick(ClickPost.VOTE_ZERO, item) //0
                        false -> onClick(ClickPost.VOTE_ADD, item) //+1
                    }
                }
                //клик (опустить рейтинг поста)
                minus.setOnClickListener {
                    when (likes) {
                        null -> onClick(ClickPost.VOTE_MINUS, item) //-1
                        true -> onClick(ClickPost.VOTE_MINUS, item) //-1
                        false -> onClick(ClickPost.VOTE_ZERO, item) //0
                    }
                }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Posts>() {
        //метод определяет новый это объект или обновление уже старого объекта
        override fun areItemsTheSame(
            oldItem: Posts,
            newItem: Posts
        ): Boolean =
            oldItem.postData?.id == newItem.postData?.id

        //метод вызывается, чтобы проверить нужно ли обновлять совпавшие объекты
        override fun areContentsTheSame(
            oldItem: Posts,
            newItem: Posts
        ): Boolean =
            oldItem == newItem
    }

    class PostsViewHolder(val binding: PostsItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
