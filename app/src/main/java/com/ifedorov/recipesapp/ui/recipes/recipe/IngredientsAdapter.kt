package com.ifedorov.recipesapp.ui.recipes.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ifedorov.recipesapp.databinding.ItemIngredientBinding
import com.ifedorov.recipesapp.model.Ingredient
import java.math.RoundingMode

class IngredientsAdapter(var dataSet: List<Ingredient>) :
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

            val totalQuantity = ingredient.quantity.toBigDecimalOrNull()
                ?.multiply(quantity.toBigDecimal())
                ?.setScale(1, RoundingMode.HALF_UP)
                ?.stripTrailingZeros()
                ?.toPlainString()
                ?: ingredient.quantity

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
}