package com.dronios777.myreddit.view.onboarding

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dronios777.myreddit.NUMBER_SCREENS_ONBOARDING
import com.dronios777.myreddit.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    @SuppressLint("DiscouragedApi")
    fun initOnboarding(): List<OnBoardingItemModel> {

        val list = mutableListOf<OnBoardingItemModel>()
        //массив заголовков для экранов онбординга
        val listTitles =
            getApplication<Application>().resources.getStringArray(R.array.onboarding_titles_array)
        //массив описаний для экранов онбординга
        val listDescription =
            getApplication<Application>().resources.getStringArray(R.array.onboarding_description_array)

        //формирую экраны онбординга
        for (i in 0 until NUMBER_SCREENS_ONBOARDING) {
            list += OnBoardingItemModel(
                image = getApplication<Application>().resources.getIdentifier(
                    "onboarding_image$i", "drawable",
                    getApplication<Application>().packageName
                ),
                title = listTitles[i],
                description = listDescription[i]
            )
        }
        return list
    }
}