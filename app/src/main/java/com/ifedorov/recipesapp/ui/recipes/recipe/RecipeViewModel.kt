package com.ifedorov.recipesapp.ui.recipes.recipe

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ifedorov.recipesapp.data.repository.RecipesRepository
import com.ifedorov.recipesapp.model.Recipe
import kotlinx.coroutines.launch

data class RecipeUiState(
    val recipe: Recipe? = null,
    val servings: Int = 1,
    val isLoading: Boolean = false,
    val recipeImageUrl: String? = null,
    val error: String? = null,
)

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext
    private val repository = RecipesRepository(appContext)

    private val _state = MutableLiveData<RecipeUiState>().apply { value = RecipeUiState() }
    val state: LiveData<RecipeUiState> get() = _state

    fun loadRecipe(recipeId: Int) {
        _state.value = _state.value?.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val cachedRecipe = repository.getRecipeByIdFromCache(recipeId)
            val isFavorite = cachedRecipe?.isFavorite

            cachedRecipe?.let { recipe ->
                _state.value = _state.value?.copy(
                    recipe = recipe,
                    servings = _state.value?.servings ?: 1,
                    recipeImageUrl = recipe.imageUrl,
                    error = null,
                )
            }

            try {
                val recipeFromApi = repository.getRecipeById(recipeId)
                val recipe = recipeFromApi.copy(
                    isFavorite = isFavorite ?: recipeFromApi.isFavorite
                )

                repository.saveRecipesToCache(listOf(recipe))

                _state.value = _state.value?.copy(
                    recipe = recipe,
                    servings = _state.value?.servings ?: 1,
                    recipeImageUrl = recipe.imageUrl,
                    error = null,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value?.copy(
                    recipe = cachedRecipe,
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun onFavoritesClicked() {
        _state.value?.recipe?.let { recipe ->
            val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)

            _state.value = _state.value?.copy(
                recipe = updatedRecipe
            )

            viewModelScope.launch {
                repository.saveRecipesToCache(listOf(updatedRecipe))
            }
        }
    }

    fun updateServings(count: Int) {
        _state.value = _state.value?.copy(servings = count)
    }
}