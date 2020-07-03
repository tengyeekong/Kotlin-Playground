package com.solution.it.newsoft.paging

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class FooterAdapter constructor(private val adapter: ListingAdapter) :
    LoadStateAdapter<FooterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): FooterViewHolder {
        return FooterViewHolder.create(parent) { adapter.retry() }
    }

    override fun onBindViewHolder(holder: FooterViewHolder, loadState: LoadState) {
        holder.bindTo(loadState)
    }
}
