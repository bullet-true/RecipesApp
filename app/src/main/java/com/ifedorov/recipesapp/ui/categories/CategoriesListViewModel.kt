package com.ifedorov.recipesapp.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ifedorov.recipesapp.data.STUB
import com.ifedorov.recipesapp.model.Category

class CategoriesListViewModel : ViewModel() {
    private val _state = MutableLiveData<List<Category>>().apply { value = emptyList() }
    val state: LiveData<List<Category>> get() = _state

    fun loadCategoriesList() {
        _state.value = STUB.getCategories()
    }
}