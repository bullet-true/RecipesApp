package com.ifedorov.recipesapp.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifedorov.recipesapp.R
import com.ifedorov.recipesapp.common.Constants.IMAGES_BASE_URL
import com.ifedorov.recipesapp.databinding.ItemCategoryBinding
import com.ifedorov.recipesapp.model.Category

class CategoriesListAdapter(var dataSet: List<Category>) :
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

        Glide
            .with(viewHolder.itemView)
            .load(IMAGES_BASE_URL + category.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_error)
            .into(viewHolder.binding.ivCardItem)

        viewHolder.binding.cardItemLayout.setOnClickListener {
            itemClickListener?.onItemClick(category.id)
        }
    }

    override fun getItemCount(): Int = dataSet.size
}