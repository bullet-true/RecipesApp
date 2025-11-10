package com.ifedorov.recipesapp.ui.recipes.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ifedorov.recipesapp.model.Recipe

data class RecipeUiState(
    val recipe: Recipe? = null,
    val servings: Int = 1,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
)

class RecipeViewModel : ViewModel() {

    private val _state = MutableLiveData<RecipeUiState>()
    val state: LiveData<RecipeUiState> get() = _state

    init {
        Log.i("!!!", "RecipeViewModel init")
        _state.value = RecipeUiState().copy(isFavorite = true)
    }
}