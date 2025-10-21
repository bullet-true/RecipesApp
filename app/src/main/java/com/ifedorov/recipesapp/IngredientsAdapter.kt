package com.ifedorov.recipesapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ifedorov.recipesapp.databinding.ItemIngredientBinding
import com.ifedorov.recipesapp.model.Ingredient

class IngredientsAdapter(private val dataSet: List<Ingredient>) :
    RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    private var quantity = 1

    class ViewHolder(val binding: ItemIngredientBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemIngredientBinding.inflate(inflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val ingredient = dataSet[position]
        with(viewHolder.binding) {
            tvdDescription.text = ingredient.description

            val ingredientQuantity = ingredient.quantity.toDoubleOrNull()
            val totalQuantity = ingredientQuantity?.let {
                formatQuantity(it * quantity)
            } ?: ingredient.quantity

            tvQuantity.text = buildString {
                append(totalQuantity)
                append(" ")
                append(ingredient.unitOfMeasure)
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    fun updateIngredients(progress: Int) {
        quantity = progress
        notifyDataSetChanged()
    }

    private fun formatQuantity(value: Double): String = when {
        value % 1.0 == 0.0 -> value.toInt().toString()
        else -> "%.1f".format(value)
    }
}