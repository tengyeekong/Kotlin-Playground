package com.solution.it.newsoft.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.solution.it.newsoft.databinding.ItemListBinding
import com.solution.it.newsoft.databinding.ItemProgressBinding
import com.solution.it.newsoft.model.List
import com.solution.it.newsoft.model.NetworkState
import javax.inject.Inject

class ListingAdapter @Inject constructor() : PagingDataAdapter<List, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    private var networkState = NetworkState.LOADING
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        viewHolder = when (viewType) {
            TYPE_PROGRESS -> {
                val binding2 = ItemProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingHolder(binding2)
            }
            else -> {
                val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ListHolder(binding)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_ITEM -> if (holder is ListHolder) {
                val currentList = getItem(position)
                holder.binding.list = currentList
            }
            TYPE_PROGRESS -> (holder as LoadingHolder).bindView(networkState)
        }
    }

    private fun hasFooter(): Boolean {
        return super.getItemCount() != 0 && networkState !== NetworkState.LOADED
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) TYPE_ITEM else TYPE_PROGRESS
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter()) 1 else 0
    }

    fun updateList(position: Int, listName: String, distance: String) {
        getItem(position)?.list_name = listName
        getItem(position)?.distance = distance
        notifyItemChanged(position)
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        this.networkState = newNetworkState
        notifyItemChanged(super.getItemCount())
    }

    internal inner class ListHolder(var binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {

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

    inner class LoadingHolder internal constructor(private val binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {

        internal fun bindView(networkState: NetworkState?) {
            if (!(networkState == null || networkState.status != NetworkState.Status.RUNNING)) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }

            if (networkState != null && networkState.status == NetworkState.Status.FAILED) {
                binding.btnRetry.visibility = View.VISIBLE
//                binding.btnRetry.setText(networkState.getMsg());
                binding.btnRetry.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener?.onRetryClick()
                    }
                }
            } else {
                binding.btnRetry.visibility = View.GONE
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(list: List)

        fun onItemLongClick(list: List, position: Int)

        fun onRetryClick()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    companion object {
        private const val TYPE_PROGRESS = 0
        private const val TYPE_ITEM = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<List>() {
            override fun areItemsTheSame(oldItem: List, newItem: List): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: List, newItem: List): Boolean {
                return (oldItem.list_name == newItem.list_name && oldItem.distance == newItem.distance)
            }
        }
    }
}
