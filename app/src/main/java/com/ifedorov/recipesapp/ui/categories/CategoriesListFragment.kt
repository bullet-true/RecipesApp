package com.ifedorov.recipesapp.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.ifedorov.recipesapp.R
import com.ifedorov.recipesapp.common.Constants
import com.ifedorov.recipesapp.databinding.FragmentListCategoriesBinding
import com.ifedorov.recipesapp.ui.recipes.recipesList.RecipesListFragment

class CategoriesListFragment : Fragment() {
    private var _binding: FragmentListCategoriesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("FragmentListCategoriesBinding can't be null")

    private val viewModel: CategoriesListViewModel by viewModels()
    private var categoriesListAdapter = CategoriesListAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListCategoriesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadCategoriesList()
        binding.rvCategories.adapter = categoriesListAdapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            categoriesListAdapter.dataSet = state.categoriesList
        }

        categoriesListAdapter.setOnItemClickListener(object :
            CategoriesListAdapter.OnItemClickListener {
            override fun onItemClick(categoryId: Int) {
                openRecipesByCategoryId(categoryId)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openRecipesByCategoryId(categoryId: Int) {
        val category = viewModel.state.value?.categoriesList?.find { it.id == categoryId }
        val categoryName = category?.title
        val categoryImageUrl = category?.imageUrl

        val bundle = bundleOf(
            Constants.ARG_CATEGORY_ID to categoryId,
            Constants.ARG_CATEGORY_NAME to categoryName,
            Constants.ARG_CATEGORY_IMAGE_URL to categoryImageUrl
        )

        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace<RecipesListFragment>(R.id.mainContainer, args = bundle)
            addToBackStack(null)
        }
    }
}