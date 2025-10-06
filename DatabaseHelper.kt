package com.example.mealscale.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "MealScaleDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE Recipes (" +
            "RecipeID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "Name TEXT," +
            "Description TEXT)"
        )

        db.execSQL(
            "CREATE TABLE Ingredients (" +
            "IngredientID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "RecipeID INTEGER," +
            "Name TEXT," +
            "Quantity REAL," +
            "FOREIGN KEY(RecipeID) REFERENCES Recipes(RecipeID))"
        )

        db.execSQL(
            "CREATE TABLE Steps (" +
            "StepID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "RecipeID INTEGER," +
            "Description TEXT," +
            "Duration INTEGER," +
            "FOREIGN KEY(RecipeID) REFERENCES Recipes(RecipeID))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Steps")
        db.execSQL("DROP TABLE IF EXISTS Ingredients")
        db.execSQL("DROP TABLE IF EXISTS Recipes")
        onCreate(db)
    }
}
