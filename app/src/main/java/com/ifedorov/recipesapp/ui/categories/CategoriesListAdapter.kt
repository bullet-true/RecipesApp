package com.ifedorov.recipesapp.ui.categories

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ifedorov.recipesapp.databinding.ItemCategoryBinding
import com.ifedorov.recipesapp.model.Category

class CategoriesListAdapter(private val dataSet: List<Category>) :
    RecyclerView.Adapter<CategoriesListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(categoryId: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemCategoryBinding.inflate(inflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val category = dataSet[position]

        with(viewHolder.binding) {
            ivCardItem.contentDescription = category.title
            tvCardItemHeader.text = category.title
            tvDescription.text = category.description
        }

        try {
            val image =
                viewHolder.itemView.context.assets.open(category.imageUrl).use { inputStream ->
                    Drawable.createFromStream(inputStream, null)
                }
            viewHolder.binding.ivCardItem.setImageDrawable(image)
        } catch (e: Exception) {
            Log.e("CategoriesListAdapter", "Error loading image: ${category.imageUrl}")
            e.printStackTrace()
        }

        viewHolder.binding.cardItemLayout.setOnClickListener {
            itemClickListener?.onItemClick(category.id)
        }
    }

    override fun getItemCount(): Int = dataSet.size
}