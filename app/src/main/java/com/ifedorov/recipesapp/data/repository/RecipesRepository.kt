package com.ifedorov.recipesapp.data.repository

import com.ifedorov.recipesapp.data.api.RecipeApiService
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

class RecipesRepository {
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

    companion object {
        private const val BASE_URL = "https://recipes.androidsprint.ru/api/"
    }
}