package com.ifedorov.recipesapp.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ifedorov.recipesapp.data.repository.RecipesRepository
import com.ifedorov.recipesapp.model.Category
import kotlinx.coroutines.launch

data class CategoriesListState(
    val isLoading: Boolean = false,
    val categoriesList: List<Category> = emptyList(),
    val error: String? = null,
)

class CategoriesListViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val repository = RecipesRepository(appContext)
    private val _state = MutableLiveData<CategoriesListState>()
        .apply { value = CategoriesListState() }
    val state: LiveData<CategoriesListState> get() = _state

    fun loadCategoriesList() {
        _state.value = _state.value?.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val cachedCategories = repository.getCategoriesFromCache()

                if (cachedCategories.isNotEmpty()) {
                    _state.value = _state.value?.copy(
                        categoriesList = cachedCategories
                    )
                }

                val categories = repository.getCategories()
                repository.saveCategoriesToCache(categories)

                _state.value = _state.value?.copy(
                    categoriesList = categories,
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