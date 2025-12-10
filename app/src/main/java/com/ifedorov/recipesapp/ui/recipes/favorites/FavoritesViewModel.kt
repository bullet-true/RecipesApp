package com.ifedorov.recipesapp.ui.recipes.favorites

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ifedorov.recipesapp.R
import com.ifedorov.recipesapp.data.repository.RecipesRepository
import com.ifedorov.recipesapp.model.Recipe

data class FavoritesState(
    val isLoading: Boolean = false,
    val favoritesRecipes: List<Recipe> = emptyList(),
    val error: String? = null,
)

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecipesRepository()
    private val appContext: Context = application.applicationContext

    private val _state = MutableLiveData<FavoritesState>()
        .apply { value = FavoritesState() }
    val state: LiveData<FavoritesState> get() = _state

    fun loadFavoritesRecipes() {
        _state.value = _state.value?.copy(error = null, isLoading = true)

        val favoritesStringSet = getFavorites()
        val favoritesIds: Set<Int> = favoritesStringSet.mapNotNull { it.toIntOrNull() }.toSet()

        repository.getRecipesByIds(favoritesIds) { result ->
            result.onSuccess { recipes ->
                _state.postValue(
                    _state.value?.copy(
                        favoritesRecipes = recipes,
                        error = null,
                        isLoading = false
                    )
                )
            }

            result.onFailure { throwable ->
                _state.postValue(
                    _state.value?.copy(
                        error = throwable.message,
                        isLoading = false
                    )
                )
            }
        }
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
}