package com.dronios777.myreddit.view.authorization

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dronios777.myreddit.PATH
import com.dronios777.myreddit.R
import com.dronios777.myreddit.databinding.FragmentAuthBinding
import com.dronios777.myreddit.state.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()
    private val args: AuthFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //если у пользователя есть токен, то пропускаем фрагмент с авторизацией
        val token = viewModel.getTokenFromSharedPreference()
        if (token != null) {
            Log.d("MyLog", "token from storage: $token")
            openSubredditsFragment()
        } else {
            startAuth() //открываем авторизацию
            saveToken() //сохраняем токен
            loadingObserve()  //слушатель состояний
            viewModel.createToken(args.code) //создаём токен
        }
    }

    private fun startAuth() {
        binding.btnAuth.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PATH))
            startActivity(intent)
            Log.d("MyLog", "btnAuth_click")
        }
    }

    private fun saveToken() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.token.collect { token ->
                viewModel.saveTokenToSharedPreference(token)//сохраняем токен
                Log.d("MyLog", "token: $token")
            }
        }
    }

    private fun loadingObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collect { loadState ->
                when (loadState) {
                    State.START ->
                        setLoadState(
                            buttonIsEnabled = true,
                            textIsVisible = false,
                            progressIsVisible = false
                        )
                    State.LOADING -> setLoadState(
                        buttonIsEnabled = false,
                        textIsVisible = false,
                        progressIsVisible = true
                    )
                    State.SUCCESS -> {
                        setLoadState(
                            buttonIsEnabled = false,
                            textIsVisible = true,
                            progressIsVisible = false
                        )
                        openSubredditsFragment()
                    }
                    State.ERROR -> {
                        setLoadState(
                            buttonIsEnabled = true,
                            textIsVisible = true,
                            progressIsVisible = false
                        )
                        binding.text.text = loadState.message
                    }
                }
            }
        }
    }

    private fun setLoadState(
        buttonIsEnabled: Boolean,
        textIsVisible: Boolean,
        progressIsVisible: Boolean
    ) {
        binding.btnAuth.isEnabled = buttonIsEnabled
        binding.text.isVisible = textIsVisible
        binding.progressBar.isVisible = progressIsVisible
    }

    private fun openSubredditsFragment() = findNavController().navigate(R.id.subredditsFragment)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}