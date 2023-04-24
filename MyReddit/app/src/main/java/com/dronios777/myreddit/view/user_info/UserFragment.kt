package com.dronios777.myreddit.view.user_info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dronios777.myreddit.R
import com.dronios777.myreddit.databinding.FragmentUserBinding
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Date
import java.sql.Timestamp

@AndroidEntryPoint
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserInfo() //информация об пользователе
        settingButton()//нстройка кнопки btnAddFriend
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun getUserInfo() {
        lifecycleScope.launchWhenStarted {
            viewModel.userInfo.collect { user ->
                if (user != null) {

                    //устанавливаем иконку
                    val icon = if (user.snoovatarImg != "")
                        user.snoovatarImg
                    else
                        user.iconImg

                    Glide
                        .with(binding.imgProfile.context)
                        .load(icon)
                        .circleCrop()
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .into(binding.imgProfile)

                    binding.nameTxt.text = user.name
                    val stamp = Timestamp(user.account_creation_date!!)
                    binding.dateCreationTxt.text = "${Date(stamp.time * 1000)}"
                    binding.valueKarmaPubTxt.text = user.publication_karma.toString()
                    binding.valueKarmaComTxt.text = user.comment_karma.toString()
                    binding.valueKarmaPhilTxt.text = user.awarder_karma.toString()
                    binding.valueKarmaRecipTxt.text = user.awardee_karma.toString()
                }
            }
        }
    }

    private fun settingButton() {
        viewModel.parsingFriends()//узнаём есть ли в друзьях открытый пользователь

        lifecycleScope.launchWhenStarted {
            viewModel.isFriend.collect { isFriend ->
                if (isFriend) //если уже в друзьях
                {
                    binding.btnAddFriend.text = getText(R.string.remove_friend)
                    binding.btnAddFriend.setOnClickListener { viewModel.removeFriend() }
                } else {
                    binding.btnAddFriend.text = getText(R.string.add_a_friend)
                    binding.btnAddFriend.setOnClickListener { viewModel.addToFriends() }
                }
            }
        }
    }

}