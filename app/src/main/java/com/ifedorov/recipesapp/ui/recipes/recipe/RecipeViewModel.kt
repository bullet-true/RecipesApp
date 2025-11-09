package com.ifedorov.recipesapp.ui.recipes.recipe

import androidx.lifecycle.ViewModel
import com.ifedorov.recipesapp.model.Recipe

data class RecipeUiState(
    val recipe: Recipe? = null,
    val servings: Int = 1,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
)

class RecipeViewModel : ViewModel() {
}