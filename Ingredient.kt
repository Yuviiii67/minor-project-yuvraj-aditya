package com.example.mealscale.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val id: Int,
    val recipeId: Int,
    val name: String,
    val quantity: Double,
    val unit: String = "g", // grams, ml, cups, tsp, etc.
    val stepNumber: Int = 0, // which step this ingredient is added in
    val scaledQuantity: Double = quantity // for dynamic scaling
) : Parcelable {
    
    companion object {
        const val UNIT_GRAMS = "g"
        const val UNIT_KILOGRAMS = "kg"
        const val UNIT_MILLILITERS = "ml"
        const val UNIT_LITERS = "l"
        const val UNIT_CUPS = "cups"
        const val UNIT_TABLESPOON = "tbsp"
        const val UNIT_TEASPOON = "tsp"
        const val UNIT_PIECES = "pcs"
        const val UNIT_PINCH = "pinch"
    }
    
    fun getFormattedQuantity(): String {
        return if (quantity == quantity.toInt().toDouble()) {
            "${quantity.toInt()} $unit"
        } else {
            "%.1f $unit".format(quantity)
        }
    }
    
    fun getScaledFormattedQuantity(): String {
        return if (scaledQuantity == scaledQuantity.toInt().toDouble()) {
            "${scaledQuantity.toInt()} $unit"
        } else {
            "%.1f $unit".format(scaledQuantity)
        }
    }
    
    fun scaleIngredient(scaleFactor: Double): Ingredient {
        return this.copy(scaledQuantity = quantity * scaleFactor)
    }
}
