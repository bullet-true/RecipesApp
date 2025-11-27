package com.ifedorov.recipesapp.ui.recipes.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.ifedorov.recipesapp.R
import com.ifedorov.recipesapp.common.Constants
import com.ifedorov.recipesapp.databinding.FragmentFavoritesBinding
import com.ifedorov.recipesapp.ui.recipes.recipe.RecipeFragment
import com.ifedorov.recipesapp.ui.recipes.recipesList.RecipesListAdapter

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("FragmentFavoritesBinding can't be null")

    private val viewModel: FavoritesViewModel by viewModels()
    private var favoritesAdapter = RecipesListAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadFavoritesRecipes()
        binding.rvFavorites.adapter = favoritesAdapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.rvFavorites.isVisible = state.isNotEmpty()
            binding.tvEmptyFavorites.isVisible = state.isEmpty()
            favoritesAdapter.dataSet = state
        }

        favoritesAdapter.setOnItemClickListener(object : RecipesListAdapter.OnItemClickListener {
            override fun onItemClick(recipeId: Int) {
                openRecipeByRecipeId(recipeId)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        val bundle = bundleOf(Constants.ARG_RECIPE_ID to recipeId)

        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace<RecipeFragment>(R.id.mainContainer, args = bundle)
            addToBackStack(null)
        }
    }
}