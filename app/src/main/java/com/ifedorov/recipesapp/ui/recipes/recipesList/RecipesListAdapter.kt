package com.ifedorov.recipesapp.ui.recipes.recipesList

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ifedorov.recipesapp.databinding.ItemRecipeBinding
import com.ifedorov.recipesapp.model.Recipe

class RecipesListAdapter(var dataSet: List<Recipe>) :
    RecyclerView.Adapter<RecipesListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(recipeId: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class ViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemRecipeBinding.inflate(inflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val recipe = dataSet[position]

        with(viewHolder.binding) {
            ivRecipeCardItem.contentDescription = recipe.title
            tvRecipeCardItemHeader.text = recipe.title
        }

        try {
            val image =
                viewHolder.itemView.context.assets.open(recipe.imageUrl).use { inputStream ->
                    Drawable.createFromStream(inputStream, null)
                }
            viewHolder.binding.ivRecipeCardItem.setImageDrawable(image)
        } catch (e: Exception) {
            Log.e("RecipesListAdapter", "Error loading image: ${recipe.imageUrl}")
            e.printStackTrace()
        }

        viewHolder.binding.cardRecipeItemLayout.setOnClickListener {
            itemClickListener?.onItemClick(recipe.id)
        }
    }

    override fun getItemCount(): Int = dataSet.size
}