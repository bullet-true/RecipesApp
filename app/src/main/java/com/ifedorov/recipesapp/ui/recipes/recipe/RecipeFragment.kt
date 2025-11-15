package com.ifedorov.recipesapp.ui.recipes.recipe

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.ifedorov.recipesapp.R
import com.ifedorov.recipesapp.common.Constants
import com.ifedorov.recipesapp.databinding.FragmentRecipeBinding
import com.ifedorov.recipesapp.model.Recipe

class RecipeFragment : Fragment() {
    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("FragmentRecipeBinding can't be null")

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = requireArguments().getInt(Constants.ARG_RECIPE_ID)
        viewModel.loadRecipe(recipeId)

        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            state.recipe?.let { recipe ->
                binding.tvRecipeHeader.text = recipe.title
                binding.ivRecipeHeader.contentDescription = recipe.title

                try {
                    val image = requireContext().assets.open(recipe.imageUrl).use { inputStream ->
                        Drawable.createFromStream(inputStream, null)
                    }
                    binding.ivRecipeHeader.setImageDrawable(image)

                } catch (e: Exception) {
                    Log.e("RecipeFragment", "Error loading image: ${recipe.imageUrl}")
                    e.printStackTrace()
                }

            }

            initRecycler(state.recipe)
            updateFavoriteIcon(state.isFavorite)

            binding.imgBtnFavorite.setOnClickListener {
                viewModel.onFavoritesClicked()
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val icon = if (isFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
        binding.imgBtnFavorite.setImageResource(icon)
    }

    private fun initRecycler(recipe: Recipe?) {
        recipe?.let {
            val ingredientsAdapter = IngredientsAdapter(it.ingredients)
            binding.rvIngredients.adapter = ingredientsAdapter
            binding.rvMethod.adapter = MethodAdapter(it.method)

            val divider = MaterialDividerItemDecoration(
                requireContext(),
                MaterialDividerItemDecoration.VERTICAL
            ).apply {
                dividerThickness = resources.getDimensionPixelSize(R.dimen.divider_height)
                dividerColor = ContextCompat.getColor(requireContext(), R.color.divider_color)
                dividerInsetStart = resources.getDimensionPixelSize(R.dimen.spacing_small)
                dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.spacing_small)
                isLastItemDecorated = false
            }

            binding.rvIngredients.addItemDecoration(divider)
            binding.rvMethod.addItemDecoration(divider)

            var currentServings = binding.seekBarServings.progress
            binding.tvServingsValue.text = currentServings.toString()

            binding.seekBarServings.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    currentServings = progress
                    binding.tvServingsValue.text = currentServings.toString()
                    ingredientsAdapter.updateIngredients(currentServings)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }
}