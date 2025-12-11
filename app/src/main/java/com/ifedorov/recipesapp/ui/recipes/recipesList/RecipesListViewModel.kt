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
    private val repository = RecipesRepository()

    private val _state = MutableLiveData<RecipesListUiState>()
        .apply { value = RecipesListUiState() }
    val state: LiveData<RecipesListUiState> get() = _state

    fun loadRecipesList(category: Category) {
        _state.value = _state.value?.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = repository.getRecipesByCategoryId(category.id)
            val imageUrl = category.imageUrl

            result.onSuccess { recipes ->
                _state.value = _state.value?.copy(
                    category = category,
                    categoryImageUrl = imageUrl,
                    recipesList = recipes,
                    isLoading = false
                )
            }

            result.onFailure { throwable ->
                _state.value = _state.value?.copy(
                    error = throwable.message,
                    isLoading = false
                )
            }
        }
    }
}