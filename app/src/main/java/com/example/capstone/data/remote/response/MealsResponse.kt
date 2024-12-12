package com.example.capstone.data.remote.response

data class MealsResponse(
    val mealsId: String ?= "",
    val mealsName: String = "",
    val mealsCalories: String = "",
    val date: String = "",
    val user: String = "",
)
