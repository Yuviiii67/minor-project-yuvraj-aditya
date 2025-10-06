package com.example.mealscale.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class SampleData {

    companion object {
        
        fun insertSampleData(db: SQLiteDatabase) {
            insertRecipes(db)
            insertIngredients(db)
            insertSteps(db)
        }

        private fun insertRecipes(db: SQLiteDatabase) {
            // Clear existing data first
            db.delete("Steps", null, null)
            db.delete("Ingredients", null, null)
            db.delete("Recipes", null, null)

            // ---- STARTERS ----
            insertRecipe(db, "Paneer Tikka", "Grilled paneer cubes marinated in spices and yogurt", "Starter", 25, "Medium")
            insertRecipe(db, "Veg Spring Rolls", "Crispy rolls stuffed with fresh vegetables", "Starter", 20, "Easy")
            insertRecipe(db, "Garlic Bread", "Toasted bread with garlic and butter coating", "Starter", 15, "Easy")

            // ---- VEG MAIN COURSES ----
            insertRecipe(db, "Paneer Butter Masala", "Rich creamy tomato gravy with soft paneer cubes", "Main Course", 40, "Medium")
            insertRecipe(db, "Vegetable Biryani", "Aromatic basmati rice cooked with mixed vegetables", "Main Course", 45, "Medium")
            insertRecipe(db, "Palak Paneer", "Paneer cubes cooked in spinach gravy", "Main Course", 35, "Medium")

            // ---- NON-VEG MAIN COURSES ----
            insertRecipe(db, "Butter Chicken", "Creamy tomato gravy chicken curry", "Main Course", 50, "Medium")
            insertRecipe(db, "Chicken Biryani", "Layered rice and chicken cooked with biryani spices", "Main Course", 60, "Hard")

            // ---- DESSERTS ----
            insertRecipe(db, "Gulab Jamun", "Deep-fried milk balls soaked in sugar syrup", "Dessert", 30, "Medium")
            insertRecipe(db, "Chocolate Mousse", "Creamy chocolate dessert with whipped cream", "Dessert", 20, "Easy")

            // ---- DRINKS ----
            insertRecipe(db, "Masala Chai", "Traditional Indian spiced tea with milk", "Drink", 10, "Easy")
            insertRecipe(db, "Mango Smoothie", "Chilled mango and yogurt refreshing drink", "Drink", 5, "Easy")
        }

        private fun insertRecipe(db: SQLiteDatabase, name: String, description: String, category: String, cookingTime: Int, difficulty: String) {
            val values = ContentValues().apply {
                put("Name", name)
                put("Description", description)
                put("Category", category)
                put("CookingTime", cookingTime)
                put("Difficulty", difficulty)
            }
            db.insert("Recipes", null, values)
        }

        private fun insertIngredients(db: SQLiteDatabase) {
            // Ingredients for Paneer Butter Masala (Recipe ID 1)
            insertIngredient(db, 1, "Paneer", 250.0, "g", 1)
            insertIngredient(db, 1, "Tomatoes", 4.0, "pcs", 1)
            insertIngredient(db, 1, "Onions", 2.0, "pcs", 1)
            insertIngredient(db, 1, "Butter", 50.0, "g", 1)
            insertIngredient(db, 1, "Cream", 100.0, "ml", 6)
            insertIngredient(db, 1, "Ginger Garlic Paste", 2.0, "tsp", 2)
            insertIngredient(db, 1, "Garam Masala", 1.0, "tsp", 5)
            insertIngredient(db, 1, "Red Chilli Powder", 1.0, "tsp", 5)
            insertIngredient(db, 1, "Salt", 1.5, "tsp", 1)

            // Ingredients for Masala Chai (Recipe ID 10)
            insertIngredient(db, 10, "Water", 200.0, "ml", 1)
            insertIngredient(db, 10, "Milk", 100.0, "ml", 4)
            insertIngredient(db, 10, "Tea Leaves", 2.0, "tsp", 3)
            insertIngredient(db, 10, "Sugar", 2.0, "tsp", 4)
            insertIngredient(db, 10, "Ginger", 1.0, "inch", 1)
            insertIngredient(db, 10, "Cardamom", 2.0, "pods", 1)
        }

        private fun insertIngredient(db: SQLiteDatabase, recipeId: Int, name: String, quantity: Double, unit: String, stepNumber: Int) {
            val values = ContentValues().apply {
                put("RecipeID", recipeId)
                put("Name", name)
                put("Quantity", quantity)
                put("Unit", unit)
                put("StepNumber", stepNumber)
            }
            db.insert("Ingredients", null, values)
        }

        private fun insertSteps(db: SQLiteDatabase) {
            // Steps for Paneer Butter Masala (Recipe ID 1)
            insertStep(db, 1, 1, "Chop tomatoes and onions finely. Cut paneer into cubes.", 0, false)
            insertStep(db, 1, 2, "Heat butter in a pan, sauté onions until golden brown.", 180, true)
            insertStep(db, 1, 3, "Add ginger-garlic paste and cook for 2 minutes.", 120, true)
            insertStep(db, 1, 4, "Add tomatoes and cook until soft and mushy.", 300, true)
            insertStep(db, 1, 5, "Add all spices and cook for 2 minutes.", 120, true)
            insertStep(db, 1, 6, "Add paneer cubes and cream, simmer for 5 minutes.", 300, true)
            insertStep(db, 1, 7, "Garnish with fresh cream and serve hot.", 0, false)

            // Steps for Masala Chai (Recipe ID 10)
            insertStep(db, 10, 1, "Crush ginger and cardamom lightly.", 0, false)
            insertStep(db, 10, 2, "Boil water with ginger and cardamom for 3 minutes.", 180, true)
            insertStep(db, 10, 3, "Add tea leaves and boil for 2 minutes.", 120, true)
            insertStep(db, 10, 4, "Add milk and sugar, bring to boil.", 180, true)
            insertStep(db, 10, 5, "Simmer for 2 minutes and strain.", 120, true)
        }

        private fun insertStep(db: SQLiteDatabase, recipeId: Int, stepNumber: Int, description: String, duration: Int, hasTimer: Boolean) {
            val values = ContentValues().apply {
                put("RecipeID", recipeId)
                put("StepNumber", stepNumber)
                put("Description", description)
                put("Duration", duration)
                put("HasTimer", if (hasTimer) 1 else 0)
            }
            db.insert("Steps", null, values)
        }
    }
}
