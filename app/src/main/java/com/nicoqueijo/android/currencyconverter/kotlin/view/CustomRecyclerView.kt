package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Extending the default RecyclerView in order to implement AdapterDataObserver to listen for
 * changes in the adapter and act accordingly. If the adapter becomes empty we want to show a View
 * which displays information about why its empty.
 */
class CustomRecyclerView : RecyclerView {

    private lateinit var emptyListView: View

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    /**
     * Whenever there is a change in the adapter data observer one of the following methods will be
     * called. In each method we check if the adapter is empty to show/hide the EmptyListView.
     */
    private val mAdapterDataObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            showEmptyListView()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            showEmptyListView()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            showEmptyListView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            showEmptyListView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            showEmptyListView()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            showEmptyListView()
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(mAdapterDataObserver)
        mAdapterDataObserver.onChanged()
    }

    fun showIfEmpty(emptyListView: View) {
        this.emptyListView = emptyListView
    }

    private fun showEmptyListView() {
        emptyListView.visibility = if (adapter!!.itemCount == 0) View.VISIBLE else View.GONE
    }
}