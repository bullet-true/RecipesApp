package com.ifedorov.recipesapp.ui.recipes.recipesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ifedorov.recipesapp.common.Constants
import com.ifedorov.recipesapp.databinding.FragmentRecipesListBinding

class RecipesListFragment : Fragment() {
    private var _binding: FragmentRecipesListBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("FragmentRecipesListBinding can't be null")

    private val viewModel: RecipesListViewModel by viewModels()
    private var recipesListAdapter = RecipesListAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipesListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryId = requireArguments().getInt(Constants.ARG_CATEGORY_ID)
        val categoryName = requireArguments().getString(Constants.ARG_CATEGORY_NAME)
        val categoryImageUrl = requireArguments().getString(Constants.ARG_CATEGORY_IMAGE_URL)

        viewModel.loadRecipesList(categoryId, categoryName, categoryImageUrl)
        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI() {
        binding.rvCategory.adapter = recipesListAdapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.ivCategoryHeader.setImageDrawable(state.categoryImage)
            binding.tvCategoryHeader.text = state.categoryName ?: ""
            binding.tvCategoryHeader.contentDescription = state.categoryName ?: ""
            recipesListAdapter.dataSet = state.recipeList
        }

        recipesListAdapter.setOnItemClickListener(object : RecipesListAdapter.OnItemClickListener {
            override fun onItemClick(recipeId: Int) {
                openRecipeByRecipeId(recipeId)
            }
        })
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        val action = RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment(recipeId)
        findNavController().navigate(action)
    }
}