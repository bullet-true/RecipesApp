package com.ifedorov.recipesapp.di

import com.ifedorov.recipesapp.data.repository.RecipesRepository
import com.ifedorov.recipesapp.ui.recipes.recipesList.RecipesListViewModel

class RecipesListViewModelFactory(
    private val recipesRepository: RecipesRepository
) : Factory<RecipesListViewModel> {

    override fun create(): RecipesListViewModel {
        return RecipesListViewModel(recipesRepository)
    }
}