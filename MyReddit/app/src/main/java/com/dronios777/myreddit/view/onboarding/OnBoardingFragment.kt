package com.dronios777.myreddit.view.onboarding

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.dronios777.myreddit.R
import com.dronios777.myreddit.databinding.FragmentOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnBoardingItems() //настройка онбординга
        onPageScrolled() //настройка текста кнопки "Skip" при листании страниц
        binding.skip.setOnClickListener { click() } //нажатие на текст "Skip"
        binding.btnSkip.setOnClickListener { click() }//нажатие на кнопку "Skip"
    }

    @SuppressLint("DiscouragedApi")
    private fun setOnBoardingItems() {

        binding.apply {
            onboardingViewPager.setPageTransformer(CubeTransformer())//подключение анимации
            onboardingViewPager.adapter = OnBoardingAdapter(viewModel.initOnboarding())
            //для совместной (синхронной) работы TabLayout и ViewPager2
            TabLayoutMediator(tabs, onboardingViewPager) { _, _ -> }.attach()
        }
    }

    private fun onPageScrolled() {
        binding.apply {
            onboardingViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)

                    if (position + 1 < onboardingViewPager.adapter!!.itemCount) {
                        skip.text = resources.getString(R.string.skip)
                        btnSkip.text = resources.getString(R.string.skip)
                    } else {
                        skip.text = resources.getString(R.string.ready)
                        btnSkip.text = resources.getString(R.string.ready)
                    }
                }
            }
            )
        }
    }

    private fun click() {
        with(binding) {
            //получаем текущую страницу
            var currentItem = onboardingViewPager.currentItem

            if (++currentItem < onboardingViewPager.adapter!!.itemCount) {
                onboardingViewPager.currentItem++
                skip.text = resources.getString(R.string.skip)
                btnSkip.text = resources.getString(R.string.skip)
            }
            if (currentItem + 1 == onboardingViewPager.adapter!!.itemCount) {
                skip.text = resources.getString(R.string.ready)
                btnSkip.text = resources.getString(R.string.ready)
            }
            if (currentItem + 1 > onboardingViewPager.adapter!!.itemCount) {
                findNavController().navigate(R.id.action_onBoardingFragment_to_authFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}