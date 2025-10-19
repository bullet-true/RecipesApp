package com.ifedorov.recipesapp

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.ifedorov.recipesapp.common.Constants
import com.ifedorov.recipesapp.databinding.FragmentRecipeBinding
import com.ifedorov.recipesapp.model.Recipe

class RecipeFragment : Fragment() {
    private var _binding: FragmentRecipeBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("FragmentRecipeBinding can't be null")

    private var recipe: Recipe? = null

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

        recipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(Constants.ARG_RECIPE, Recipe::class.java)
        } else {
            requireArguments().getParcelable(Constants.ARG_RECIPE)
        }

        initUI()
        initRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI() {
        recipe?.let {
            binding.tvRecipeHeader.text = it.title
            binding.ivRecipeHeader.contentDescription = it.title

            try {
                val image = requireContext().assets.open(it.imageUrl).use { inputStream ->
                    Drawable.createFromStream(inputStream, null)
                }
                binding.ivRecipeHeader.setImageDrawable(image)
            } catch (e: Exception) {
                Log.e("RecipeFragment", "Error loading image: ${it.imageUrl}")
                e.printStackTrace()
            }
        }
    }

    private fun initRecycler() {
        recipe?.let {
            binding.rvIngredients.adapter = IngredientsAdapter(it.ingredients)
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
        }
    }
}