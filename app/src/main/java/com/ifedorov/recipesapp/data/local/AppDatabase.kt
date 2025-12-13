package com.ifedorov.recipesapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ifedorov.recipesapp.data.local.dao.CategoriesDao
import com.ifedorov.recipesapp.model.Category

@Database(entities = [Category::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
}