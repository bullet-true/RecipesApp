package com.ifedorov.recipesapp.data.repository

import com.ifedorov.recipesapp.data.api.RecipeApiService
import com.ifedorov.recipesapp.model.Category
import com.ifedorov.recipesapp.model.Recipe
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RecipesRepository {
    private val threadPool: ExecutorService = Executors.newFixedThreadPool(4)
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

    fun getCategories(callback: (Result<List<Category>>) -> Unit) {
        threadPool.execute {
            try {
                val response = service.getCategories().execute()

                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    callback(Result.success(body))
                } else {
                    callback(
                        Result.failure(
                            Exception(
                                "HTTP response error in getCategories(). " +
                                        "Code: ${response.code()}. Message:  ${response.message()}"
                            )
                        )
                    )
                }

            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    fun getRecipesByCategoryId(categoryId: Int, callback: (Result<List<Recipe>>) -> Unit) {
        threadPool.execute {
            try {
                val response = service.getRecipesByCategoryId(categoryId).execute()

                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    callback(Result.success(body))
                } else {
                    callback(
                        Result.failure(
                            Exception(
                                "HTTP response error in getRecipesByCategoryId. " +
                                        "Code: ${response.code()}. Message:  ${response.message()}"
                            )
                        )
                    )
                }

            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    fun getRecipeById(recipeId: Int, callback: (Result<Recipe?>) -> Unit) {
        threadPool.execute {
            try {
                val response = service.getRecipeById(recipeId).execute()

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        callback(Result.success(body))
                    } else {
                        callback(Result.failure(Exception("Recipe with ID:$recipeId not found")))
                    }

                } else {
                    callback(
                        Result.failure(
                            Exception(
                                "HTTP response error in getRecipeById. " +
                                        "Code: ${response.code()}. Message:  ${response.message()}"
                            )
                        )
                    )
                }

            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    fun getRecipesByIds(recipesIds: Set<Int>, callback: (Result<List<Recipe>>) -> Unit) {
        threadPool.execute {
            try {
                if (recipesIds.isEmpty()) {
                    callback(Result.success(emptyList()))
                } else {
                    val idsString = recipesIds.joinToString(separator = ",")
                    val response = service.getRecipesByIds(idsString).execute()

                    if (response.isSuccessful) {
                        val body = response.body() ?: emptyList()
                        callback(Result.success(body))
                    } else {
                        callback(
                            Result.failure(
                                Exception(
                                    "HTTP response error in getRecipesByIds. " +
                                            "Code: ${response.code()}. Message:  ${response.message()}"
                                )
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    companion object {
        private const val BASE_URL = "https://recipes.androidsprint.ru/api/"
    }
}