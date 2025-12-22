package com.ifedorov.recipesapp.di

import android.content.Context
import androidx.room.Room
import com.ifedorov.recipesapp.common.Constants.BASE_URL
import com.ifedorov.recipesapp.data.api.RecipeApiService
import com.ifedorov.recipesapp.data.local.AppDatabase
import com.ifedorov.recipesapp.data.local.dao.CategoriesDao
import com.ifedorov.recipesapp.data.local.dao.RecipesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RecipeModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "database-recipes"
        ).build()

    @Provides
    fun provideCategoriesDao(appDatabase: AppDatabase): CategoriesDao = appDatabase.categoriesDao()

    @Provides
    fun provideRecipesDao(appDatabase: AppDatabase): RecipesDao = appDatabase.recipesDao()

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return client
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json; charset=utf-8".toMediaType()
                )
            )
            .build()

        return retrofit
    }

    @Provides
    @Singleton
    fun provideRecipeApiService(retrofit: Retrofit): RecipeApiService {
        return retrofit.create(RecipeApiService::class.java)
    }
}