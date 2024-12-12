package com.example.capstone.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.data.remote.response.MealsResponse
import com.example.capstone.databinding.ItemMealsBinding

class MealsAdapter(private var meals: List<MealsResponse>) : RecyclerView.Adapter<MealsAdapter.MealViewHolder>() {

    class MealViewHolder(private val binding: ItemMealsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: MealsResponse) {
            binding.txtMeal.text = meal.mealsName
            binding.txtDate.text = meal.date
            binding.txtCalories.text = "${meal.mealsCalories} Cal"
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        holder.bind(meal)
    }

    override fun getItemCount(): Int {
        return meals.size
    }

    fun updateMeals(newMeals: List<MealsResponse>) {
        meals = newMeals
        notifyDataSetChanged() // Notify adapter about data changes
    }
}