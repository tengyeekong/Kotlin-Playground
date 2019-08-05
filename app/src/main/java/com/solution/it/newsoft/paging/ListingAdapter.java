package com.solution.it.newsoft.paging;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.solution.it.newsoft.databinding.ItemListBinding;
import com.solution.it.newsoft.databinding.ItemProgressBinding;
import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.model.NetworkState;

import javax.inject.Inject;

public class ListingAdapter extends PagedListAdapter<List, RecyclerView.ViewHolder> {
    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;
    private NetworkState networkState = NetworkState.LOADING;
    private OnItemClickListener listener;

    @Inject
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
            return oldItem.getList_name() != null && oldItem.getList_name().equals(newItem.getList_name())
                    && oldItem.getDistance() != null && oldItem.getDistance().equals(newItem.getDistance());
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TYPE_PROGRESS:
                ItemProgressBinding binding2 = ItemProgressBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                viewHolder = new LoadingHolder(binding2);
                break;
            default:
                ItemListBinding binding = ItemListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                viewHolder = new ListHolder(binding);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                if (holder instanceof ListHolder) {
                    List currentList = getItem(position);
                    ((ListHolder) holder).binding.setList(currentList);
                }
                break;
            case TYPE_PROGRESS:
                ((LoadingHolder) holder).bindView(networkState);
                break;
        }
    }

    private boolean hasFooter() {
        return super.getItemCount() != 0 && networkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        return position < super.getItemCount() ? TYPE_ITEM : TYPE_PROGRESS;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + ((hasFooter()) ? 1 : 0);
    }

    public void updateList(int position, String listName, String distance) {
        getCurrentList().get(position).setList_name(listName);
        getCurrentList().get(position).setDistance(distance);
        notifyItemChanged(position);
    }

    public void setNetworkState(NetworkState newNetworkState) {
        this.networkState = newNetworkState;
        notifyItemChanged(super.getItemCount());
    }

    class ListHolder extends RecyclerView.ViewHolder {
        ItemListBinding binding;

        ListHolder(ItemListBinding binding) {
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

    public class LoadingHolder extends RecyclerView.ViewHolder {

        private ItemProgressBinding binding;

        LoadingHolder(ItemProgressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindView(NetworkState networkState) {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                binding.btnRetry.setVisibility(View.VISIBLE);
//                binding.btnRetry.setText(networkState.getMsg());
                binding.btnRetry.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onRetryClick();
                    }
                });
            } else {
                binding.btnRetry.setVisibility(View.GONE);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(List list);

        void onItemLongClick(List list, int position);

        void onRetryClick();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
