package com.tengyeekong.kotlinplayground.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.tengyeekong.kotlinplayground.databinding.ItemFooterBinding

class FooterViewHolder(
    private val binding: ItemFooterBinding,
    private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(loadState: LoadState) {
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.tvRetry.isVisible = loadState is LoadState.Error
        binding.tvError.isVisible = !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
        binding.tvError.text = (loadState as? LoadState.Error)?.error?.message

        binding.tvRetry.setOnClickListener { retryCallback }
    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): FooterViewHolder {
            val binding = ItemFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FooterViewHolder(binding, retryCallback)
        }
    }
}
