package com.ifedorov.recipesapp.ui.recipes.recipesList

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ifedorov.recipesapp.data.STUB
import com.ifedorov.recipesapp.model.Recipe

data class RecipesListUiState(
    val categoryId: Int = 0,
    val categoryTitle: String? = null,
    val categoryDescription: String? = null,
    val categoryImage: Drawable? = null,
    val isLoading: Boolean = false,
    val recipeList: List<Recipe> = emptyList()
)

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext

    private val _state = MutableLiveData<RecipesListUiState>()
        .apply { value = RecipesListUiState() }
    val state: LiveData<RecipesListUiState> get() = _state

    fun loadRecipesList(
        categoryId: Int,
        categoryTitle: String?,
        categoryDescription: String?,
        categoryImageUrl: String?
    ) {
        val recipesList = STUB.getRecipesByCategoryId(categoryId)
        var categoryImage: Drawable? = null

        try {
            categoryImage = appContext.assets.open(categoryImageUrl ?: "").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
        } catch (e: Exception) {
            Log.e(
                "RecipesListViewModel",
                "Error loading image in loadRecipesList() function: $categoryImageUrl"
            )
            e.printStackTrace()
        }

        _state.value = _state.value?.copy(
            categoryId = categoryId,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryImage = categoryImage,
            recipeList = recipesList
        )
    }
}