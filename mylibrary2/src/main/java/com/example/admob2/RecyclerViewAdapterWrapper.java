package com.example.admob2;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


public class RecyclerViewAdapterWrapper extends RecyclerView.Adapter {
    private final RecyclerView.Adapter<RecyclerView.ViewHolder> wrapped;

    public RecyclerViewAdapterWrapper(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        this.wrapped = adapter;
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                RecyclerViewAdapterWrapper.this.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int i, int i2) {
                RecyclerViewAdapterWrapper.this.notifyItemRangeChanged(i, i2);
            }

            @Override
            public void onItemRangeInserted(int i, int i2) {
                RecyclerViewAdapterWrapper.this.notifyItemRangeInserted(i, i2);
            }

            @Override
            public void onItemRangeRemoved(int i, int i2) {
                RecyclerViewAdapterWrapper.this.notifyItemRangeRemoved(i, i2);
            }

            @Override
            public void onItemRangeMoved(int i, int i2, int i3) {
                RecyclerViewAdapterWrapper.this.notifyItemMoved(i, i2);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return this.wrapped.onCreateViewHolder(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        this.wrapped.onBindViewHolder(viewHolder, i);
    }

    @Override
    public int getItemCount() {
        return this.wrapped.getItemCount();
    }

    @Override
    public int getItemViewType(int i) {
        return this.wrapped.getItemViewType(i);
    }

    @Override
    public void setHasStableIds(boolean z) {
        this.wrapped.setHasStableIds(z);
    }

    @Override
    public long getItemId(int i) {
        return this.wrapped.getItemId(i);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        this.wrapped.onViewRecycled(viewHolder);
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder viewHolder) {
        return this.wrapped.onFailedToRecycleView(viewHolder);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
        this.wrapped.onViewAttachedToWindow(viewHolder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
        this.wrapped.onViewDetachedFromWindow(viewHolder);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver adapterDataObserver) {
        this.wrapped.registerAdapterDataObserver(adapterDataObserver);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver adapterDataObserver) {
        this.wrapped.unregisterAdapterDataObserver(adapterDataObserver);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.wrapped.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.wrapped.onDetachedFromRecyclerView(recyclerView);
    }

    public RecyclerView.Adapter<RecyclerView.ViewHolder> getWrappedAdapter() {
        return this.wrapped;
    }
}
