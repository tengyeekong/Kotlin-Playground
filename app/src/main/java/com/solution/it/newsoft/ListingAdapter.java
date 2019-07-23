package com.solution.it.newsoft;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.solution.it.newsoft.databinding.ItemListBinding;
import com.solution.it.newsoft.databinding.ItemProgressBinding;
import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.util.ListAdapter;

import java.util.ArrayList;

public class ListingAdapter extends ListAdapter<List, RecyclerView.ViewHolder> {
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private OnItemClickListener listener;
    private boolean isLoadingAdded = false;

    public ListingAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<List> DIFF_CALLBACK = new DiffUtil.ItemCallback<List>() {
        @Override
        public boolean areItemsTheSame(List oldItem, List newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(List oldItem, List newItem) {
            return oldItem.getList_name() != null && oldItem.getList_name().equals(newItem.getList_name()) &&
                    oldItem.getDistance() != null && oldItem.getDistance().equals(newItem.getDistance());
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case ITEM:
                ItemListBinding binding = ItemListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                viewHolder = new ListHolder(binding);
                break;
            case LOADING:
                ItemProgressBinding binding2 = ItemProgressBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                viewHolder = new LoadingHolder(binding2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                List currentList = getItem(position);
                ((ListHolder) holder).binding.setList(currentList);
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == getItemCount() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void updateList(int position, String listName, String distance) {
        ArrayList<List> oriList = new ArrayList(getList());
        oriList.get(position).setList_name(listName);
        oriList.get(position).setDistance(distance);
        notifyItemChanged(position);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        ArrayList list = new ArrayList<>();
        list.addAll(getList());
        list.add(new List());
        submitList(list);
    }

    public void removeLoadingFooter(ArrayList newList) {
        isLoadingAdded = false;

        ArrayList<List> oriList = new ArrayList(getList());
        int position = oriList.size() - 1;
        List item = oriList.get(position);

        if (item != null && item.getId() == null) {
            oriList.remove(item);
            newList.addAll(0, oriList);
            submitList(newList);
        }
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
                    listener.onItemLongClick(getItem(position), position);
                }
                return true;
            });
        }
    }

    class LoadingHolder extends RecyclerView.ViewHolder {

        public LoadingHolder(ItemProgressBinding binding) {
            super(binding.getRoot());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(List list);

        void onItemLongClick(List list, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
