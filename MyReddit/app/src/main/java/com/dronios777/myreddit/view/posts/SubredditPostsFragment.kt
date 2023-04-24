package com.dronios777.myreddit.view.posts


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dronios777.myreddit.R
import com.dronios777.myreddit.SUB
import com.dronios777.myreddit.UNSUB
import com.dronios777.myreddit.data.model.posts.SubredditPostsResponse
import com.dronios777.myreddit.databinding.FragmentSubredditPostsBinding
import com.dronios777.myreddit.state.ClickPost
import com.dronios777.myreddit.view.posts.comments.CommentsViewModel
import com.dronios777.myreddit.view.user_info.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class SubredditPostsFragment : Fragment() {

    private var _binding: FragmentSubredditPostsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubredditPostsViewModel by activityViewModels()
    private val commentsViewModel: CommentsViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private val postsAdapter by lazy {
        PostsListAdapter { state, item -> onItemClick(state, item) }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubredditPostsBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshPosts()//слушатель для обновления постов
        settingButtonFollowSub()//настройка кнопки "подписа/отписка"
        setSubredditInfo()//установка данных об открытом сабреддите
        getItemPosts()//получение списка постов адаптером
    }

    private fun settingButtonFollowSub() {
        binding.buttonFollowSub.setOnClickListener {
            //вызываем AlertDialog с вопросом о подписке/отписке
            setUpAlertDialog()
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.subreddit.collect {
                //меняем иконку и текст у кнопки
                val image: Int?
                val text: String?
                if (it != null) {
                    if (it.data?.userIsSubscriber == true) {
                        image = R.drawable.user_is_subscriber
                        text = resources.getString(R.string.button_joined_text)
                    } else {
                        image = R.drawable.add1
                        text = resources.getString(R.string.button_join_text)
                    }

                    binding.apply {
                        buttonFollowSub.text = text
                        buttonFollowSub.setIconResource(image)
                    }
                }
            }

        }
    }

    private fun setUpAlertDialog() {

        //если подписаны, то запрос на отписку
        val action = if (viewModel.subreddit.value?.data?.userIsSubscriber == true) UNSUB else SUB
        val subTitle = if (action == SUB) R.string.subJoin_title else R.string.subLeave_title
        val subMessage =
            if (action == SUB) {
                resources.getString(R.string.subJoin_message)
            } else {
                resources.getString(R.string.subLeave_message)
            }

        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(subTitle)
            .setMessage("$subMessage ${viewModel.subreddit.value?.data?.displayNamePrefixed}")
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.joinOrLeave(action)
                //задержка, т.к. сервер не мгновенно обновляет данные
                Handler(Looper.getMainLooper()).postDelayed({ updateSub() }, 2000)
            }
            .setNegativeButton(R.string.no) { _, _ ->
                dialog.create().hide()
            }
        dialog.create().show()
    }

    private fun updateSub() {//обновляем информацию
        viewModel.subreddit.value?.data?.let {
            it.displayNamePrefixed?.let { it1 -> viewModel.search(it1) }
        }
    }

    private fun setSubredditInfo() {
        with(viewModel.subreddit.value?.data) {
            //устанавливаем иконку
            val icon = if (this?.communityIcon?.trim() != "")
                this?.communityIcon?.substring(0)?.substringBefore("?")
            else
                this.iconImg
            Glide
                .with(binding.icon.context)
                .load(icon)
                .circleCrop()
                .placeholder(R.drawable.icon_default)
                .into(binding.icon)

            //определяем какой верхний баннер будет у сабреддита(community)
            val headerImage =
                if (this?.bannerBackgroundImage?.trim() != "")
                    this?.bannerBackgroundImage?.substring(0)?.substringBefore("?")
                else
                    if ((this?.bannerImg?.trim() ?: "") != "") this.bannerImg
                    else
                        if (this?.headerImg.toString().trim() != "") this.headerImg
                        else
                            binding.bannerImage.setBackgroundResource(R.color.custom_color_secondary)

            Glide
                .with(binding.bannerImage.context)
                .load(headerImage)
                .placeholder(R.color.custom_color_secondary)
                .into(binding.bannerImage)
        }

        binding.apply {
            titleSub.text = viewModel.subreddit.value?.data?.displayNamePrefixed ?: ""
            followSub.text = buildString {
                append(resources.getString(R.string.subscribers_text) + " ")
                append(viewModel.subreddit.value?.data?.subscribers.toString())
            }
            descriptionSub.text = viewModel.subreddit.value?.data?.publicDescription
        }
    }

    private fun getItemPosts() {
        binding.rvPostsList.adapter = postsAdapter

        lifecycleScope.launchWhenStarted {
            with(viewModel.subredditPosts) {
                collect {
                    onEach { posts ->
                        posts?.collectLatest { postsAdapter.submitData(it) }
                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                }
            }
        }
    }


    private fun refreshPosts() {
        with(binding) {
            swipeRefresh.setOnRefreshListener {
                viewModel.apply {
                    subreddit.value?.let {
                        getSubredditPosts(it)
                        swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
    }

    private fun onItemClick(
        state: ClickPost,
        item: SubredditPostsResponse.Data.Posts
    ) {
        with(viewModel) {
            when (state) {
                //изменяем рейтинг поста (голосуем)
                ClickPost.VOTE_ADD -> {
                    vote(item, ClickPost.VOTE_ADD.dir!!)
                }
                ClickPost.VOTE_MINUS -> {
                    vote(item, ClickPost.VOTE_MINUS.dir!!)
                }
                ClickPost.VOTE_ZERO -> {
                    vote(item, ClickPost.VOTE_ZERO.dir!!)
                }
                ClickPost.COMMENTS -> {
                    item.postData?.id?.let { commentsViewModel.comments(it) }
                    findNavController().navigate(R.id.action_subredditPostsFragment_to_commentsFragment)
                }
                ClickPost.POST_AUTHOR -> {
                    item.postData?.author?.let { userViewModel.getUserInfo(it) }
                    findNavController().navigate(R.id.userFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


