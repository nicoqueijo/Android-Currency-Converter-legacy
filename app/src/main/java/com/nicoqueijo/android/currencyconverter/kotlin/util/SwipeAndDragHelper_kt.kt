package com.nicoqueijo.android.currencyconverter.kotlin.util

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.nicoqueijo.android.currencyconverter.kotlin.adapter.ActiveCurrenciesAdapter_kt

class SwipeAndDragHelper_kt(private val actionCompletionContract: ActionCompletionContract, dragDirs: Int, swipeDirs: Int) :
        ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        actionCompletionContract.onViewMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        actionCompletionContract.onViewSwiped(viewHolder, direction, viewHolder.adapterPosition)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (viewHolder != null) {
                val foregroundView: View = (viewHolder as ActiveCurrenciesAdapter_kt.ViewHolder).rowForeground
                getDefaultUIUtil().onSelected(foregroundView)
            }
        } else {
            super.onSelectedChanged(viewHolder, actionState)
        }
    }

    override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder?,
                                 dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val foregroundView: View = (viewHolder as ActiveCurrenciesAdapter_kt.ViewHolder).rowForeground
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive)
        } else {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView: View = (viewHolder as ActiveCurrenciesAdapter_kt.ViewHolder).rowForeground
        getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val thisHolder = viewHolder as ActiveCurrenciesAdapter_kt.ViewHolder
            val foregroundView: View = thisHolder.rowForeground
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView,
                    dX, dY, actionState, isCurrentlyActive)
            val deleteIconStartVisibility = if (dX > 0.0) View.VISIBLE else View.INVISIBLE
            val deleteIconEndVisibility = if (dX < 0.0) View.VISIBLE else View.INVISIBLE
            thisHolder.deleteIconStart.visibility = deleteIconStartVisibility
            thisHolder.deleteIconEnd.visibility = deleteIconEndVisibility
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    interface ActionCompletionContract {
        fun onViewMoved(oldPosition: Int, newPosition: Int)
        fun onViewSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int)
    }
}