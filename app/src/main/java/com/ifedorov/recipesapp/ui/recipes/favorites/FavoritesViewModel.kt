package com.ifedorov.recipesapp.ui.recipes.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifedorov.recipesapp.data.repository.RecipesRepository
import com.ifedorov.recipesapp.model.Recipe
import kotlinx.coroutines.launch

data class FavoritesState(
    val isLoading: Boolean = false,
    val favoritesRecipes: List<Recipe> = emptyList(),
    val error: String? = null,
)

class FavoritesViewModel(private val repository: RecipesRepository) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
        .apply { value = FavoritesState() }
    val state: LiveData<FavoritesState> get() = _state

    fun loadFavoritesRecipes() {
        _state.value = _state.value?.copy(error = null, isLoading = true)

        viewModelScope.launch {
            try {
                val cachedFavorites = repository.getFavoritesFromCache()

                _state.value = _state.value?.copy(
                    favoritesRecipes = cachedFavorites,
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
}