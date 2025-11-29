package com.ifedorov.recipesapp.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ifedorov.recipesapp.data.STUB
import com.ifedorov.recipesapp.model.Category

data class CategoriesListState(
    val isLoading: Boolean = false,
    val categoriesList: List<Category> = emptyList(),
)

class CategoriesListViewModel : ViewModel() {
    private val _state = MutableLiveData<CategoriesListState>()
        .apply { value = CategoriesListState() }
    val state: LiveData<CategoriesListState> get() = _state

    fun loadCategoriesList() {
        val categories = STUB.getCategories()
        _state.value = _state.value?.copy(
            categoriesList = categories
        )
    }
}