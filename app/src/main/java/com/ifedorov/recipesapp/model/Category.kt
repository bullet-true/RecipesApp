package com.ifedorov.recipesapp.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
) : Parcelable