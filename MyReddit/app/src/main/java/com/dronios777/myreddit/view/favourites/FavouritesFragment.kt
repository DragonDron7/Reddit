package com.dronios777.myreddit.view.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dronios777.myreddit.*
import com.dronios777.myreddit.data.model.posts.SubredditPostsResponse
import com.dronios777.myreddit.data.model.subreddit.ResponseSubreddits
import com.dronios777.myreddit.databinding.FragmentFavouritesBinding
import com.dronios777.myreddit.state.ClickPost
import com.dronios777.myreddit.state.ClickSubreddit
import com.dronios777.myreddit.view.posts.PostsListAdapter
import com.dronios777.myreddit.view.posts.SubredditPostsViewModel
import com.dronios777.myreddit.view.posts.comments.CommentsViewModel
import com.dronios777.myreddit.view.subreddits.SubListAdapter
import com.dronios777.myreddit.view.user_info.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouritesViewModel by activityViewModels()
    private val viewModelSubredditPosts: SubredditPostsViewModel by activityViewModels()
    private val commentsViewModel: CommentsViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private val subredditsAdapter by lazy {
        SubListAdapter { state, item -> onSubClick(state, item) }
    }

    private val postsFavoriteAdapter by lazy {
        PostsListAdapter { state, item -> onPostClick(state, item) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshSubreddits()//слушатель для обновления сабреддитов
        toggleButtonGroupListener()//слушатель переключения кнопок
        showHideLoadingIndicator() //слушатель индикатора загрузки
        performClick() //щелчок по кнопке
    }

    private fun performClick() {
        with(binding) {
            if (viewModel.btnClick.value == BTN_SUB)
                btnSub.performClick() else btnPosts.performClick()
        }
    }

    private fun toggleButtonGroupListener() {
        binding.buttonGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->

            if (isChecked) {
                when (checkedId) {
                    R.id.btnSub -> {
                        viewModel.getFavouritesSubreddits()//загружаем сабреддиты, на которые подписан user
                        getItemSubreddit()//получение списка сабреддитов адаптером
                        viewModel.btnClick(BTN_SUB) //кнопка избранных сабреддитов активна
                        binding.favList.visibility = View.VISIBLE
                    }
                    R.id.btnPosts -> {
                        viewModel.subscribePost()//загружаем посты из сабреддитов, на которые подписан user
                        getItemPost()//получение списка постов адаптером
                        viewModel.btnClick(BTN_POSTS)//кнопка избранных постов активна
                        binding.favList.visibility = View.VISIBLE
                    }
                }
            } else {
                if (toggleButtonGroup.checkedButtonId == View.NO_ID) {
                    binding.favList.visibility = View.GONE
                    Toast.makeText(requireContext(), R.string.mes_txt_noSelect, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun showHideLoadingIndicator() {
        viewModel.isLoading.onEach {
            binding.swipeRefresh.isRefreshing = it
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun getItemSubreddit() {
        binding.favList.adapter = subredditsAdapter
        viewModel.subreddits.onEach {
            subredditsAdapter.submitList(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun getItemPost() {
        binding.favList.adapter = postsFavoriteAdapter

        lifecycleScope.launchWhenStarted {
            viewModel.subredditPosts.collect {
                viewModel.subredditPosts.onEach { posts ->
                    posts?.collectLatest { postsFavoriteAdapter.submitData(it) }
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            }
        }
    }

    private fun refreshSubreddits() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun onSubClick(
        state: ClickSubreddit,
        item: ResponseSubreddits.Data.Children
    ) {
        when (state) {
            //запрашиваем список постов для данного сабреддита
            ClickSubreddit.SUBREDDIT -> {
                viewModelSubredditPosts.getSubredditPosts(item)
                findNavController().navigate(R.id.action_favouritesFragment_to_subredditPostsFragment)
            }
            //вызываем AlertDialog с вопросом о подписке/отписке
            ClickSubreddit.SUBSCRIBE -> setUpAlertDialog(item)
        }
    }

    private fun onPostClick(
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
                    findNavController().navigate(R.id.commentsFragment)
                }
                ClickPost.POST_AUTHOR -> {
                    item.postData?.author?.let { userViewModel.getUserInfo(it) }
                    findNavController().navigate(R.id.userFragment)
                }
            }
        }
    }

    private fun setUpAlertDialog(item: ResponseSubreddits.Data.Children) {
        //если подписаны, то запрос на отписку
        val action = if (item.data?.userIsSubscriber == true) UNSUB else SUB

        val subTitle = if (action == SUB) R.string.subJoin_title else R.string.subLeave_title
        val subMessage =
            if (action == SUB) resources.getString(R.string.subJoin_message)
            else resources.getString(R.string.subLeave_message)

        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(subTitle)
            .setMessage("$subMessage ${item.data?.displayNamePrefixed}")
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.joinOrLeave(item, action)
            }
            .setNegativeButton(R.string.no) { _, _ ->
                dialog.create().hide()
            }
        dialog.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}