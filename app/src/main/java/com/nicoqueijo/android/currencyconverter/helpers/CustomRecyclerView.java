package com.nicoqueijo.android.currencyconverter.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Extending the default RecyclerView in order to implement AdapterDataObserver to listen for
 * changes in the adapter and act accordingly. If the adapter becomes empty we want to show a View
 * which displays a hint on how to add new items using the FloatingActionButton.
 */
public class CustomRecyclerView extends RecyclerView {

    private View mEmptyListView;

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Whenever there is a change in the adapter data observer one of the following methods will be
     * called. In each method we check if the adapter is empty to show/hide the EmptyListView.
     */
    private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            showEmptyListView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            showEmptyListView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            showEmptyListView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            showEmptyListView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            showEmptyListView();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            showEmptyListView();
        }
    };

    /**
     * Register a new observer to listen for data changes.
     *
     * @param adapter the bridge for the dataset of the RecyclerView.
     */
    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(mAdapterDataObserver);
        mAdapterDataObserver.onChanged();
    }

    /**
     * Passes the view to show when the RecyclerView is empty.
     *
     * @param emptyListView the view to show when the RecyclerView is empty.
     */
    public void showIfEmpty(View emptyListView) {
        mEmptyListView = emptyListView;
    }

    /**
     * Shows/hides the view that hints the user how to add items if the RecyclerView is empty or
     * not.
     */
    private void showEmptyListView() {
        mEmptyListView.setVisibility(getAdapter().getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
