package com.ifedorov.recipesapp.data.repository

import com.ifedorov.recipesapp.data.api.RecipeApiService
import com.ifedorov.recipesapp.data.local.dao.CategoriesDao
import com.ifedorov.recipesapp.data.local.dao.RecipesDao
import com.ifedorov.recipesapp.model.Category
import com.ifedorov.recipesapp.model.Recipe

class RecipesRepository(
    private val categoriesDao: CategoriesDao,
    private val recipesDao: RecipesDao,
    private val service: RecipeApiService
) {
    suspend fun getCategories(): List<Category> =
        service.getCategories()

    suspend fun getRecipesByCategoryId(categoryId: Int): List<Recipe> =
        service.getRecipesByCategoryId(categoryId)

    suspend fun getRecipeById(recipeId: Int): Recipe =
        service.getRecipeById(recipeId)

    suspend fun getRecipesByIds(recipesIds: Set<Int>): List<Recipe> =
        if (recipesIds.isEmpty()) {
            emptyList()
        } else {
            val idsString = recipesIds.joinToString(separator = ",")
            service.getRecipesByIds(idsString)
        }

    suspend fun getCategoriesFromCache(): List<Category> =
        categoriesDao.getCategories()

    suspend fun saveCategoriesToCache(categories: List<Category>) {
        categoriesDao.insertCategories(categories)
    }

    suspend fun getRecipesByCategoryIdFromCache(categoryId: Int): List<Recipe> =
        recipesDao.getRecipesByCategoryId(categoryId)

    suspend fun saveRecipesToCache(recipes: List<Recipe>) {
        recipesDao.insertRecipes(recipes)
    }

    suspend fun getRecipeByIdFromCache(recipeId: Int): Recipe? =
        recipesDao.getRecipeById(recipeId)

    suspend fun getFavoritesFromCache(): List<Recipe> =
        recipesDao.getFavorites()
}