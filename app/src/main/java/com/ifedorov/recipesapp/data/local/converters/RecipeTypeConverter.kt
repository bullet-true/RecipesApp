package com.ifedorov.recipesapp.data.local.converters

import androidx.room.TypeConverter
import com.ifedorov.recipesapp.model.Ingredient
import kotlinx.serialization.json.Json

class RecipeTypeConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun ingredientsToString(list: List<Ingredient>): String =
        json.encodeToString(list)

    @TypeConverter
    fun stringToIngredients(string: String): List<Ingredient> =
        json.decodeFromString(string)

    @TypeConverter
    fun methodToString(list: List<String>): String =
        json.encodeToString(list)

    @TypeConverter
    fun stringToMethod(string: String): List<String> =
        json.decodeFromString(string)
}