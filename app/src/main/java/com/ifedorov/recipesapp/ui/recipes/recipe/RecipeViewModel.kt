package com.ifedorov.recipesapp.ui.recipes.recipe

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ifedorov.recipesapp.R
import com.ifedorov.recipesapp.data.STUB
import com.ifedorov.recipesapp.model.Recipe

data class RecipeUiState(
    val recipe: Recipe? = null,
    val servings: Int = 1,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val recipeImage: Drawable? = null,
)

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext

    private val _state = MutableLiveData<RecipeUiState>().apply { value = RecipeUiState() }
    val state: LiveData<RecipeUiState> get() = _state

    fun loadRecipe(recipeId: Int) {
        // TODO("load from network")

        val recipe = STUB.getRecipeById(recipeId)
        var recipeImage: Drawable? = null

        recipe?.let {
            try {
                recipeImage = appContext.assets.open(it.imageUrl).use { inputStream ->
                    Drawable.createFromStream(inputStream, null)
                }
            } catch (e: Exception) {
                Log.e(
                    "RecipeViewModel",
                    "Error loading image in loadRecipe() function: ${it.imageUrl} "
                )
                e.printStackTrace()
            }
        }

        _state.value = _state.value?.copy(
            recipe = recipe,
            isFavorite = getFavorites().contains(recipeId.toString()),
            servings = _state.value?.servings ?: 1,
            recipeImage = recipeImage
        )
    }

    fun onFavoritesClicked() {
        val recipeId = _state.value?.recipe?.id
        val favorites = getFavorites()
        val isFavorite = favorites.contains(recipeId.toString())

        if (isFavorite) {
            favorites.remove(recipeId.toString())
        } else {
            favorites.add(recipeId.toString())
        }

        saveFavorites(favorites)
        _state.value = _state.value?.copy(isFavorite = !isFavorite)
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