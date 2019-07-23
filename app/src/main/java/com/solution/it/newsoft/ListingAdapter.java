package com.solution.it.newsoft;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.solution.it.newsoft.databinding.ItemListBinding;
import com.solution.it.newsoft.model.List;

public class ListingAdapter extends ListAdapter<List, ListingAdapter.ListHolder> {
    private OnItemClickListener listener;

    public ListingAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<List> DIFF_CALLBACK = new DiffUtil.ItemCallback<List>() {
        @Override
        public boolean areItemsTheSame(List oldItem, List newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(List oldItem, List newItem) {
            return oldItem.getList_name().equals(newItem.getList_name()) &&
                    oldItem.getDistance().equals(newItem.getDistance());
        }
    };

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListBinding binding = ItemListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
        List currentList = getItem(position);
        holder.binding.setList(currentList);
    }

    class ListHolder extends RecyclerView.ViewHolder {
        ItemListBinding binding;

        public ListHolder(ItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(getItem(position));
                }
                return true;
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(List list);
        void onItemLongClick(List list);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
