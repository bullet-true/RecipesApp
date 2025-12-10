package com.ifedorov.recipesapp.ui.recipes.recipesList

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ifedorov.recipesapp.data.repository.RecipesRepository
import com.ifedorov.recipesapp.model.Category
import com.ifedorov.recipesapp.model.Recipe

data class RecipesListUiState(
    val category: Category? = null,
    val categoryImageUrl: String? = null,
    val isLoading: Boolean = false,
    val recipesList: List<Recipe> = emptyList(),
    val error: String? = null,
)

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecipesRepository()
    private val appContext: Context = application.applicationContext

    private val _state = MutableLiveData<RecipesListUiState>()
        .apply { value = RecipesListUiState() }
    val state: LiveData<RecipesListUiState> get() = _state

    fun loadRecipesList(category: Category) {
        _state.value = _state.value?.copy(isLoading = true, error = null)

        repository.getRecipesByCategoryId(category.id) { result ->
            val imageUrl = category.imageUrl

            result.onSuccess { recipes ->
                _state.postValue(
                    _state.value?.copy(
                        category = category,
                        categoryImageUrl = imageUrl,
                        recipesList = recipes,
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
}