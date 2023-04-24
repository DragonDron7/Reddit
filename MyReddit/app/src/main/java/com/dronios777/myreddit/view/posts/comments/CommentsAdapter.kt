package com.dronios777.myreddit.view.posts.comments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dronios777.myreddit.DATE_FORMAT
import com.dronios777.myreddit.R
import com.dronios777.myreddit.data.model.posts.CommentsResponse.Data.Comments
import com.dronios777.myreddit.databinding.CommentsItemBinding
import com.dronios777.myreddit.state.ClickPost
import java.text.SimpleDateFormat
import java.util.*

class CommentsAdapter(
    private val onClick: (ClickPost, Comments) -> Unit
) : ListAdapter<Comments, CommentsAdapter.CommentsViewHolder>(
    DiffUtilCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {

        return CommentsViewHolder(
            CommentsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val item = getItem(position)

        val formatter = SimpleDateFormat(DATE_FORMAT)
        formatter.timeZone = TimeZone.getTimeZone("GMT")

        with(holder.binding) {
            item?.let {

                settingVote(holder, item)//настройка кнопок отвечающих за голосование
                commentDate.text = try {
                    formatter.format(item.commentsData?.created_utc?.times(1000))
                } catch (e: Exception) {
                    ""
                }
                comment.text = item.commentsData?.body
                rating.text = item.commentsData?.ups
            }
        }
    }

    private fun settingVote(holder: CommentsViewHolder, item: Comments) {
        with(holder.binding) {
            item.let {
                val likes = item.commentsData?.likes

                when (item.commentsData?.likes) {
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

    class DiffUtilCallback : DiffUtil.ItemCallback<Comments>() {
        //метод определяет новый это объект или обновление уже старого объекта
        override fun areItemsTheSame(
            oldItem: Comments,
            newItem: Comments
        ): Boolean =
            oldItem.commentsData?.id == newItem.commentsData?.id

        //метод вызывается, чтобы проверить нужно ли обновлять совпавшие объекты
        override fun areContentsTheSame(
            oldItem: Comments,
            newItem: Comments
        ): Boolean =
            oldItem == newItem
    }

    class CommentsViewHolder(val binding: CommentsItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
