package com.dronios777.myreddit.view.posts.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.dronios777.myreddit.data.model.posts.CommentsResponse.Data.Comments
import com.dronios777.myreddit.databinding.FragmentCommentsBinding
import com.dronios777.myreddit.state.ClickPost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CommentsFragment : Fragment() {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CommentsViewModel by activityViewModels()

    private val commentsAdapter by lazy {
        CommentsAdapter { state, item -> onItemClick(state, item) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshComments()//слушатель для обновления постов
        showHideLoadingIndicator() //слушатель индикатора загрузки
        getItemComments()//получение списка комментариев адаптером


    }

    private fun showHideLoadingIndicator() {
        viewModel.isLoading.onEach {
            binding.swipeRefresh.isRefreshing = it
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun refreshComments() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.postID.value?.let { viewModel.comments(it) }
        }
    }

    private fun getItemComments() {
        //   Toast.makeText(requireContext(),it.toString(),Toast.LENGTH_SHORT).show()

        binding.rvComList000.adapter = commentsAdapter
        viewModel.comments.onEach {
            commentsAdapter.submitList(it)
            //  Log.d("MyLog", "comments777: $it")
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun onItemClick(
        state: ClickPost,
        item: Comments
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
                    //
                }
                ClickPost.POST_AUTHOR -> {
                    //
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}