package com.ifedorov.recipesapp.ui.recipes.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ifedorov.RecipeApplication
import com.ifedorov.recipesapp.databinding.FragmentFavoritesBinding
import com.ifedorov.recipesapp.ui.recipes.recipesList.RecipesListAdapter

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("FragmentFavoritesBinding can't be null")

    private lateinit var viewModel: FavoritesViewModel
    private var favoritesAdapter = RecipesListAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (requireActivity().application as RecipeApplication).appContainer
        viewModel = appContainer.favoritesViewModelFactory.create()
    }

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
            binding.rvFavorites.isVisible = state.favoritesRecipes.isNotEmpty()
            binding.tvEmptyFavorites.isVisible = state.favoritesRecipes.isEmpty()

            favoritesAdapter.dataSet = state.favoritesRecipes
            favoritesAdapter.notifyDataSetChanged()

            if (state.error != null) {
                Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
            }
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
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToRecipeFragment(recipeId)
        findNavController().navigate(action)
    }
}