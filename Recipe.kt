package com.example.mealscale.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int,
    val name: String,
    val description: String,
    val category: String = "Main Course",
    val cookingTime: Int = 0, // in minutes
    val difficulty: String = "Easy",
    val imageUrl: String? = null
) : Parcelable {
    
    companion object {
        const val CATEGORY_STARTER = "Starter"
        const val CATEGORY_MAIN_COURSE = "Main Course"
        const val CATEGORY_DESSERT = "Dessert"
        const val CATEGORY_DRINK = "Drink"
        
        const val DIFFICULTY_EASY = "Easy"
        const val DIFFICULTY_MEDIUM = "Medium"
        const val DIFFICULTY_HARD = "Hard"
    }
    
    fun getFormattedCookingTime(): String {
        return if (cookingTime < 60) {
            "${cookingTime} mins"
        } else {
            "${cookingTime / 60} hr ${cookingTime % 60} mins"
        }
    }
}
