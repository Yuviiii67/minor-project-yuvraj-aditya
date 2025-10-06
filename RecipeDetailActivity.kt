package com.example.mealscale.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mealscale.R
import com.example.mealscale.data.DatabaseHelper
import com.example.mealscale.model.Ingredient
import com.example.mealscale.model.Recipe
import com.example.mealscale.model.Step
import com.example.mealscale.adapters.IngredientAdapter
import com.example.mealscale.adapters.StepAdapter

class RecipeDetailActivity : AppCompatActivity() {
    
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recipeNameTextView: TextView
    private lateinit var recipeDescriptionTextView: TextView
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var startCookingButton: Button
    private lateinit var scaleIngredientsButton: Button
    
    private var recipeId: Int = -1
    private var recipe: Recipe? = null
    private val ingredients: MutableList<Ingredient> = mutableListOf()
    private val steps: MutableList<Step> = mutableListOf()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
        
        initializeViews()
        setupDatabase()
        getIntentData()
        loadRecipeDetails()
        setupRecyclerViews()
        setupButtons()
    }
    
    private fun initializeViews() {
        recipeNameTextView = findViewById(R.id.recipeNameTextView)
        recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView)
        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView)
        stepsRecyclerView = findViewById(R.id.stepsRecyclerView)
        startCookingButton = findViewById(R.id.startCookingButton)
        scaleIngredientsButton = findViewById(R.id.scaleIngredientsButton)
    }
    
    private fun setupDatabase() {
        dbHelper = DatabaseHelper(this)
    }
    
    private fun getIntentData() {
        recipeId = intent.getIntExtra("recipe_id", -1)
        val recipeName = intent.getStringExtra("recipe_name") ?: "Recipe Details"
        
        supportActionBar?.title = recipeName
        recipeNameTextView.text = recipeName
    }
    
    private fun loadRecipeDetails() {
        if (recipeId == -1) return
        
        val db = dbHelper.readableDatabase
        
        // Load recipe basic info
        val recipeCursor = db.rawQuery(
            "SELECT * FROM Recipes WHERE RecipeID = ?", 
            arrayOf(recipeId.toString())
        )
        
        recipeCursor.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex("Name")
                val descIndex = cursor.getColumnIndex("Description")
                
                val name = cursor.getString(nameIndex)
                val description = cursor.getString(descIndex)
                
                recipe = Recipe(recipeId, name, description)
                recipeDescriptionTextView.text = description
            }
        }
        
        // Load ingredients
        val ingredientCursor = db.rawQuery(
            "SELECT * FROM Ingredients WHERE RecipeID = ? ORDER BY IngredientID",
            arrayOf(recipeId.toString())
        )
        
        ingredients.clear()
        ingredientCursor.use { cursor ->
            val idIndex = cursor.getColumnIndex("IngredientID")
            val nameIndex = cursor.getColumnIndex("Name")
            val quantityIndex = cursor.getColumnIndex("Quantity")
            
            while (cursor.moveToNext()) {
                val id = cursor.getInt(idIndex)
                val name = cursor.getString(nameIndex)
                val quantity = cursor.getDouble(quantityIndex)
                
                ingredients.add(Ingredient(id, recipeId, name, quantity))
            }
        }
        
        // Load steps
        val stepCursor = db.rawQuery(
            "SELECT * FROM Steps WHERE RecipeID = ? ORDER BY StepID",
            arrayOf(recipeId.toString())
        )
        
        steps.clear()
        stepCursor.use { cursor ->
            val idIndex = cursor.getColumnIndex("StepID")
            val descIndex = cursor.getColumnIndex("Description")
            val durationIndex = cursor.getColumnIndex("Duration")
            
            var stepNumber = 1
            while (cursor.moveToNext()) {
                val id = cursor.getInt(idIndex)
                val description = cursor.getString(descIndex)
                val duration = cursor.getInt(durationIndex)
                
                steps.add(Step(id, recipeId, stepNumber, description, duration, duration > 0))
                stepNumber++
            }
        }
    }
    
    private fun setupRecyclerViews() {
        // Ingredients RecyclerView
        val ingredientAdapter = IngredientAdapter(ingredients)
        ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecipeDetailActivity)
            adapter = ingredientAdapter
            setHasFixedSize(true)
        }
        
        // Steps RecyclerView
        val stepAdapter = StepAdapter(steps) { step ->
            // Handle step click if needed
        }
        stepsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecipeDetailActivity)
            adapter = stepAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupButtons() {
        startCookingButton.setOnClickListener {
            if (steps.isNotEmpty()) {
                val intent = Intent(this, StepGuideActivity::class.java).apply {
                    putExtra("recipe_id", recipeId)
                    putExtra("recipe_name", recipe?.name ?: "Recipe")
                }
                startActivity(intent)
            }
        }
        
        scaleIngredientsButton.setOnClickListener {
            // Navigate to ingredient scaling activity
            val intent = Intent(this, IngredientInputActivity::class.java).apply {
                putExtra("recipe_id", recipeId)
                putExtra("recipe_name", recipe?.name ?: "Recipe")
            }
            startActivity(intent)
        }
    }
}
