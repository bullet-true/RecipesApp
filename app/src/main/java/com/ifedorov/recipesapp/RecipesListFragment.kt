package com.ifedorov.recipesapp

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.ifedorov.recipesapp.common.Constants
import com.ifedorov.recipesapp.databinding.FragmentRecipesListBinding

class RecipesListFragment : Fragment() {
    private var _binding: FragmentRecipesListBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("FragmentRecipesListBinding can't be null")

    private var categoryId: Int? = null
    private var categoryName: String? = null
    private var categoryImageUrl: String? = null

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

        categoryId = requireArguments().getInt(Constants.ARG_CATEGORY_ID)
        categoryName = requireArguments().getString(Constants.ARG_CATEGORY_NAME)
        categoryImageUrl = requireArguments().getString(Constants.ARG_CATEGORY_IMAGE_URL)

        setupHeader()
        initRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupHeader() {
        binding.tvCategoryHeader.text = categoryName
        binding.tvCategoryHeader.contentDescription = categoryName

        try {
            val image = requireContext().assets.open(categoryImageUrl ?: "").use { inputStream ->
                Drawable.createFromStream(inputStream, null)
            }
            binding.ivCategoryHeader.setImageDrawable(image)
        } catch (e: Exception) {
            Log.e("RecipesListFragment", "Error loading image: $categoryImageUrl")
            e.printStackTrace()
        }
    }

    private fun initRecycler() {
        val recipesListAdapter = RecipesListAdapter(STUB.getRecipesByCategoryId(categoryId ?: 0))

        recipesListAdapter.setOnItemClickListener(object : RecipesListAdapter.OnItemClickListener {
            override fun onItemClick(recipeId: Int) {
                openRecipeByRecipeId(recipeId)
            }
        })

        binding.rvCategory.adapter = recipesListAdapter
    }

    private fun openRecipeByRecipeId(recipeId: Int) {

        val recipe = STUB.getRecipeById(recipeId)
        val bundle = bundleOf(Constants.ARG_RECIPE to recipe)

        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace<RecipeFragment>(R.id.mainContainer, args = bundle)
            addToBackStack(null)
        }
    }
}