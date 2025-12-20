package com.ifedorov.recipesapp.ui.recipes.recipesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ifedorov.RecipeApplication
import com.ifedorov.recipesapp.R
import com.ifedorov.recipesapp.common.Constants.IMAGES_BASE_URL
import com.ifedorov.recipesapp.databinding.FragmentRecipesListBinding

class RecipesListFragment : Fragment() {
    private var _binding: FragmentRecipesListBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("FragmentRecipesListBinding can't be null")

    private lateinit var viewModel: RecipesListViewModel
    private var recipesListAdapter = RecipesListAdapter(emptyList())
    private val args: RecipesListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (requireActivity().application as RecipeApplication).appContainer
        viewModel = appContainer.recipesListViewModelFactory.create()
    }

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

        val category = args.category
        viewModel.loadRecipesList(category)

        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI() {
        binding.rvCategory.adapter = recipesListAdapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            state.categoryImageUrl?.let { url ->
                Glide
                    .with(this)
                    .load(IMAGES_BASE_URL + url)
                    .centerCrop()
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_error)
                    .into(binding.ivCategoryHeader)
            }

            binding.tvCategoryHeader.text = state.category?.title ?: ""
            binding.tvCategoryHeader.contentDescription = state.category?.title ?: ""

            recipesListAdapter.dataSet = state.recipesList
            recipesListAdapter.notifyDataSetChanged()

            if (state.error != null) {
                Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
            }
        }

        recipesListAdapter.setOnItemClickListener(object : RecipesListAdapter.OnItemClickListener {
            override fun onItemClick(recipeId: Int) {
                openRecipeByRecipeId(recipeId)
            }
        })
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        val action =
            RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment(recipeId)

        findNavController().navigate(action)
    }
}