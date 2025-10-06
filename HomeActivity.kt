package com.example.mealscale.ui

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mealscale.R
import com.example.mealscale.adapters.RecipeAdapter
import com.example.mealscale.data.DatabaseHelper
import com.example.mealscale.model.Recipe

class HomeActivity : AppCompatActivity() {
    
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var recipeAdapter: RecipeAdapter
    
    private var allRecipes: MutableList<Recipe> = mutableListOf()
    private var filteredRecipes: MutableList<Recipe> = mutableListOf()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        initializeViews()
        setupDatabase()
        setupRecyclerView()
        setupSearchView()
        loadRecipesFromDatabase()
    }
    
    private fun initializeViews() {
        recipeRecyclerView = findViewById(R.id.recipeRecyclerView)
        searchView = findViewById(R.id.searchView)
    }
    
    private fun setupDatabase() {
        dbHelper = DatabaseHelper(this)
    }
    
    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(filteredRecipes) { recipe ->
            // Handle recipe click - navigate to detail screen
            navigateToRecipeDetails(recipe)
        }
        
        recipeRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = recipeAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search submit if needed
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                filterRecipes(newText ?: "")
                return true
            }
        })
        
        // Set search hint
        searchView.queryHint = "Search recipes..."
    }
    
    private fun loadRecipesFromDatabase() {
        try {
            // Clear existing data
            allRecipes.clear()
            filteredRecipes.clear()
            
            // Get recipes from database
            val recipes = dbHelper.getAllRecipes()
            
            if (recipes.isNotEmpty()) {
                allRecipes.addAll(recipes)
                filteredRecipes.addAll(recipes)
                
                // Update UI on main thread
                runOnUiThread {
                    recipeAdapter.notifyDataSetChanged()
                    updateEmptyState()
                }
            } else {
                // No recipes found - this shouldn't happen with sample data
                showEmptyState("No recipes found. Sample data may not be loaded.")
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            showEmptyState("Error loading recipes: ${e.message}")
        }
    }
    
    private fun filterRecipes(query: String) {
        filteredRecipes.clear()
        
        if (query.isEmpty()) {
            // Show all recipes if search is empty
            filteredRecipes.addAll(allRecipes)
        } else {
            // Filter recipes based on search query
            val lowerCaseQuery = query.lowercase()
            allRecipes.forEach { recipe ->
                if (recipe.name.lowercase().contains(lowerCaseQuery) ||
                    recipe.description.lowercase().contains(lowerCaseQuery) ||
                    recipe.category.lowercase().contains(lowerCaseQuery)) {
                    filteredRecipes.add(recipe)
                }
            }
        }
        
        recipeAdapter.notifyDataSetChanged()
        updateEmptyState()
    }
    
    private fun navigateToRecipeDetails(recipe: Recipe) {
        val intent = Intent(this, RecipeDetailActivity::class.java).apply {
            putExtra("recipe_id", recipe.id)
            putExtra("recipe_name", recipe.name)
            putExtra("recipe_description", recipe.description)
        }
        startActivity(intent)
        
        // Add slide animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    
    private fun updateEmptyState() {
        // You can implement this if you have an empty state view
        // For now, the RecyclerView will just show nothing
        if (filteredRecipes.isEmpty()) {
            // Show empty state message
            // emptyStateTextView.visibility = View.VISIBLE
            // recipeRecyclerView.visibility = View.GONE
        } else {
            // Show recipe list
            // emptyStateTextView.visibility = View.GONE
            // recipeRecyclerView.visibility = View.VISIBLE
        }
    }
    
    private fun showEmptyState(message: String) {
        // You can show a Toast or update a TextView
        // Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data if needed (e.g., after adding new recipes)
        // loadRecipesFromDatabase()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Close database connection if needed
        // dbHelper.close()
    }
}
