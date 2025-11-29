package com.ifedorov.recipesapp.ui.recipes.favorites

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ifedorov.recipesapp.R
import com.ifedorov.recipesapp.data.STUB
import com.ifedorov.recipesapp.model.Recipe

data class FavoritesState(
    val isLoading: Boolean = false,
    val favoritesRecipes: List<Recipe> = emptyList(),
)

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext: Context = application.applicationContext

    private val _state = MutableLiveData<FavoritesState>()
        .apply { value = FavoritesState() }
    val state: LiveData<FavoritesState> get() = _state

    fun loadFavoritesRecipes() {
        val favoritesStringSet = getFavorites()
        val favoritesIds: Set<Int> = favoritesStringSet.mapNotNull { it.toIntOrNull() }.toSet()
        val favoritesRecipes = STUB.getRecipesByIds(favoritesIds)

        _state.value = _state.value?.copy(
            favoritesRecipes = favoritesRecipes
        )
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