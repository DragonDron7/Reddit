package com.dronios777.myreddit.view.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dronios777.myreddit.R
import com.dronios777.myreddit.data.storage.Token
import com.dronios777.myreddit.databinding.FragmentMyProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Date
import java.sql.Timestamp

@AndroidEntryPoint
class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickLogoutButton()
        alertDialogObserve()//слушатель состояния  AlertDialog
        getUserInfo() //информация об пользователе
    }


    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun getUserInfo() {
        lifecycleScope.launchWhenStarted {

            viewModel.updateUserInfo()

            viewModel.userInfo.collect { user ->
                if (user != null) {
                    Glide
                        .with(binding.imgProfile.context)
                        .load(user.iconImg)
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
                    binding.friendsCount.text = getString(R.string.friends) + " ${user.numFriends}"
                }
            }
        }
    }

    private fun clickLogoutButton() {
        binding.logoutBar.menu.getItem(0).setOnMenuItemClickListener {
            viewModel.showAlertDialog(true)
            true
        }
    }

    private fun alertDialogObserve() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(R.string.logout_title)
            .setMessage(R.string.logout_message)
            .setIcon(R.drawable.ic_baseline_person_24)
            //выходим
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.showAlertDialog(false)
                dialog.create().hide()
                Token(requireContext()).clearToken() //удаляем токен
                findNavController().apply {
                    navigate(R.id.onBoardingFragment)
                    backQueue.clear()
                }
            }
            //остаёмся
            .setNegativeButton(R.string.no) { _, _ ->
                viewModel.showAlertDialog(false)
                dialog.create().hide()

            }
        dialog.setCancelable(false)//щелчок вне диалогового окна запрещаю
        viewModel.showAlertDialog.observe(activity as LifecycleOwner) { show ->
            if (show)
                dialog.create().show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}