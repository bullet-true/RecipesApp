package com.ifedorov.recipesapp.di

import com.ifedorov.recipesapp.data.repository.RecipesRepository
import com.ifedorov.recipesapp.ui.recipes.favorites.FavoritesViewModel

class FavoritesViewModelFactory(
    private val recipesRepository: RecipesRepository
) : Factory<FavoritesViewModel> {

    override fun create(): FavoritesViewModel {
        return FavoritesViewModel(recipesRepository)
    }
}