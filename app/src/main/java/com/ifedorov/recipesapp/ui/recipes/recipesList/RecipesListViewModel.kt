package com.ifedorov.recipesapp.ui.recipes.recipesList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ifedorov.recipesapp.data.repository.RecipesRepository
import com.ifedorov.recipesapp.model.Category
import com.ifedorov.recipesapp.model.Recipe
import kotlinx.coroutines.launch

data class RecipesListUiState(
    val category: Category? = null,
    val categoryImageUrl: String? = null,
    val isLoading: Boolean = false,
    val recipesList: List<Recipe> = emptyList(),
    val error: String? = null,
)

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val repository = RecipesRepository(appContext)

    private val _state = MutableLiveData<RecipesListUiState>()
        .apply { value = RecipesListUiState() }
    val state: LiveData<RecipesListUiState> get() = _state

    fun loadRecipesList(category: Category) {
        _state.value = _state.value?.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val imageUrl = category.imageUrl
            val cachedRecipes = repository.getRecipesByCategoryIdFromCache(category.id)

            _state.value = _state.value?.copy(
                category = category,
                categoryImageUrl = imageUrl,
                recipesList = cachedRecipes
            )

            try {
                val remoteRecipes = repository.getRecipesByCategoryId(category.id)

                val recipes = remoteRecipes.map { remoteRecipe ->
                    val cachedRecipe = cachedRecipes.find { it.id == remoteRecipe.id }

                    remoteRecipe.copy(
                        categoryId = category.id,
                        isFavorite = cachedRecipe?.isFavorite ?: remoteRecipe.isFavorite
                    )
                }

                repository.saveRecipesToCache(recipes)

                _state.value = _state.value?.copy(
                    category = category,
                    categoryImageUrl = imageUrl,
                    recipesList = recipes,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value?.copy(
                    recipesList = cachedRecipes,
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
}