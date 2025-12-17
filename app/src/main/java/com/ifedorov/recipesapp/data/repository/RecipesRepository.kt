package com.ifedorov.recipesapp.data.repository

import android.content.Context
import androidx.room.Room
import com.ifedorov.recipesapp.data.api.RecipeApiService
import com.ifedorov.recipesapp.data.local.AppDatabase
import com.ifedorov.recipesapp.model.Category
import com.ifedorov.recipesapp.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RecipesRepository(context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(
            json.asConverterFactory(
                "application/json; charset=utf-8".toMediaType()
            )
        )
        .build()

    private val service: RecipeApiService = retrofit.create(RecipeApiService::class.java)

    private val db: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "database-recipes"
    ).build()

    private val categoriesDao = db.categoriesDao()
    private val recipesDao = db.recipesDao()

    suspend fun getCategories(): List<Category> =
        withContext(Dispatchers.IO) {
            service.getCategories()
        }

    suspend fun getRecipesByCategoryId(categoryId: Int): List<Recipe> =
        withContext(Dispatchers.IO) {
            service.getRecipesByCategoryId(categoryId)
        }

    suspend fun getRecipeById(recipeId: Int): Recipe =
        withContext(Dispatchers.IO) {
            service.getRecipeById(recipeId)
        }

    suspend fun getRecipesByIds(recipesIds: Set<Int>): List<Recipe> =
        withContext(Dispatchers.IO) {
            if (recipesIds.isEmpty()) {
                emptyList()
            } else {
                val idsString = recipesIds.joinToString(separator = ",")
                service.getRecipesByIds(idsString)
            }
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

    companion object {
        private const val BASE_URL = "https://recipes.androidsprint.ru/api/"
    }
}