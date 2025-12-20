package com.ifedorov.recipesapp.di

import com.ifedorov.recipesapp.data.repository.RecipesRepository
import com.ifedorov.recipesapp.ui.recipes.recipe.RecipeViewModel

class RecipeViewModelFactory(
    private val recipesRepository: RecipesRepository
) : Factory<RecipeViewModel> {

    override fun create(): RecipeViewModel {
        return RecipeViewModel(recipesRepository)
    }
}