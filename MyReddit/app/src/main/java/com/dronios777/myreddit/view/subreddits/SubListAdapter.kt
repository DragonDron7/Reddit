package com.dronios777.myreddit.view.subreddits

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dronios777.myreddit.R
import com.dronios777.myreddit.data.model.subreddit.ResponseSubreddits.Data.Children
import com.dronios777.myreddit.databinding.SubredditsItemBinding
import com.dronios777.myreddit.state.ClickSubreddit
import java.util.*


class SubListAdapter(
    private val onClick: (ClickSubreddit, Children) -> Unit
) : ListAdapter<Children, SubListAdapter.SubredditsViewHolder>(
    DiffUtilCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditsViewHolder {

        return SubredditsViewHolder(
            SubredditsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SubredditsViewHolder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            nameSub.text = item.data!!.displayNamePrefixed
            description.text = item.data.publicDescription
            subscribers.text =
                "${subscribers.resources.getString(R.string.subscribers_text)} ${item.data.subscribers}"

            item?.let {
                //устанавливаем иконку
                val icon = if (it.data?.communityIcon.toString().trim() != "")
                    it.data?.communityIcon?.substring(0)?.substringBefore("?")
                else
                    it.data?.iconImg

                Glide
                    .with(communityIcon.context)
                    .load(icon)
                    .circleCrop()
                    .placeholder(R.drawable.icon_default)
                    .into(communityIcon)

                //меняем картинку, если подписаны
                val image = if (it.data?.userIsSubscriber == true)
                    R.drawable.user_is_subscriber
                else
                    R.drawable.add1

                Glide
                    .with(imageSubscriber.context)
                    .load(image)
                    .placeholder(R.drawable.add1)
                    .into(imageSubscriber)
            }

            //клик по сабреддиту (кроме кнопки подписки)
            subreddit.setOnClickListener {
                item?.let {
                    onClick(ClickSubreddit.SUBREDDIT, item)
                }
            }
            //клик (подписаться/отписаться)
            imageSubscriber.setOnClickListener {
                item?.let {
                    onClick(ClickSubreddit.SUBSCRIBE, item)
                }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Children>() {
        //метод вызывается, чтобы проверить, что два разных объекта описывают один и тот же элемент из набора данных
        override fun areItemsTheSame(
            oldItem: Children,
            newItem: Children
        ): Boolean =
            oldItem.data!!.id == newItem.data!!.id

        //метод вызывается, чтобы проверить у 2-х разных объектах одни и теже данные
        override fun areContentsTheSame(
            oldItem: Children,
            newItem: Children
        ): Boolean =
            oldItem == newItem
    }

    class SubredditsViewHolder(val binding: SubredditsItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}