package com.ifedorov.recipesapp.ui.recipes.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.ifedorov.recipesapp.R
import com.ifedorov.recipesapp.databinding.FragmentRecipeBinding

class RecipeFragment : Fragment() {
    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("FragmentRecipeBinding can't be null")

    private val viewModel: RecipeViewModel by viewModels()
    private var ingredientsAdapter = IngredientsAdapter(emptyList())
    private var methodAdapter = MethodAdapter(emptyList())
    private val args: RecipeFragmentArgs by navArgs()

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

        val recipeId = args.recipeId
        viewModel.loadRecipe(recipeId)

        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI() {
        binding.rvIngredients.adapter = ingredientsAdapter
        binding.rvMethod.adapter = methodAdapter

        val divider = MaterialDividerItemDecoration(
            requireContext(),
            MaterialDividerItemDecoration.VERTICAL
        ).apply {
            dividerThickness = resources.getDimensionPixelSize(R.dimen.divider_height)
            dividerColor =
                ContextCompat.getColor(requireContext(), R.color.divider_color)
            dividerInsetStart = resources.getDimensionPixelSize(R.dimen.spacing_small)
            dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.spacing_small)
            isLastItemDecorated = false
        }

        binding.rvIngredients.addItemDecoration(divider)
        binding.rvMethod.addItemDecoration(divider)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            state.recipe?.let { recipe ->
                binding.tvRecipeHeader.text = recipe.title
                binding.ivRecipeHeader.contentDescription = recipe.title
                binding.ivRecipeHeader.setImageDrawable(state.recipeImage)

                ingredientsAdapter.dataSet = recipe.ingredients
                ingredientsAdapter.notifyDataSetChanged()

                methodAdapter.dataSet = recipe.method
                methodAdapter.notifyDataSetChanged()

                binding.seekBarServings.progress = state.servings
                binding.tvServingsValue.text = state.servings.toString()

                ingredientsAdapter.updateIngredients(state.servings)
            }

            updateFavoriteIcon(state.isFavorite)

            if (state.error != null) {
                Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
            }
        }

        binding.imgBtnFavorite.setOnClickListener {
            viewModel.onFavoritesClicked()
        }

        binding.seekBarServings.setOnSeekBarChangeListener(
            PortionSeekBarListener { progress ->
                viewModel.updateServings(progress)
            }
        )
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val icon = if (isFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
        binding.imgBtnFavorite.setImageResource(icon)
    }
}

private class PortionSeekBarListener(val onChangeIngredients: (Int) -> Unit) :
    SeekBar.OnSeekBarChangeListener {

    override fun onProgressChanged(
        seekBar: SeekBar?,
        progress: Int,
        fromUser: Boolean
    ) {
        onChangeIngredients(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}