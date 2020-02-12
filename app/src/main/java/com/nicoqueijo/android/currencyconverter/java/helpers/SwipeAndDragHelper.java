package com.nicoqueijo.android.currencyconverter.java.helpers;

import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.nicoqueijo.android.currencyconverter.java.adapters.ActiveCurrenciesAdapter;

/**
 * Implements the functionality of swipe to dismiss and drag & drop support to RecyclerView along
 * with a background layout.
 * Credit: https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/
 */
public class SwipeAndDragHelper extends ItemTouchHelper.SimpleCallback {

    public static final String TAG = ItemTouchHelper.class.getSimpleName();

    public interface IActionCompletionContract {

        void onViewMoved(int oldPosition, int newPosition);

        void onViewSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

    private IActionCompletionContract mIActionCompletionContract;

    public SwipeAndDragHelper(IActionCompletionContract iActionCompletionContract, int dragDirs,
                              int swipeDirs) {
        super(dragDirs, swipeDirs);
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
        mIActionCompletionContract.onViewSwiped(viewHolder, direction, viewHolder
                .getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (viewHolder != null) {
                final View foregroundView = ((ActiveCurrenciesAdapter.ViewHolder) viewHolder)
                        .mRowForeground;
                getDefaultUIUtil().onSelected(foregroundView);
            }
        } else {
            super.onSelectedChanged(viewHolder, actionState);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            final View foregroundView = ((ActiveCurrenciesAdapter.ViewHolder) viewHolder)
                    .mRowForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        } else {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState,
                    isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((ActiveCurrenciesAdapter.ViewHolder) viewHolder)
                .mRowForeground;
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            ActiveCurrenciesAdapter.ViewHolder thisHolder = ((ActiveCurrenciesAdapter.ViewHolder) viewHolder);
            final View foregroundView = thisHolder.mRowForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
            final int deleteIconStartVisibility = dX > 0.0 ? View.VISIBLE : View.INVISIBLE;
            final int deleteIconEndVisibility = dX < 0.0 ? View.VISIBLE : View.INVISIBLE;
            thisHolder.mDeleteIconStart.setVisibility(deleteIconStartVisibility);
            thisHolder.mDeleteIconEnd.setVisibility(deleteIconEndVisibility);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
}