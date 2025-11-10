package com.ifedorov.recipesapp.ui.recipes.recipe

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
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

    private var recipe: Recipe? = null
    private var isFavorite = false

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

        viewModel.state.observe(viewLifecycleOwner) { state ->
            Log.i("!!!", "isFavorite = ${state.isFavorite}")
        }

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

            val favorites = getFavorites()
            isFavorite = favorites.contains(it.id.toString())
        }

        updateFavoriteIcon()

        binding.imgBtnFavorite.setOnClickListener {
            recipe?.let {
                val favorites = getFavorites()

                if (isFavorite) {
                    favorites.remove(it.id.toString())
                } else {
                    favorites.add(it.id.toString())
                }

                saveFavorites(favorites)
            }

            isFavorite = !isFavorite
            updateFavoriteIcon()
        }
    }

    private fun updateFavoriteIcon() {
        val icon = if (isFavorite) R.drawable.ic_heart else R.drawable.ic_heart_empty
        binding.imgBtnFavorite.setImageResource(icon)
    }

    private fun saveFavorites(favoritesSet: Set<String>) {
        val sharedPrefs = requireContext().getSharedPreferences(
            getString(R.string.preference_favorites_recipes),
            Context.MODE_PRIVATE
        )
        with(sharedPrefs.edit()) {
            putStringSet(getString(R.string.saved_favorites_recipes), favoritesSet)
            apply()
        }
    }

    private fun getFavorites(): MutableSet<String> {
        val sharedPrefs = requireContext().getSharedPreferences(
            getString(R.string.preference_favorites_recipes),
            Context.MODE_PRIVATE
        )
        val savedSet = sharedPrefs.getStringSet(
            getString(R.string.saved_favorites_recipes),
            emptySet()
        )

        return HashSet(savedSet)
    }

    private fun initRecycler() {
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