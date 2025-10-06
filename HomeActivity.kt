package com.example.mealscale.ui

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mealscale.R
import com.example.mealscale.data.DatabaseHelper
import com.example.mealscale.model.Recipe
import com.example.mealscale.adapters.RecipeAdapter

class HomeActivity : AppCompatActivity() {
    
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var recipeAdapter: RecipeAdapter
    private var recipeList: MutableList<Recipe> = mutableListOf()
    private var filteredRecipeList: MutableList<Recipe> = mutableListOf()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        initializeViews()
        setupDatabase()
        setupRecyclerView()
        setupSearchView()
        loadRecipes()
    }
    
    private fun initializeViews() {
        recipeRecyclerView = findViewById(R.id.recipeRecyclerView)
        searchView = findViewById(R.id.searchView)
    }
    
    private fun setupDatabase() {
        dbHelper = DatabaseHelper(this)
    }
    
    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(filteredRecipeList) { recipe ->
            // Handle recipe click - navigate to detail screen
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("recipe_id", recipe.id)
                putExtra("recipe_name", recipe.name)
            }
            startActivity(intent)
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
                return false
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                filterRecipes(newText ?: "")
                return true
            }
        })
    }
    
    private fun loadRecipes() {
        // Get recipes from database
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Recipes", null)
        
        recipeList.clear()
        filteredRecipeList.clear()
        
        cursor.use { c ->
            val idIndex = c.getColumnIndex("RecipeID")
            val nameIndex = c.getColumnIndex("Name")
            val descIndex = c.getColumnIndex("Description")
            
            while (c.moveToNext()) {
                val id = c.getInt(idIndex)
                val name = c.getString(nameIndex)
                val description = c.getString(descIndex)
                
                recipeList.add(Recipe(id, name, description))
            }
        }
        
        filteredRecipeList.addAll(recipeList)
        recipeAdapter.notifyDataSetChanged()
        
        // Show empty state if no recipes
        if (recipeList.isEmpty()) {
            // You can show a placeholder text here
        }
    }
    
    private fun filterRecipes(query: String) {
        filteredRecipeList.clear()
        
        if (query.isEmpty()) {
            filteredRecipeList.addAll(recipeList)
        } else {
            val lowerCaseQuery = query.lowercase()
            recipeList.forEach { recipe ->
                if (recipe.name.lowercase().contains(lowerCaseQuery) ||
                    recipe.description.lowercase().contains(lowerCaseQuery)) {
                    filteredRecipeList.add(recipe)
                }
            }
        }
        
        recipeAdapter.notifyDataSetChanged()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh recipes if needed (e.g., after adding new ones)
        loadRecipes()
    }
}
