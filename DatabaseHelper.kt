package com.example.mealscale.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mealscale.model.Ingredient
import com.example.mealscale.model.Recipe
import com.example.mealscale.model.Step

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MealScaleDB"
        private const val DATABASE_VERSION = 1

        // Table Names
        const val TABLE_RECIPES = "Recipes"
        const val TABLE_INGREDIENTS = "Ingredients"
        const val TABLE_STEPS = "Steps"

        // Common Column Names
        const val KEY_ID = "ID"
        const val KEY_RECIPE_ID = "RecipeID"

        // Recipes Table Columns
        const val KEY_RECIPE_NAME = "Name"
        const val KEY_RECIPE_DESCRIPTION = "Description"
        const val KEY_CATEGORY = "Category"
        const val KEY_COOKING_TIME = "CookingTime"
        const val KEY_DIFFICULTY = "Difficulty"

        // Ingredients Table Columns
        const val KEY_INGREDIENT_NAME = "Name"
        const val KEY_QUANTITY = "Quantity"
        const val KEY_UNIT = "Unit"
        const val KEY_STEP_NUMBER = "StepNumber"

        // Steps Table Columns
        const val KEY_STEP_DESCRIPTION = "Description"
        const val KEY_DURATION = "Duration"
        const val KEY_STEP_NUMBER = "StepNumber"
        const val KEY_HAS_TIMER = "HasTimer"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Recipes Table
        val CREATE_RECIPES_TABLE = """
            CREATE TABLE $TABLE_RECIPES (
                ${getRecipeIdColumn()} INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_RECIPE_NAME TEXT NOT NULL,
                $KEY_RECIPE_DESCRIPTION TEXT,
                $KEY_CATEGORY TEXT DEFAULT 'Main Course',
                $KEY_COOKING_TIME INTEGER DEFAULT 0,
                $KEY_DIFFICULTY TEXT DEFAULT 'Easy'
            )
        """.trimIndent()

        // Create Ingredients Table
        val CREATE_INGREDIENTS_TABLE = """
            CREATE TABLE $TABLE_INGREDIENTS (
                IngredientID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_RECIPE_ID INTEGER NOT NULL,
                $KEY_INGREDIENT_NAME TEXT NOT NULL,
                $KEY_QUANTITY REAL NOT NULL,
                $KEY_UNIT TEXT DEFAULT 'g',
                $KEY_STEP_NUMBER INTEGER DEFAULT 0,
                FOREIGN KEY($KEY_RECIPE_ID) REFERENCES $TABLE_RECIPES(${getRecipeIdColumn()})
            )
        """.trimIndent()

        // Create Steps Table
        val CREATE_STEPS_TABLE = """
            CREATE TABLE $TABLE_STEPS (
                StepID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_RECIPE_ID INTEGER NOT NULL,
                $KEY_STEP_NUMBER INTEGER NOT NULL,
                $KEY_STEP_DESCRIPTION TEXT NOT NULL,
                $KEY_DURATION INTEGER DEFAULT 0,
                $KEY_HAS_TIMER INTEGER DEFAULT 0,
                FOREIGN KEY($KEY_RECIPE_ID) REFERENCES $TABLE_RECIPES(${getRecipeIdColumn()})
            )
        """.trimIndent()

        // Execute table creation
        db.execSQL(CREATE_RECIPES_TABLE)
        db.execSQL(CREATE_INGREDIENTS_TABLE)
        db.execSQL(CREATE_STEPS_TABLE)

        // Populate with sample data
        populateSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STEPS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INGREDIENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPES")
        
        // Create tables again
        onCreate(db)
    }

    // Recipe Operations
    fun addRecipe(recipe: Recipe): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_RECIPE_NAME, recipe.name)
            put(KEY_RECIPE_DESCRIPTION, recipe.description)
            put(KEY_CATEGORY, recipe.category)
            put(KEY_COOKING_TIME, recipe.cookingTime)
            put(KEY_DIFFICULTY, recipe.difficulty)
        }
        return db.insert(TABLE_RECIPES, null, values)
    }

    fun getAllRecipes(): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        val db = readableDatabase
        
        val cursor: Cursor = db.query(
            TABLE_RECIPES,
            arrayOf(getRecipeIdColumn(), KEY_RECIPE_NAME, KEY_RECIPE_DESCRIPTION, KEY_CATEGORY, KEY_COOKING_TIME, KEY_DIFFICULTY),
            null, null, null, null, "$KEY_RECIPE_NAME ASC"
        )

        cursor.use {
            val idIndex = it.getColumnIndex(getRecipeIdColumn())
            val nameIndex = it.getColumnIndex(KEY_RECIPE_NAME)
            val descIndex = it.getColumnIndex(KEY_RECIPE_DESCRIPTION)
            val categoryIndex = it.getColumnIndex(KEY_CATEGORY)
            val timeIndex = it.getColumnIndex(KEY_COOKING_TIME)
            val difficultyIndex = it.getColumnIndex(KEY_DIFFICULTY)

            while (it.moveToNext()) {
                val recipe = Recipe(
                    id = it.getInt(idIndex),
                    name = it.getString(nameIndex),
                    description = it.getString(descIndex),
                    category = it.getString(categoryIndex),
                    cookingTime = it.getInt(timeIndex),
                    difficulty = it.getString(difficultyIndex)
                )
                recipes.add(recipe)
            }
        }
        return recipes
    }

    fun getRecipeById(recipeId: Int): Recipe? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_RECIPES,
            arrayOf(getRecipeIdColumn(), KEY_RECIPE_NAME, KEY_RECIPE_DESCRIPTION, KEY_CATEGORY, KEY_COOKING_TIME, KEY_DIFFICULTY),
            "${getRecipeIdColumn()} = ?",
            arrayOf(recipeId.toString()),
            null, null, null
        )

        cursor.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(getRecipeIdColumn())
                val nameIndex = it.getColumnIndex(KEY_RECIPE_NAME)
                val descIndex = it.getColumnIndex(KEY_RECIPE_DESCRIPTION)
                val categoryIndex = it.getColumnIndex(KEY_CATEGORY)
                val timeIndex = it.getColumnIndex(KEY_COOKING_TIME)
                val difficultyIndex = it.getColumnIndex(KEY_DIFFICULTY)

                return Recipe(
                    id = it.getInt(idIndex),
                    name = it.getString(nameIndex),
                    description = it.getString(descIndex),
                    category = it.getString(categoryIndex),
                    cookingTime = it.getInt(timeIndex),
                    difficulty = it.getString(difficultyIndex)
                )
            }
        }
        return null
    }

    fun searchRecipes(query: String): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        val db = readableDatabase
        
        val selection = "$KEY_RECIPE_NAME LIKE ? OR $KEY_RECIPE_DESCRIPTION LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%")
        
        val cursor = db.query(
            TABLE_RECIPES,
            arrayOf(getRecipeIdColumn(), KEY_RECIPE_NAME, KEY_RECIPE_DESCRIPTION, KEY_CATEGORY, KEY_COOKING_TIME, KEY_DIFFICULTY),
            selection, selectionArgs, null, null, "$KEY_RECIPE_NAME ASC"
        )

        cursor.use {
            val idIndex = it.getColumnIndex(getRecipeIdColumn())
            val nameIndex = it.getColumnIndex(KEY_RECIPE_NAME)
            val descIndex = it.getColumnIndex(KEY_RECIPE_DESCRIPTION)
            val categoryIndex = it.getColumnIndex(KEY_CATEGORY)
            val timeIndex = it.getColumnIndex(KEY_COOKING_TIME)
            val difficultyIndex = it.getColumnIndex(KEY_DIFFICULTY)

            while (it.moveToNext()) {
                val recipe = Recipe(
                    id = it.getInt(idIndex),
                    name = it.getString(nameIndex),
                    description = it.getString(descIndex),
                    category = it.getString(categoryIndex),
                    cookingTime = it.getInt(timeIndex),
                    difficulty = it.getString(difficultyIndex)
                )
                recipes.add(recipe)
            }
        }
        return recipes
    }

    // Ingredient Operations
    fun getIngredientsForRecipe(recipeId: Int): List<Ingredient> {
        val ingredients = mutableListOf<Ingredient>()
        val db = readableDatabase
        
        val cursor = db.query(
            TABLE_INGREDIENTS,
            arrayOf("IngredientID", KEY_RECIPE_ID, KEY_INGREDIENT_NAME, KEY_QUANTITY, KEY_UNIT, KEY_STEP_NUMBER),
            "$KEY_RECIPE_ID = ?",
            arrayOf(recipeId.toString()),
            null, null, "IngredientID ASC"
        )

        cursor.use {
            val idIndex = it.getColumnIndex("IngredientID")
            val recipeIdIndex = it.getColumnIndex(KEY_RECIPE_ID)
            val nameIndex = it.getColumnIndex(KEY_INGREDIENT_NAME)
            val quantityIndex = it.getColumnIndex(KEY_QUANTITY)
            val unitIndex = it.getColumnIndex(KEY_UNIT)
            val stepIndex = it.getColumnIndex(KEY_STEP_NUMBER)

            while (it.moveToNext()) {
                val ingredient = Ingredient(
                    id = it.getInt(idIndex),
                    recipeId = it.getInt(recipeIdIndex),
                    name = it.getString(nameIndex),
                    quantity = it.getDouble(quantityIndex),
                    unit = it.getString(unitIndex),
                    stepNumber = it.getInt(stepIndex)
                )
                ingredients.add(ingredient)
            }
        }
        return ingredients
    }

    fun addIngredient(ingredient: Ingredient): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_RECIPE_ID, ingredient.recipeId)
            put(KEY_INGREDIENT_NAME, ingredient.name)
            put(KEY_QUANTITY, ingredient.quantity)
            put(KEY_UNIT, ingredient.unit)
            put(KEY_STEP_NUMBER, ingredient.stepNumber)
        }
        return db.insert(TABLE_INGREDIENTS, null, values)
    }

    // Step Operations
    fun getStepsForRecipe(recipeId: Int): List<Step> {
        val steps = mutableListOf<Step>()
        val db = readableDatabase
        
        val cursor = db.query(
            TABLE_STEPS,
            arrayOf("StepID", KEY_RECIPE_ID, KEY_STEP_NUMBER, KEY_STEP_DESCRIPTION, KEY_DURATION, KEY_HAS_TIMER),
            "$KEY_RECIPE_ID = ?",
            arrayOf(recipeId.toString()),
            null, null, "$KEY_STEP_NUMBER ASC"
        )

        cursor.use {
            val idIndex = it.getColumnIndex("StepID")
            val recipeIdIndex = it.getColumnIndex(KEY_RECIPE_ID)
            val stepNumberIndex = it.getColumnIndex(KEY_STEP_NUMBER)
            val descIndex = it.getColumnIndex(KEY_STEP_DESCRIPTION)
            val durationIndex = it.getColumnIndex(KEY_DURATION)
            val hasTimerIndex = it.getColumnIndex(KEY_HAS_TIMER)

            while (it.moveToNext()) {
                val step = Step(
                    id = it.getInt(idIndex),
                    recipeId = it.getInt(recipeIdIndex),
                    stepNumber = it.getInt(stepNumberIndex),
                    description = it.getString(descIndex),
                    duration = it.getInt(durationIndex),
                    hasTimer = it.getInt(hasTimerIndex) == 1
                )
                steps.add(step)
            }
        }
        return steps
    }

    fun addStep(step: Step): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_RECIPE_ID, step.recipeId)
            put(KEY_STEP_NUMBER, step.stepNumber)
            put(KEY_STEP_DESCRIPTION, step.description)
            put(KEY_DURATION, step.duration)
            put(KEY_HAS_TIMER, if (step.hasTimer) 1 else 0)
        }
        return db.insert(TABLE_STEPS, null, values)
    }

    // Utility Methods
    fun getTotalCookingTime(recipeId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM($KEY_DURATION) as total FROM $TABLE_STEPS WHERE $KEY_RECIPE_ID = ?",
            arrayOf(recipeId.toString())
        )

        return cursor.use {
            if (it.moveToFirst()) {
                it.getInt(it.getColumnIndexOrThrow("total"))
            } else {
                0
            }
        }
    }

    fun getRecipeCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_RECIPES", null)
        
        return cursor.use {
            if (it.moveToFirst()) {
                it.getInt(0)
            } else {
                0
            }
        }
    }

    // Sample Data Population
    private fun populateSampleData(db: SQLiteDatabase) {
        SampleData.insertSampleData(db)
    }

    // Helper method to handle column name differences
    private fun getRecipeIdColumn(): String {
        return "RecipeID"
    }
}

// Extension function for Cursor safety
fun Cursor.getColumnIndexOrThrow(columnName: String): Int {
    val index = getColumnIndex(columnName)
    if (index == -1) {
        throw IllegalArgumentException("Column $columnName does not exist")
    }
    return index
}
