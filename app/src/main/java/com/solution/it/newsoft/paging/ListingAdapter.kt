package com.solution.it.newsoft.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.solution.it.newsoft.databinding.ItemListBinding
import com.solution.it.newsoft.databinding.ItemProgressBinding
import com.solution.it.newsoft.model.List
import com.solution.it.newsoft.model.NetworkState

import javax.inject.Inject

class ListingAdapter @Inject
constructor() : PagingDataAdapter<List, ListingAdapter.ListHolder>(DIFF_CALLBACK) {
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListHolder(binding)
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        val currentList = getItem(position)
        holder.binding.list = currentList
    }

    fun updateList(position: Int, listName: String, distance: String) {
        getItem(position)?.list_name = listName
        getItem(position)?.distance = distance
        notifyItemChanged(position)
    }

    inner class ListHolder(var binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { listener?.onItemClick(it) }
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { listener?.onItemLongClick(it, position) }
                }
                true
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(list: List)
        fun onItemLongClick(list: List, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<List>() {
            override fun areItemsTheSame(oldItem: List, newItem: List): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: List, newItem: List): Boolean {
                return oldItem == newItem
            }
        }
    }
}
