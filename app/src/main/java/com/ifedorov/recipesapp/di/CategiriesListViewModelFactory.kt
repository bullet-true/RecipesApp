package com.ifedorov.recipesapp.di

import com.ifedorov.recipesapp.data.repository.RecipesRepository
import com.ifedorov.recipesapp.ui.categories.CategoriesListViewModel

class CategoriesListViewModelFactory(
    private val recipesRepository: RecipesRepository
) : Factory<CategoriesListViewModel> {

    override fun create(): CategoriesListViewModel {
        return CategoriesListViewModel(recipesRepository)
    }
}