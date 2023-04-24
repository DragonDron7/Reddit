package com.dronios777.myreddit.view.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dronios777.myreddit.R
import com.dronios777.myreddit.databinding.OnboardingItemContainerBinding

class OnBoardingAdapter(private val listItems: List<OnBoardingItemModel>) :
    RecyclerView.Adapter<OnBoardingAdapter.OnBoardingItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingItemViewHolder {
        val view = LayoutInflater.from(parent.context)//создаём шаблон
            .inflate(R.layout.onboarding_item_container, parent, false)
        return OnBoardingItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnBoardingItemViewHolder, position: Int) {
        holder.bind(listItems[position]) //заполняем в n позиции
    }

    override fun getItemCount(): Int {
        return listItems.size //общее кол-во элементов списка, которые перелистываем
    }

    //хранит ссылки на view
    inner class OnBoardingItemViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = OnboardingItemContainerBinding.bind(item)

        //размечаем шаблон (view)
        fun bind(model: OnBoardingItemModel) = with(binding) {
            imageOnboarding.setImageResource(model.image)
            textTitle.text = model.title
            textDescription.text = model.description
        }
    }
}
