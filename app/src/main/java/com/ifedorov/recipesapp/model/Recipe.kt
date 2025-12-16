package com.ifedorov.recipesapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ifedorov.recipesapp.data.local.converters.RecipeTypeConverter
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@TypeConverters(RecipeTypeConverter::class)
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val id: Int,
    val title: String,
    val ingredients: List<Ingredient>,
    val method: List<String>,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "category_id") val categoryId: Int = 0,
) : Parcelable