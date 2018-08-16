package com.nicoqueijo.android.currencyconverter.helpers;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Implements the functionality of swipe to dismiss and drag & drop support to RecyclerView.
 * Credit: https://therubberduckdev.wordpress.com/2017/10/24/android-recyclerview-drag-and-drop-
 * and-swipe-to-dismiss/
 */
public class SwipeAndDragHelper extends ItemTouchHelper.Callback {

    public interface IActionCompletionContract {

        void onViewMoved(int oldPosition, int newPosition);

        void onViewSwiped(int position);
    }

    private IActionCompletionContract mIActionCompletionContract;

    public SwipeAndDragHelper(IActionCompletionContract iActionCompletionContract) {
        this.mIActionCompletionContract = iActionCompletionContract;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mIActionCompletionContract.onViewMoved(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mIActionCompletionContract.onViewSwiped(viewHolder.getAdapterPosition());
    }
}