package com.ifedorov.recipesapp.ui.recipes.recipe

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ifedorov.recipesapp.R
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
            try {
                val favorites = getFavorites()
                val cachedRecipe = repository.getRecipeByIdFromCache(recipeId)?.copy(
                    isFavorite = favorites.contains(recipeId.toString())
                )

                cachedRecipe?.let {
                    _state.value = _state.value?.copy(
                        recipe = it,
                        servings = _state.value?.servings ?: 1,
                        recipeImageUrl = it.imageUrl,
                        error = null,
                    )
                }

                val recipe = repository.getRecipeById(recipeId).copy(
                    isFavorite = favorites.contains(recipeId.toString())
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
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun onFavoritesClicked() {
        _state.value?.recipe?.let {
            val favorites = getFavorites()
            val isFavorite = favorites.contains(it.id.toString())

            if (isFavorite) {
                favorites.remove(it.id.toString())
            } else {
                favorites.add(it.id.toString())
            }

            saveFavorites(favorites)

            _state.value = _state.value?.copy(
                recipe = it.copy(isFavorite = !isFavorite)
            )
        }
    }

    fun updateServings(count: Int) {
        _state.value = _state.value?.copy(servings = count)
    }

    private fun getFavorites(): MutableSet<String> {
        val sharedPrefs = appContext.getSharedPreferences(
            appContext.getString(R.string.preference_favorites_recipes),
            Context.MODE_PRIVATE
        )
        val savedSet = sharedPrefs.getStringSet(
            appContext.getString(R.string.saved_favorites_recipes),
            emptySet()
        )

        return HashSet(savedSet)
    }

    private fun saveFavorites(favoritesSet: Set<String>) {
        val sharedPrefs = appContext.getSharedPreferences(
            appContext.getString(R.string.preference_favorites_recipes),
            Context.MODE_PRIVATE
        )
        with(sharedPrefs.edit()) {
            putStringSet(appContext.getString(R.string.saved_favorites_recipes), favoritesSet)
            apply()
        }
    }
}