package com.ifedorov.recipesapp.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ifedorov.recipesapp.data.repository.RecipesRepository
import com.ifedorov.recipesapp.model.Category

data class CategoriesListState(
    val isLoading: Boolean = false,
    val categoriesList: List<Category> = emptyList(),
    val error: String? = null,
)

class CategoriesListViewModel : ViewModel() {
    private val repository = RecipesRepository()
    private val _state = MutableLiveData<CategoriesListState>()
        .apply { value = CategoriesListState() }
    val state: LiveData<CategoriesListState> get() = _state

    fun loadCategoriesList() {
        _state.value = _state.value?.copy(isLoading = true, error = null)

        repository.getCategories { result ->
            result.onSuccess { categories ->
                _state.postValue(
                    _state.value?.copy(
                        categoriesList = categories,
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