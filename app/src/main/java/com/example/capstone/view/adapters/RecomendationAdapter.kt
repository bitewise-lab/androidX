package com.example.capstone.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.data.remote.response.RecommendationsItem
import com.example.capstone.databinding.ItemFoodRecomendationBinding

class RecomendationAdapter : ListAdapter<RecommendationsItem, RecomendationAdapter.MyViewHolder>(DIFF_CALLBACKS) {

    class MyViewHolder(private val binding: ItemFoodRecomendationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RecommendationsItem) {
            binding.txtNameTitle.text = data.healthyPackagedFoodBrands
            binding.txtSugarValue.text = data.sugar
            binding.txtFatValue.text = data.fat
            binding.txtCarboValue.text = data.carbo
            binding.txtProteinValue.text = data.protein

            when(data.category) {
                "karbohidrat kompleks" -> binding.imgMeal.setImageResource(com.example.capstone.R.drawable.karbohidrat_kompleks)
                "protein" -> binding.imgMeal.setImageResource(com.example.capstone.R.drawable.protein)
                "sayuran hijau" -> binding.imgMeal.setImageResource(com.example.capstone.R.drawable.sayuran_hijau)
                "buah" -> binding.imgMeal.setImageResource(com.example.capstone.R.drawable.buah)
                "sumber serat" -> binding.imgMeal.setImageResource(com.example.capstone.R.drawable.sumber_serat)
                "sumber kalsium" -> binding.imgMeal.setImageResource(com.example.capstone.R.drawable.sumber_kalsium)
                "minuman sehat" -> binding.imgMeal.setImageResource(com.example.capstone.R.drawable.minuman_sehat)
                else -> binding.imgMeal.setImageResource(com.example.capstone.R.drawable.makanan_sehat)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemFoodRecomendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val postItem = getItem(position)
        holder.bind(postItem)
    }

    companion object {
        val DIFF_CALLBACKS = object : DiffUtil.ItemCallback<RecommendationsItem>() {
            override fun areItemsTheSame(oldItem: RecommendationsItem, newItem: RecommendationsItem): Boolean {
                return oldItem.healthyPackagedFoodBrands == newItem.healthyPackagedFoodBrands
            }

            override fun areContentsTheSame(oldItem: RecommendationsItem, newItem: RecommendationsItem): Boolean {
                return oldItem.healthyPackagedFoodBrands == newItem.healthyPackagedFoodBrands
            }
        }
    }
}