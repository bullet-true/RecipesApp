package com.ifedorov.recipesapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ifedorov.recipesapp.model.Recipe

@Dao
interface RecipesDao {
    @Query("SELECT * FROM recipes WHERE category_id = :categoryId")
    suspend fun getRecipesByCategoryId(categoryId: Int): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<Recipe>)

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Int): Recipe?

    @Query("SELECT * FROM recipes WHERE is_favorite = true")
    suspend fun getFavorites(): List<Recipe>
}