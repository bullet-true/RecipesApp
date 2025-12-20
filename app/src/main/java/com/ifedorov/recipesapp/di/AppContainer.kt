package com.ifedorov.recipesapp.di

import android.content.Context
import androidx.room.Room
import com.ifedorov.recipesapp.data.api.RecipeApiService
import com.ifedorov.recipesapp.data.local.AppDatabase
import com.ifedorov.recipesapp.data.repository.RecipesRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AppContainer(context: Context) {
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

    private val recipeApiService: RecipeApiService = retrofit.create(RecipeApiService::class.java)

    private val db: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "database-recipes"
    ).build()

    private val categoriesDao = db.categoriesDao()
    private val recipesDao = db.recipesDao()

    val repository = RecipesRepository(
        categoriesDao = categoriesDao,
        recipesDao = recipesDao,
        service = recipeApiService
    )

    val categoriesListViewModelFactory = CategoriesListViewModelFactory(repository)
    val recipesListViewModelFactory = RecipesListViewModelFactory(repository)
    val favoritesViewModelFactory = FavoritesViewModelFactory(repository)
    val recipeViewModelFactory = RecipeViewModelFactory(repository)

    companion object {
        private const val BASE_URL = "https://recipes.androidsprint.ru/api/"
    }
}