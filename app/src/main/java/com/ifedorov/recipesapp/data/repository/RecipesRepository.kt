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

    suspend fun getCategories(): Result<List<Category>> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getCategories().execute()
                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception(
                            "HTTP response error in getCategories(). " +
                                    "Code: ${response.code()}. Message:  ${response.message()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getRecipesByCategoryId(categoryId: Int): Result<List<Recipe>> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getRecipesByCategoryId(categoryId).execute()

                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception(
                            "HTTP response error in getRecipesByCategoryId. " +
                                    "Code: ${response.code()}. Message:  ${response.message()}"
                        )
                    )
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    suspend fun getRecipeById(recipeId: Int): Result<Recipe?> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.getRecipeById(recipeId).execute()

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception("Recipe with ID:$recipeId not found"))
                    }

                } else {
                    Result.failure(
                        Exception(
                            "HTTP response error in getRecipeById. " +
                                    "Code: ${response.code()}. Message:  ${response.message()}"
                        )
                    )
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    suspend fun getRecipesByIds(recipesIds: Set<Int>): Result<List<Recipe>> =
        withContext(Dispatchers.IO) {
            try {
                if (recipesIds.isEmpty()) {
                    Result.success(emptyList())
                } else {
                    val idsString = recipesIds.joinToString(separator = ",")
                    val response = service.getRecipesByIds(idsString).execute()

                    if (response.isSuccessful) {
                        val body = response.body() ?: emptyList()
                        Result.success(body)
                    } else {
                        Result.failure(
                            Exception(
                                "HTTP response error in getRecipesByIds. " +
                                        "Code: ${response.code()}. Message:  ${response.message()}"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    companion object {
        private const val BASE_URL = "https://recipes.androidsprint.ru/api/"
    }
}