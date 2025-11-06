package com.example.myapplication

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html // <-- ADDED IMPORT
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.database.sqlite.SQLiteDatabase
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase
    private var timer: CountDownTimer? = null
    private var scaledDuration: Int = 0

    private lateinit var categorySpinner: AutoCompleteTextView
    private lateinit var recipeSpinner: AutoCompleteTextView
    private lateinit var ingredientInput: EditText
    private lateinit var scaleButton: Button
    private lateinit var ingredientsView: TextView
    private lateinit var stepsView: TextView
    private lateinit var startTimerButton: Button
    private lateinit var timerView: TextView
    private lateinit var ingredientInputLayout: TextInputLayout
    private lateinit var recipeImageView: ImageView

    private data class RecipeData(
        val category: String,
        val name: String,
        val baseIngredient: String,
        val baseQuantity: Float,
        val ingredients: String,
        val steps: String,
        val duration: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        categorySpinner = findViewById(R.id.categorySpinner)
        recipeSpinner = findViewById(R.id.recipeSpinner)
        ingredientInput = findViewById(R.id.ingredientInput)
        ingredientInputLayout = findViewById(R.id.ingredientInputLayout)
        scaleButton = findViewById(R.id.scaleButton)
        ingredientsView = findViewById(R.id.ingredientsView)
        stepsView = findViewById(R.id.stepsView)
        startTimerButton = findViewById(R.id.startTimerButton)
        timerView = findViewById(R.id.timerView)
        recipeImageView = findViewById(R.id.recipeImageView)

        setupDatabase()
        loadCategorySpinner()
        setupListeners()
    }

    private fun setupDatabase() {
        db = openOrCreateDatabase("MealScaleDB", MODE_PRIVATE, null)
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS Recipes(" +
                    "Category TEXT, " +
                    "Name TEXT PRIMARY KEY, " +
                    "BaseIngredient TEXT, " +
                    "BaseQuantity REAL, " +
                    "Ingredients TEXT, " +
                    "Steps TEXT, " +
                    "Duration INT)"
        )
        db.execSQL("DELETE FROM Recipes")
        insertRecipes()
    }

    private fun loadCategorySpinner() {
        val cursor = db.rawQuery("SELECT DISTINCT Category FROM Recipes ORDER BY Category ASC", null)
        val categories = mutableListOf<String>()
        while (cursor.moveToNext()) {
            categories.add(cursor.getString(0))
        }
        cursor.close()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        categorySpinner.setAdapter(adapter)
    }

    private fun setupListeners() {
        categorySpinner.setOnItemClickListener { parent, _, position, _ ->
            val category = parent.getItemAtPosition(position).toString()
            loadRecipeSpinner(category)
            recipeSpinner.text = null
            ingredientsView.text = getString(R.string.ingredients_placeholder)
            stepsView.text = getString(R.string.steps_placeholder)
            ingredientInput.text = null
            timerView.visibility = View.GONE
            recipeImageView.visibility = View.GONE
            scaledDuration = 0
        }

        recipeSpinner.setOnItemClickListener { parent, _, position, _ ->
            val recipeName = parent.getItemAtPosition(position).toString()
            updateHintText(recipeName)
            ingredientsView.text = getString(R.string.ingredients_placeholder)
            stepsView.text = getString(R.string.steps_placeholder)
            ingredientInput.text = null
            timerView.visibility = View.GONE
            scaledDuration = 0

            // 🖼️ Display image dynamically
            val imageResId = getRecipeImageResId(recipeName)
            recipeImageView.setImageResource(imageResId)
            recipeImageView.visibility = View.VISIBLE
        }

        scaleButton.setOnClickListener { handleScaleIngredients() }
        startTimerButton.setOnClickListener { handleStartTimer() }
    }

    private fun loadRecipeSpinner(category: String) {
        val cursor = db.rawQuery("SELECT Name FROM Recipes WHERE Category=? ORDER BY Name ASC", arrayOf(category))
        val names = mutableListOf<String>()
        while (cursor.moveToNext()) names.add(cursor.getString(0))
        cursor.close()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, names)
        recipeSpinner.setAdapter(adapter)
    }

    private fun updateHintText(recipeName: String) {
        val cursor = db.rawQuery("SELECT BaseQuantity FROM Recipes WHERE Name=?", arrayOf(recipeName))
        if (cursor.moveToFirst()) {
            val baseServings = cursor.getFloat(0)
            ingredientInputLayout.hint = "Enter Servings (base is $baseServings)"
        }
        cursor.close()
    }

    private fun getRecipeImageResId(recipeName: String): Int {
        // Normalize name (lowercase + underscores)
        val imageName = recipeName.lowercase(Locale.getDefault())
            .replace(" ", "_")
            .replace("-", "_")

        // Try automatic resource lookup
        val resId = resources.getIdentifier(imageName, "drawable", packageName)

        // ✅ Fallback check if image is missing or misnamed
        return if (resId != 0) resId else when (imageName) {
            "kheer" -> R.drawable.kheer
            "gulab_jamun" -> R.drawable.gulab_jamun
            "rava_kesari" -> R.drawable.rava_kesari
            "mango_lassi_popsicles" -> R.drawable.mango_lassi_popsicles
            "chocolate_mug_cake" -> R.drawable.chocolate_mug_cake
            "paneer_tikka" -> R.drawable.paneer_tikka
            "aloo_tikki" -> R.drawable.aloo_tikki
            "cheese_garlic_bread" -> R.drawable.cheese_garlic_bread
            "sabudana_vada" -> R.drawable.sabudana_vada
            "masala_chai" -> R.drawable.masala_chai
            "mango_lassi" -> R.drawable.mango_lassi
            "lemon_mint_cooler" -> R.drawable.lemon_mint_cooler
            "cold_coffee" -> R.drawable.cold_coffee
            "paneer_butter_masala" -> R.drawable.paneer_butter_masala
            "chole_masala" -> R.drawable.chole_masala
            "dal_tadka" -> R.drawable.dal_tadka
            "palak_paneer" -> R.drawable.palak_paneer
            "aloo_gobi" -> R.drawable.aloo_gobi
            "bhindi_masala" -> R.drawable.bhindi_masala
            "kadai_mushroom" -> R.drawable.kadai_mushroom
            "garlic_butter_naan" -> R.drawable.garlic_butter_naan
            "naan" -> R.drawable.naan
            "chapati" -> R.drawable.chapati
            "poori" -> R.drawable.poori
            "paratha" -> R.drawable.paratha
            "veg_biryani" -> R.drawable.veg_biryani
            "rajma_curry" -> R.drawable.rajma_curry
            "stuffed_capsicum" -> R.drawable.stuffed_capsicum
            "rose_milk" -> R.drawable.rose_milk
            else -> R.drawable.ic_launcher_background
        }
    }

    private fun handleScaleIngredients() {
        val recipeName = recipeSpinner.text.toString()
        if (recipeName.isEmpty()) {
            Toast.makeText(this, "Please select a recipe", Toast.LENGTH_SHORT).show()
            return
        }

        val input = ingredientInput.text.toString()
        val userInputServings = input.toIntOrNull()
        if (userInputServings == null || userInputServings <= 0) {
            Toast.makeText(this, "Please enter a valid number of servings", Toast.LENGTH_SHORT).show()
            return
        }

        val cursor = db.rawQuery("SELECT BaseQuantity, Ingredients, Steps, Duration FROM Recipes WHERE Name=?", arrayOf(recipeName))
        if (cursor.moveToFirst()) {
            val baseServings = cursor.getFloat(0)
            val ingredients = cursor.getString(1)
            val steps = cursor.getString(2)
            val baseDuration = cursor.getInt(3)

            val scaleFactor = userInputServings / baseServings.toDouble()
            val scaledIngredients = scaleIngredients(ingredients, scaleFactor)
            scaledDuration = scaleDuration(baseDuration, baseServings.toInt(), userInputServings)

            ingredientsView.text = "Scaled Ingredients:\n$scaledIngredients"

            // --- MODIFIED SECTION ---
            val servingsText = "Steps (for $userInputServings servings):"
            // Corrected typos "baset" -> "base" and "Ist" -> "list" - The typo is `baset` which should be `base` and `Ist` which should be `list`.
            val disclaimer = "<b>NOTE: Quantities in the steps below are for the base recipe. Please use the scaled ingredient list above.</b>"

            // Replace newline characters with HTML line breaks for steps
            val htmlSteps = steps.replace("\n", "<br/>")

            // Use Html.fromHtml to render the bold tag
            stepsView.text = Html.fromHtml("$servingsText<br/><br/>$disclaimer<br/><br/>$htmlSteps", Html.FROM_HTML_MODE_LEGACY)
            // --- END OF MODIFICATION ---

            timerView.text = "Estimated Cook Time: $scaledDuration minutes"
            timerView.visibility = View.VISIBLE
        }
        cursor.close()
    }

    private fun scaleIngredients(original: String, scaleFactor: Double): String {
        val newLines = original.split("\n").map { line ->
            val parts = line.split("–")
            if (parts.size != 2) return@map line

            val ingredientName = parts[0].trim()
            val rest = parts[1].trim()
            val regex = Regex("""^(\d+\s+\d/\d)|(\d+/\d+)|(\d+\.\d+)|(\d+)""")
            val match = regex.find(rest)

            if (match != null) {
                val numString = match.value
                val remain = rest.substring(numString.length).trim()

                val baseValue = when {
                    numString.contains(" ") -> {
                        val parts2 = numString.split(" ")
                        val intPart = parts2[0].toDouble()
                        val fracParts = parts2[1].split("/")
                        intPart + (fracParts[0].toDouble() / fracParts[1].toDouble())
                    }
                    numString.contains("/") -> {
                        val fracParts = numString.split("/")
                        fracParts[0].toDouble() / fracParts[1].toDouble()
                    }
                    else -> numString.toDouble()
                }

                val scaled = baseValue * scaleFactor
                val formatted = if (scaled % 1.0 == 0.0) scaled.toInt().toString()
                else String.format(Locale.US, "%.2f", scaled)
                "$ingredientName – $formatted $remain"
            } else line
        }
        return newLines.joinToString("\n")
    }

    private fun scaleDuration(base: Int, baseQty: Int, userQty: Int): Int {
        val diff = userQty - baseQty
        val newDur = when {
            diff > 0 && diff <= 10 -> base + 10
            diff > 10 && diff <= 20 -> base + 20
            diff > 20 && diff <= 50 -> base + 45
            diff > 50 -> base + 60
            diff < 0 -> (base * (userQty.toDouble() / baseQty)).toInt()
            else -> base
        }
        return newDur.coerceAtLeast(5)
    }

    private fun handleStartTimer() {
        timer?.cancel()
        if (scaledDuration <= 0) {
            Toast.makeText(this, "Please scale a recipe first", Toast.LENGTH_SHORT).show()
            return
        }

        // --- CORRECTED CODE ---
        val durationMs = (scaledDuration * 60 * 1000).toLong()
        timerView.visibility = View.VISIBLE

        timer = object : CountDownTimer(durationMs, 1000) {
            override fun onTick(ms: Long) {
                val min = (ms / 1000) / 60
                val sec = (ms / 1000) % 60
                timerView.text = String.format(Locale.getDefault(), "Time Left: %02d:%02d", min, sec)
            }

            override fun onFinish() {
                timerView.text = "Timer Done! 🎉"
                scaledDuration = 0
            }
        }.start()
    }

    private fun insertRecipes() {
        val recipes = listOf(
            // --- DESSERTS ---
            RecipeData("Desserts", "Kheer", "Basmati rice", 2.0f, // 2 Servings
                "Basmati rice – 1/4 cup\n" +
                        "Whole milk – 4 cups\n" +
                        "Sugar – 1/2 cup\n" +
                        "Cardamom powder – 1/2 tsp\n" +
                        "Chopped nuts (almonds, pistachios) – 2 tbsp\n" +
                        "Raisins – 1 tbsp\n" +
                        "Saffron strands – a pinch (soaked in 1 tbsp warm milk)",
                "1. Wash 1/4 cup rice until water runs clear. Soak in water for 30 minutes, then drain completely.\n" +
                        "2. Pour 4 cups of whole milk into a heavy-bottomed pan to prevent scorching.\n" +
                        "3. Bring the milk to a boil over medium heat, stirring occasionally.\n" +
                        "4. Add the drained rice. Turn heat to low and simmer for 25-30 minutes.\n" +
                        "5. Stir every few minutes to prevent rice from sticking. Cook until the kheer is thick and creamy.\n" +
                        "6. Add 1/2 cup sugar and 1/2 tsp cardamom powder. Stir until sugar dissolves.\n" +
                        "7. Add 2 tbsp chopped nuts and 1 tbsp raisins. Cook for 5 more minutes. Garnish with saffron milk.", 30),

            RecipeData("Desserts", "Gulab Jamun", "Khoya", 4.0f, // 4 Servings (~12 jamuns)
                "Khoya (unsweetened) – 1 cup, crumbled\n" +
                        "Maida (all-purpose flour) – 2 tbsp\n" +
                        "Baking soda – 1 pinch\n" +
                        "Sugar – 1.5 cups\n" +
                        "Water – 1.5 cups\n" +
                        "Cardamom pods – 2, slightly crushed\n" +
                        "Rose water – 1 tsp\n" +
                        "Ghee or Oil – for deep frying",
                "1. Make syrup: Boil 1.5 cups sugar, 1.5 cups water, and 2 cardamom pods for 5 minutes. Turn off heat, add 1 tsp rose water. Syrup should be sticky (not thick).\n" +
                        "2. Crumble 1 cup khoya. Add 2 tbsp maida and 1 pinch of baking soda.\n" +
                        "3. Gently knead just until a smooth, soft dough forms. Do not over-knead.\n" +
                        "4. Roll the dough into 12-15 small, completely smooth, crack-free balls.\n" +
                        "5. Heat ghee or oil on LOW. To test, add a small piece of dough; it should rise slowly.\n" +
                        "6. Fry the balls on low heat, stirring gently and constantly, for 10-12 minutes until deep golden brown.\n" +
                        "7. Remove from ghee, drain for 30 seconds, and add directly to the *warm* (not hot) sugar syrup.\n" +
                        "8. Let them soak for at least 45 minutes to absorb the syrup.", 45),

            RecipeData("Desserts", "Rava Kesari", "Rava", 2.0f, // 2 Servings
                "Rava (Semolina) – 1/2 cup\n" +
                        "Sugar – 1/2 cup\n" +
                        "Ghee – 1/4 cup\n" +
                        "Water – 1.5 cups\n" +
                        "Cardamom powder – 1/4 tsp\n" +
                        "Cashews – 1 tbsp, broken\n" +
                        "Raisins – 1 tbsp",
                "1. Heat 1 tbsp of the ghee in a pan. Fry 1 tbsp cashews and 1 tbsp raisins until golden, then remove and set aside.\n" +
                        "2. In the same pan, add the remaining ghee and 1/2 cup rava.\n" +
                        "3. Roast the rava on low heat for 7-10 minutes until it's aromatic. Do not let it brown. Set rava aside.\n" +
                        "4. In a separate pot, boil 1.5 cups of water.\n" +
                        "5. Carefully pour the boiling water into the roasted rava, stirring constantly to avoid any lumps.\n" +
                        "6. Cook for 2-3 minutes until the rava absorbs all the water and is cooked through.\n" +
                        "7. Add 1/2 cup sugar and 1/4 tsp cardamom powder. The mixture will become loose as the sugar melts.\n" +
                        "8. Keep stirring on low heat for 3-5 minutes until the mixture thickens again.\n" +
                        "9. Garnish with the fried cashews and raisins.", 15),

            RecipeData("Desserts", "Mango Lassi Popsicles", "Mango pulp", 2.0f, // 2 Servings
                "Ripe Mango pulp – 1 cup\n" +
                        "Thick Yogurt (dahi) – 1 cup\n" +
                        "Sugar – 2 tbsp (or to taste)\n" +
                        "Cardamom powder – 1 pinch",
                "1. In a blender, combine 1 cup mango pulp, 1 cup thick yogurt, and 2 tbsp sugar.\n" +
                        "2. Add 1 pinch of cardamom powder.\n" +
                        "3. Blend for 1-2 minutes until the mixture is completely smooth and creamy.\n" +
                        "4. Pour this lassi mixture into your popsicle molds.\n" +
                        "5. Insert the popsicle sticks and freeze for a minimum of 6 hours.", 360),

            RecipeData("Desserts", "Chocolate Mug Cake", "All-purpose flour", 1.0f, // 1 Serving
                "All-purpose flour (maida) – 4 tbsp\n" +
                        "Cocoa powder (unsweetened) – 2 tbsp\n" +
                        "Granulated Sugar – 3 tbsp\n" +
                        "Milk – 3 tbsp\n" +
                        "Vegetable Oil – 2 tbsp\n" +
                        "Baking powder – 1/4 tsp\n" +
                        "A pinch of salt",
                "1. In a large, microwave-safe mug, add 4 tbsp flour, 2 tbsp cocoa powder, 3 tbsp sugar, 1/4 tsp baking powder, and a pinch of salt.\n" +
                        "2. Whisk the dry ingredients together with a fork.\n" +
                        "3. Add 3 tbsp milk and 2 tbsp oil. Mix with the fork until the batter is smooth.\n" +
                        "4. Microwave on high for 90 seconds. The cake will rise a lot.\n" +
                        "5. Let it cool for 1-2 minutes before eating.", 2),

            // --- STARTERS ---
            RecipeData("Starters", "Paneer Tikka", "Paneer", 2.0f, // 2 Servings
                "Paneer – 200g, cut into 1-inch cubes\n" +
                        "Thick Yogurt (hung curd) – 1/2 cup\n" +
                        "Ginger-garlic paste – 1 tsp\n" +
                        "Red chili powder – 1 tsp\n" +
                        "Garam masala – 1/2 tsp\n" +
                        "Lemon juice – 1 tbsp\n" +
                        "Capsicum (Green) – 1, cut into cubes\n" +
                        "Onion – 1, cut into cubes\n" +
                        "Salt – to taste",
                "1. In a bowl, whisk 1/2 cup thick yogurt with 1 tsp ginger-garlic paste, 1 tsp chili powder, 1/2 tsp garam masala, salt and 1 tbsp lemon juice.\n" +
                        "2. Add 200g paneer cubes, 1 cubed onion, and 1 cubed capsicum.\n" +
                        "3. Gently toss until evenly coated. Cover and refrigerate for 30 minutes.\n" +
                        "4. Thread the paneer and vegetables alternately onto skewers.\n" +
                        "5. Bake in a preheated oven at 200°C (400°F) for 15-20 minutes, until the edges are nicely charred.", 20),

            RecipeData("Starters", "Aloo Tikki", "Boiled potatoes", 2.0f, // 2 Servings (~6 tikkis)
                "Potatoes – 3 large, boiled and cooled\n" +
                        "Bread crumbs – 1/4 cup\n" +
                        "Green chili – 1, finely chopped\n" +
                        "Coriander leaves – 2 tbsp, finely chopped\n" +
                        "Cumin powder – 1/2 tsp\n" +
                        "Amchur (dry mango powder) – 1/4 tsp\n" +
                        "Salt – to taste\n" +
                        "Oil – for shallow frying",
                "1. Peel and mash 3 cooled boiled potatoes smoothly.\n" +
                        "2. Add 1/4 cup bread crumbs, 1 chopped green chili, 2 tbsp coriander leaves, 1/2 tsp cumin, 1/4 tsp amchur, and salt.\n" +
                        "3. Mix very well until it forms a firm dough.\n" +
                        "4. Divide the mixture and shape into 6 flat, round patties.\n" +
                        "5. Heat 2-3 tbsp oil in a tawa (griddle).\n" +
                        "6. Fry tikkis on medium-low heat for 3-4 minutes per side, until crisp and deep golden brown.", 25),

            RecipeData("Starters", "Veg Spring Rolls", "Spring roll sheets", 3.0f, // 3 Servings (6 rolls)
                "Spring roll sheets – 6\n" +
                        "Cabbage – 1/2 cup, shredded\n" +
                        "Carrots – 1/4 cup, shredded\n" +
                        "Capsicum – 1/4 cup, shredded\n" +
                        "Soy sauce – 1 tsp\n" +
                        "Black pepper – 1/4 tsp\n" +
                        "Oil – for deep frying",
                "1. Heat 1 tsp oil in a wok. Add 1/2 cup cabbage, 1/4 cup carrots, and 1/4 cup capsicum. Stir-fry on high heat for 2-3 minutes until tender-crisp.\n" +
                        "2. Add 1 tsp soy sauce and 1/4 tsp black pepper. Let the filling cool completely.\n" +
                        "3. Make a paste of 1 tbsp maida (flour) and 2 tbsp water.\n" +
                        "4. Place 2 tbsp of cool filling on a spring roll sheet. Fold, roll tightly, and seal the edge with the paste.\n" +
                        "5. Deep fry the rolls on medium heat for 3-4 minutes until golden and crispy.", 30),

            RecipeData("Starters", "Cheese Garlic Bread", "Bread slices", 2.0f, // 2 Servings
                "Bread slices – 4\n" +
                        "Butter – 2 tbsp, softened\n" +
                        "Garlic – 1 tsp, finely minced\n" +
                        "Mozzarella Cheese – 1/2 cup, shredded\n" +
                        "Oregano – 1/4 tsp",
                "1. In a small bowl, mix 2 tbsp of softened butter with 1 tsp of finely minced garlic.\n" +
                        "2. Add 1/4 tsp oregano and mix well.\n" +
                        "3. Spread the garlic butter evenly on all 4 bread slices.\n" +
                        "4. Top with 1/2 cup of shredded mozzarella cheese.\n" +
                        "5. Bake in a preheated oven at 180°C (350°F) for 8-10 minutes, until cheese is melted and bubbly.", 10),

            RecipeData("Starters", "Sabudana Vada", "Sabudana", 2.0f, // 2 Servings
                "Sabudana (Tapioca pearls) – 1 cup\n" +
                        "Boiled potato – 1 large, mashed\n" +
                        "Peanuts – 2 tbsp, roasted and crushed\n" +
                        "Green chili – 1-2, finely chopped\n" +
                        "Cumin seeds – 1 tsp\n" +
                        "Salt – to taste\n" +
                        "Oil – for deep frying",
                "1. Wash 1 cup sabudana. Soak in just enough water to cover it for 4-6 hours. Drain any excess water.\n" +
                        "2. In a bowl, combine the soaked sabudana, 1 mashed boiled potato, and 2 tbsp of roasted crushed peanuts.\n" +
                        "3. Add 1-2 chopped green chilies, 1 tsp cumin seeds, and salt to taste.\n" +
                        "4. Mix everything well to form a dough.\n" +
                        "5. Shape the mixture into flat, round patties (vadas).\n" +
                        "6. Deep fry in medium-hot oil for 4-5 minutes per side, until golden brown and crispy.", 40),

            // --- DRINKS ---
            RecipeData("Drinks", "Masala Chai", "Water", 2.0f, // 2 Servings
                "Water – 1 cup\n" +
                        "Milk – 1 cup\n" +
                        "Black tea leaves – 2 tsp\n" +
                        "Sugar – 2 tsp (or to taste)\n" +
                        "Ginger – 1/2 inch piece, crushed\n" +
                        "Cardamom pod – 1, crushed",
                "1. In a saucepan, add 1 cup of water, 1/2 inch crushed ginger, and 1 crushed cardamom pod. Bring to a boil.\n" +
                        "2. Add 2 tsp of tea leaves. Let it boil for 1-2 minutes.\n" +
                        "3. Add 1 cup of milk and 2 tsp of sugar. Bring back to a simmer.\n" +
                        "4. Simmer on low heat for 2-3 minutes to get a rich color.\n" +
                        "5. Strain the chai into 2 cups and serve hot.", 10),

            RecipeData("Drinks", "Mango Lassi", "Mango pulp", 2.0f, // 2 Servings
                "Ripe Mango pulp – 1 cup\n" +
                        "Plain Yogurt (dahi) – 1 cup\n" +
                        "Sugar – 2 tbsp (or to taste)\n" +
                        "Cardamom powder – 1/4 tsp\n" +
                        "Ice cubes – 3-4",
                "1. Add 1 cup mango pulp, 1 cup plain yogurt, 2 tbsp sugar, and 1/4 tsp cardamom powder to a blender.\n" +
                        "2. Add 3-4 ice cubes. Blend on high speed for 60-90 seconds.\n" +
                        "3. Stop when the lassi is completely smooth and frothy.\n" +
                        "4. Pour into 2 tall glasses and serve chilled.", 5),

            RecipeData("Drinks", "Lemon Mint Cooler", "Lemon juice", 1.0f, // 1 Serving
                "Lemon juice – 2 tbsp (from 1 lemon)\n" +
                        "Fresh Mint leaves – 10-12\n" +
                        "Sugar – 2 tsp (or to taste)\n" +
                        "Cold Water – 1 cup\n" +
                        "Chilled Soda – 1/2 cup\n" +
                        "Ice cubes – 3-4",
                "1. In a tall glass, add 10-12 fresh mint leaves, 2 tsp sugar, and 2 tbsp lemon juice.\n" +
                        "2. Gently muddle (press) the mint with a spoon for 30 seconds to release its oils.\n" +
                        "3. Add 1 cup of cold water and stir until the sugar is dissolved.\n" +
                        "4. Add 3-4 ice cubes. Top off with 1/2 cup of chilled soda. Stir gently.", 5),

            RecipeData("Drinks", "Cold Coffee", "Milk", 1.0f, // 1 Serving
                "Chilled Milk – 1 cup\n" +
                        "Instant coffee – 1 tsp\n" +
                        "Sugar – 2 tsp (or to taste)\n" +
                        "Ice cubes – 4-5",
                "1. In a blender, add 1 cup of chilled milk, 1 tsp instant coffee, and 2 tsp sugar.\n" +
                        "2. Add 4-5 ice cubes. This is essential for the frothy texture.\n" +
                        "3. Blend on high speed for 1-2 minutes until extremely frothy.\n" +
                        "4. Pour into a tall glass and serve immediately.", 3),

            RecipeData("Drinks", "Rose Milk", "Milk", 1.0f, // 1 Serving
                "Chilled Milk – 1 cup\n" +
                        "Rose syrup – 2 tbsp\n" +
                        "Sugar – 1 tsp (optional)\n" +
                        "Soaked sabja seeds (basil seeds) – 1 tsp (optional)",
                "1. Pour 1 cup of chilled milk into a glass.\n" +
                        "2. Add 2 tbsp of rose syrup. Stir vigorously for 30-60 seconds.\n" +
                        "3. Taste it. Add 1 tsp sugar only if needed.\n" +
                        "4. If using, add 1 tsp of pre-soaked sabja seeds. Stir and serve.", 2),

            // --- MAIN COURSES ---
            RecipeData("Main Courses", "Paneer Butter Masala", "Paneer", 2.0f, // 2 Servings
                "Paneer – 200g, cubed\n" +
                        "Butter – 2 tbsp\n" +
                        "Onion – 1, finely chopped\n" +
                        "Tomato puree – 1 cup\n" +
                        "Fresh Cream – 1/4 cup\n" +
                        "Garam masala – 1/2 tsp\n" +
                        "Red chili powder (Kashmiri) – 1 tsp\n" +
                        "Ginger-garlic paste – 1 tsp\n" +
                        "Water – 1/2 cup\n" +
                        "Salt – to taste",
                "1. Heat 2 tbsp butter in a pan over medium heat.\n" +
                        "2. Add 1 finely chopped onion and sauté for 5-7 minutes, until soft and translucent.\n" +
                        "3. Add 1 tsp ginger-garlic paste and sauté for 1 minute.\n" +
                        "4. Add 1 cup tomato puree, 1 tsp chili powder, and 1/2 tsp garam masala. Cook for 5 minutes, until the masala thickens.\n" +
                        "5. Add 1/4 cup cream and 1/2 cup water. Stir and bring to a gentle simmer.\n" +
                        "6. Add 200g paneer cubes and salt. Simmer on low heat for 5 minutes.\n" +
                        "7. Garnish with a swirl of cream and serve hot.", 30),

            RecipeData("Main Courses", "Chole Masala", "Chickpeas", 2.0f, // 2 Servings
                "Chickpeas (Kabuli Chana) – 1 cup, soaked overnight\n" +
                        "Onion – 1 large, finely chopped\n" +
                        "Tomato – 1 large, pureed\n" +
                        "Chole masala powder – 2-3 tsp\n" +
                        "Ginger-garlic paste – 1 tsp\n" +
                        "Oil – 2 tbsp\n" +
                        "Water – 2 cups\n" +
                        "Salt – to taste",
                "1. Heat 2 tbsp oil in a pressure cooker. Sauté 1 finely chopped onion until deep golden brown.\n" +
                        "2. Add 1 tsp ginger-garlic paste. Add 1 pureed tomato and cook for 3-4 minutes until oil separates.\n" +
                        "3. Add 2-3 tsp chole masala powder and salt. Sauté for 1 more minute.\n" +
                        "4. Add 1 cup of soaked, drained chickpeas and 2 cups of water.\n" +
                        "5. Pressure cook for 5-6 whistles, or until the chickpeas are very soft.\n" +
                        "6. Open the cooker, lightly mash a few chickpeas to thicken the gravy. Simmer for 5-10 minutes.", 40),

            RecipeData("Main Courses", "Veg Biryani", "Basmati rice", 2.0f, // 2 Servings
                "Basmati rice – 1 cup\n" +
                        "Mixed veggies (carrot, peas, beans, potato) – 1 cup, chopped\n" +
                        "Thick Yogurt (dahi) – 1/4 cup, whisked\n" +
                        "Biryani masala – 2 tbsp\n" +
                        "Saffron – 1 pinch, soaked in 2 tbsp warm milk\n" +
                        "Onion – 1, thinly sliced (for frying)\n" +
                        "Ginger-garlic paste – 1 tsp\n" +
                        "Ghee/Oil – 3 tbsp",
                "1. Wash 1 cup Basmati rice. Cook it in boiling water for 5-7 minutes until 70% cooked. Drain.\n" +
                        "2. Heat 3 tbsp ghee/oil, fry 1 sliced onion until golden brown (birista). Remove.\n" +
                        "3. In the same oil, add 1 tsp ginger-garlic paste. Sauté 1 cup mixed veggies for 5 minutes.\n" +
                        "4. Add 1/4 cup yogurt, 2 tbsp biryani masala, and salt. Cook for 3-4 minutes.\n" +
                        "5. In a heavy-bottomed pot, layer: 1/2 of the rice, then all the veggies, then the remaining rice.\n" +
                        "6. Top with fried onions and drizzle the saffron-infused milk.\n" +
                        "7. Cover tightly. Cook on *very* low heat ('dum') for 15-20 minutes.", 45),

            RecipeData("Main Courses", "Dal Tadka", "Toor dal", 2.0f, // 2 Servings
                "Toor dal (Arhar dal) – 1/2 cup\n" +
                        "Water – 1.5 cups\n" +
                        "Turmeric powder – 1/2 tsp\n" +
                        "Salt – to taste\n" +
                        "Ghee – 2 tbsp\n" +
                        "Cumin seeds – 1 tsp\n" +
                        "Garlic – 4 cloves, finely chopped\n" +
                        "Onion – 1 medium, finely chopped\n" +
                        "Tomato – 1 small, finely chopped\n" +
                        "Red chili powder – 1/2 tsp",
                "1. Rinse 1/2 cup toor dal. Pressure cook with 1.5 cups water, 1/2 tsp turmeric, and salt for 3-4 whistles until soft.\n" +
                        "2. Whisk the cooked dal lightly.\n" +
                        "3. Prepare the tadka: Heat 2 tbsp ghee. Add 1 tsp cumin seeds.\n" +
                        "4. Add 4 cloves of chopped garlic. Fry for 30 seconds until golden.\n" +
                        "5. Add 1 chopped onion and sauté until translucent.\n" +
                        "6. Add 1 chopped tomato and cook until soft.\n" +
                        "7. Add 1/2 tsp red chili powder. Pour this sizzling tadka over the boiled dal.\n" +
                        "8. Simmer the dal for 2 more minutes. Garnish with coriander.", 25),

            RecipeData("Main Courses", "Rajma Curry", "Rajma", 2.0f, // 2 Servings
                "Rajma (Kidney beans) – 1 cup, soaked overnight\n" +
                        "Onion – 1 large, finely chopped\n" +
                        "Tomato – 1 large, pureed\n" +
                        "Ginger-garlic paste – 1 tsp\n" +
                        "Garam masala – 1/2 tsp\n" +
                        "Coriander powder – 1 tsp\n" +
                        "Red chili powder – 1 tsp\n" +
                        "Oil – 2 tbsp\n" +
                        "Salt – to taste",
                "1. Pressure cook 1 cup of (overnight-soaked) rajma with 3 cups water and salt for 6-8 whistles until soft.\n" +
                        "2. Make masala: Heat 2 tbsp oil, sauté 1 chopped onion until deep golden brown.\n" +
                        "3. Add 1 tsp ginger-garlic paste and 1 pureed tomato. Cook for 5-7 minutes until oil separates.\n" +
                        "4. Add spices: 1 tsp chili powder, 1 tsp coriander powder, 1/2 tsp garam masala. Fry for 1 minute.\n" +
                        "5. Add the boiled rajma *with its water* to the masala.\n" +
                        "6. Mash some of the rajma beans to thicken the gravy.\n" +
                        "7. Cover and simmer on low heat for 20-30 minutes.", 45),

            RecipeData("Main Courses", "Palak Paneer", "Spinach", 2.0f, // 2 Servings
                "Spinach (Palak) – 2 cups, packed\n" +
                        "Paneer – 200g, cubed\n" +
                        "Onion – 1, finely chopped\n" +
                        "Garlic – 4 cloves, minced\n" +
                        "Garam masala – 1/2 tsp\n" +
                        "Fresh Cream – 2 tbsp (optional)\n" +
                        "Oil/Ghee – 2 tbsp\n" +
                        "Salt – to taste",
                "1. Blanch 2 cups of packed spinach in boiling water for 2-3 minutes.\n" +
                        "2. Immediately transfer spinach to ice-cold water. This keeps the green color.\n" +
                        "3. Drain and blend the spinach to a smooth puree.\n" +
                        "4. Heat 2 tbsp oil/ghee. Sauté 1 chopped onion for 3-4 minutes. Add 4 cloves of minced garlic and sauté for 1 more minute.\n" +
                        "5. Add the spinach puree, salt, and 1/2 tsp garam masala. Cook for 5-7 minutes.\n" +
                        "6. Add 200g paneer cubes. Stir gently. Simmer for 5 minutes.\n" +
                        "7. Finish with 2 tbsp of cream (optional) and turn off heat.", 30),

            RecipeData("Main Courses", "Aloo Gobi", "Potato", 2.0f, // 2 Servings
                "Potatoes – 2 medium, cubed\n" +
                        "Cauliflower – 1 cup, florets\n" +
                        "Onion – 1, sliced\n" +
                        "Cumin seeds – 1 tsp\n" +
                        "Turmeric powder – 1/2 tsp\n" +
                        "Chili powder – 1 tsp\n" +
                        "Oil – 2-3 tbsp\n" +
                        "Salt – to taste",
                "1. Heat 3 tbsp oil in a pan. Add 1 tsp cumin seeds. Let them splutter.\n" +
                        "2. Add 1 sliced onion and sauté for 2-3 minutes.\n" +
                        "3. Add 2 chopped potatoes and 1 cup cauliflower florets.\n" +
                        "4. Add spices: 1/2 tsp turmeric, 1 tsp chili powder, and salt. Mix well.\n" +
                        "5. Cover and cook on low heat for 15-20 minutes. Stir occasionally.\n" +
                        "6. Once veggies are tender, remove lid, increase heat, and sauté for 3-5 minutes until slightly roasted.", 25),

            RecipeData("Main Courses", "Bhindi Masala", "Bhindi", 2.0f, // 2 Servings
                "Bhindi (Okra) – 1 cup, chopped\n" +
                        "Onion – 1 large, sliced\n" +
                        "Tomato – 1, chopped\n" +
                        "Coriander powder – 1 tsp\n" +
                        "Chili powder – 1 tsp\n" +
                        "Turmeric powder – 1/2 tsp\n" +
                        "Amchur (dry mango powder) – 1/2 tsp\n" +
                        "Oil – 2 tbsp\n" +
                        "Salt – to taste",
                "1. Wash 1 cup bhindi (okra) and dry it *completely* with a cloth.\n" +
                        "2. Chop the bhindi into 1-inch pieces.\n" +
                        "3. Heat 2 tbsp oil. Sauté 1 large sliced onion for 5-7 minutes until soft and golden.\n" +
                        "4. Add 1 chopped tomato and cook for 3-4 minutes until soft.\n" +
                        "5. Add spices: 1/2 tsp turmeric, 1 tsp chili powder, 1 tsp coriander powder, and salt. Sauté for 1 minute.\n" +
                        "6. Add the chopped bhindi. Stir to coat well.\n" +
                        "7. Cook *uncovered* on medium-low heat for 10-12 minutes, stirring occasionally. Add 1/2 tsp amchur at the end.", 20),

            RecipeData("Main Courses", "Kadai Mushroom", "Mushroom", 2.0f, // 2 Servings
                "Mushroom – 1 cup, sliced\n" +
                        "Capsicum – 1, cubed\n" +
                        "Onion – 1, cubed\n" +
                        "Tomato – 1, pureed\n" +
                        "Kadai masala – 1 tbsp\n" +
                        "Ginger-garlic paste – 1 tsp\n" +
                        "Oil – 2 tbsp\n" +
                        "Salt – to taste",
                "1. Heat 1 tbsp oil in a kadai on high heat.\n" +
                        "2. Add 1 cubed onion and 1 cubed capsicum. Stir-fry for 2-3 minutes until charred but crunchy. Remove and set aside.\n" +
                        "3. In the same pan, add 1 tbsp more oil. Add 1 cup of sliced mushrooms.\n" +
                        "4. Cook on high heat for 5-7 minutes until mushrooms are golden brown.\n" +
                        "5. Add 1 tsp ginger-garlic paste. Sauté for 30 seconds.\n" +
                        "6. Add 1 pureed tomato and 1 tbsp of kadai masala. Cook for 3-4 minutes.\n" +
                        "7. Add the sautéed onion and capsicum back to the pan. Add salt. Toss and cook for 2 more minutes.", 25),

            RecipeData("Main Courses", "Stuffed Capsicum", "Capsicum", 2.0f, // 2 Servings
                "Capsicum (Bell Peppers) – 3 medium\n" +
                        "Boiled potatoes – 2 large, mashed\n" +
                        "Garam masala – 1/2 tsp\n" +
                        "Chili powder – 1/2 tsp\n" +
                        "Amchur (dry mango powder) – 1/2 tsp\n" +
                        "Cumin seeds – 1/2 tsp\n" +
                        "Oil – 2 tbsp\n" +
                        "Salt – to taste",
                "1. Prepare stuffing: Heat 1 tsp oil, sauté 1/2 tsp cumin. Add 2 mashed boiled potatoes.\n" +
                        "2. Add spices to potatoes: 1/2 tsp garam masala, 1/2 tsp chili powder, 1/2 tsp amchur, and salt. Mix and cook for 2 minutes. Let cool.\n" +
                        "3. Cut the tops off 3 capsicums and remove all seeds.\n" +
                        "4. Stuff the potato mixture tightly into each capsicum.\n" +
                        "5. Heat 1 tbsp oil in a flat-bottomed pan on low heat.\n" +
                        "6. Place the stuffed capsicums in the pan.\n" +
                        "7. Cover and cook on low heat for 15-20 minutes, turning every 5-7 minutes, until the capsicum skin is soft and wrinkled.", 35),

            // --- BREADS ---
            RecipeData("Breads", "Chapati", "Wheat flour", 4.0f, // 4 Servings (~8 chapatis)
                "Whole Wheat Flour (Atta) – 2 cups\n" +
                        "Water – approx 1 cup, warm\n" +
                        "Salt – 1/2 tsp\n" +
                        "Ghee or Oil – 1 tsp (optional, in dough)",
                "1. In a bowl, mix 2 cups whole wheat flour (atta) with 1/2 tsp salt.\n" +
                        "2. Slowly add warm water and knead for 7-10 minutes to form a soft, smooth dough.\n" +
                        "3. Cover the dough and let it rest for at least 20 minutes.\n" +
                        "4. Make 8 small, equal-sized balls.\n" +
                        "5. Dip a ball in dry flour and roll it into a thin, even circle (6-7 inches).\n" +
                        "6. Place the chapati on a preheated hot tawa (griddle). Cook for 20-30 seconds.\n" +
                        "7. Flip it. Cook the other side for 1-2 minutes until brown spots appear.\n" +
                        "8. Flip again onto a direct flame to puff up. Apply ghee.", 15),

            RecipeData("Breads", "Naan", "Maida", 2.0f, // 2 Servings (~4 naans)
                "Maida (All-purpose flour) – 2 cups\n" +
                        "Yogurt (Curd) – 1/2 cup, whisked\n" +
                        "Active Dry Yeast – 1 tsp\n" +
                        "Sugar – 1 tsp\n" +
                        "Warm Water – 1/4 cup\n" +
                        "Oil – 1 tbsp\n" +
                        "Salt – 1/2 tsp",
                "1. In 1/4 cup warm water, dissolve 1 tsp sugar and 1 tsp active dry yeast. Let it sit for 10 minutes until frothy.\n" +
                        "2. In a large bowl, mix 2 cups maida, 1/2 cup yogurt, 1 tbsp oil, and 1/2 tsp salt.\n" +
                        "3. Add the frothy yeast mixture. Knead for 8-10 minutes, adding more warm water if needed, to make a very soft dough.\n" +
                        "4. Coat the dough with oil, cover, and let it rise for 1-2 hours, until doubled.\n" +
                        "5. Punch down the dough and divide into 4 balls. Shape into oval naans.\n" +
                        "6. Cook on a very hot tawa or in an oven (250°C / 480°F) for 3-5 minutes, until puffed and golden.", 20),

            RecipeData("Breads", "Paratha", "Wheat flour", 2.0f, // 2 Servings (~4 parathas)
                "Whole Wheat Flour (Atta) – 2 cups\n" +
                        "Stuffing (e.g., mashed potato) – 1 cup\n" +
                        "Water – as needed\n" +
                        "Salt – 1/2 tsp\n" +
                        "Ghee or Oil – for frying",
                "1. Prepare a soft chapati dough using 2 cups wheat flour, 1/2 tsp salt, and water. Let it rest for 20 minutes.\n" +
                        "2. Divide dough and 1 cup of stuffing into 4 equal portions.\n" +
                        "3. Roll a dough ball into a 3-inch circle. Place a portion of stuffing in the center.\n" +
                        "4. Bring the edges of the dough up and pinch them together at the top, sealing the stuffing.\n" +
                        "5. Gently press the ball flat, dust with flour, and *carefully* roll it out into a 6-7 inch circle.\n" +
                        "6. Cook on a hot tawa for 1 minute. Flip, drizzle 1/2 tsp ghee/oil.\n" +
                        "7. Flip again, drizzle ghee on the other side. Press and cook for 1-2 minutes per side until golden brown and crispy.", 20),

            RecipeData("Breads", "Poori", "Wheat flour", 2.0f, // 2 Servings (~12 pooris)
                "Whole Wheat Flour (Atta) – 2 cups\n" +
                        "Salt – 1/2 tsp\n" +
                        "Water – as needed\n" +
                        "Oil – for deep frying",
                "1. In a bowl, mix 2 cups wheat flour and 1/2 tsp salt.\n" +
                        "2. Add water little by little and knead into a *stiff*, firm dough (stiffer than chapati dough).\n" +
                        "3. Knead for 5-7 minutes. Cover and let it rest for 15-20 minutes.\n" +
                        "4. Make 12 small, lemon-sized balls. Rub them with a drop of oil.\n" +
                        "5. Roll each ball into a 3-4 inch circle.\n" +
                        "6. Heat oil for deep frying (it must be very hot).\n" +
                        "7. Gently slide one poori into the oil. Lightly press it down to help it puff up.\n" +
                        "8. Fry for 30-45 seconds per side until light golden. Drain.", 20),

            RecipeData("Breads", "Garlic Butter Naan", "Maida", 2.0f, // 2 Servings (~4 naans)
                "Maida (All-purpose flour) – 2 cups\n" +
                        "Yogurt (Curd) – 1/2 cup\n" +
                        "Active Dry Yeast – 1 tsp\n" +
                        "Sugar – 1 tsp\n" +
                        "Garlic – 2 tbsp, finely minced\n" +
                        "Butter – 2 tbsp, melted\n" +
                        "Coriander leaves – 1 tbsp, chopped",
                "1. Follow the Naan recipe (steps 1-4) to prepare the risen dough and divide it into 4 balls.\n" +
                        "2. While rolling a dough ball into an oval, sprinkle 1/2 tsp of minced garlic and some coriander on top.\n" +
                        "3. Roll over it one last time to press the garlic into the dough.\n" +
                        "4. Cook the naan on a very hot tawa or in the oven (3-5 minutes).\n" +
                        "5. While the naan is cooking, melt 2 tbsp of butter and add the remaining minced garlic to it.\n" +
                        "6. As soon as the naan is cooked, remove it and brush it generously with the garlic butter. Garnish with more coriander.", 25)
        )

        val insert = db.compileStatement(
            "INSERT OR REPLACE INTO Recipes(Category, Name, BaseIngredient, BaseQuantity, Ingredients, Steps, Duration) VALUES(?, ?, ?, ?, ?, ?, ?)"
        )

        for (recipe in recipes) {
            insert.bindString(1, recipe.category)
            insert.bindString(2, recipe.name)
            insert.bindString(3, recipe.baseIngredient)
            insert.bindDouble(4, recipe.baseQuantity.toDouble())
            insert.bindString(5, recipe.ingredients)
            insert.bindString(6, recipe.steps)
            insert.bindLong(7, recipe.duration.toLong())
            insert.executeInsert()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        db.close()
    }
}