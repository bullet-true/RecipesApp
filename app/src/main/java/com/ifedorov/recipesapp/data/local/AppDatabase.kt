package com.ifedorov.recipesapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ifedorov.recipesapp.data.local.dao.CategoriesDao
import com.ifedorov.recipesapp.data.local.dao.RecipesDao
import com.ifedorov.recipesapp.model.Category
import com.ifedorov.recipesapp.model.Recipe

@Database(entities = [Category::class, Recipe::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
    abstract fun recipesDao(): RecipesDao
}