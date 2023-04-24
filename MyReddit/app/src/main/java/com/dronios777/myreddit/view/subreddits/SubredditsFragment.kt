package com.dronios777.myreddit.view.subreddits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dronios777.myreddit.*
import com.dronios777.myreddit.data.model.subreddit.ResponseSubreddits
import com.dronios777.myreddit.databinding.FragmentSubredditsBinding
import com.dronios777.myreddit.state.ClickSubreddit
import com.dronios777.myreddit.view.posts.SubredditPostsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class SubredditsFragment : Fragment() {

    private var _binding: FragmentSubredditsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubredditsViewModel by activityViewModels()
    private val viewModelSubredditPosts: SubredditPostsViewModel by activityViewModels()

    private val subredditsAdapter by lazy {
        SubListAdapter { state, item -> onItemClick(state, item) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubredditsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshSubreddits()//слушатель для обновления сабреддитов
        toggleButtonGroupListener()//слушатель переключения кнопок (New/Popular)
        showHideLoadingIndicator() //слушатель индикатора загрузки
        getItemSubreddit()//получение списка сабреддитов адаптером
        performClick() //щелчок по кнопке
        searchSubreddits()//слушатель поиска сабреддитов
        errorObserve()//слушатель ошибок
    }

    private fun errorObserve() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isError.collect { error ->
                if (error) binding.error.visibility = View.VISIBLE
                else binding.error.visibility = View.GONE
            }
        }
    }

    private fun searchSubreddits() {
        binding.searchSubreddits.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            //нажития на ввод или кнопку поиска
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchSubreddits.clearFocus()
                if (query != null && query.trim() != "") {
                    viewModel.search("$query")
                }
                return true
            }

            //поиск идёт во время набора теста (не реализую согласно моей логике)
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun performClick() {
        with(binding) {
            if (viewModel.subWhere.value == NEW)
                btnNew.performClick() else btnPopular.performClick()
        }
    }

    private fun getItemSubreddit() {
        binding.rvSubList.adapter = subredditsAdapter
        viewModel.subreddits.onEach {
            subredditsAdapter.submitList(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun showHideLoadingIndicator() {
        viewModel.isLoading.onEach {
            binding.swipeRefresh.isRefreshing = it
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun refreshSubreddits() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun toggleButtonGroupListener() {
        binding.toggleButtonGroup.addOnButtonCheckedListener { toggleButtonGroup, checkedId, isChecked ->

            if (isChecked) {
                when (checkedId) {
                    R.id.btnNew -> {
                        viewModel.subredditPath(NEW)
                        viewModel.refresh()
                    }
                    R.id.btnPopular -> {
                        viewModel.subredditPath(POPULAR)
                        viewModel.refresh()
                    }
                }
            } else {
                if (toggleButtonGroup.checkedButtonId == View.NO_ID) {
                    performClick()
                }
            }
        }
    }

    private fun onItemClick(
        state: ClickSubreddit,
        item: ResponseSubreddits.Data.Children
    ) {
        when (state) {
            //запрашиваем список постов для данного сабреддита
            ClickSubreddit.SUBREDDIT -> {
                viewModelSubredditPosts.getSubredditPosts(item)
                findNavController().navigate(R.id.action_subredditsFragment_to_subredditPostsFragment)
            }
            //вызываем AlertDialog с вопросом о подписке/отписке
            ClickSubreddit.SUBSCRIBE -> setUpAlertDialog(item)
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



