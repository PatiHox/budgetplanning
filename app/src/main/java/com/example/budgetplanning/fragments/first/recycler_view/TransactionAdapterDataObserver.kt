package com.example.budgetplanning.fragments.first.recycler_view

import androidx.recyclerview.widget.RecyclerView

class TransactionAdapterDataObserver(
    /*private val dataInserted: (posStart: Int, count: Int) -> Unit,*/
    private val dataRemoved: (posStart: Int, count: Int) -> Unit,
    private val dataChanged: () -> Unit
) : RecyclerView.AdapterDataObserver() {

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        /*dataInserted(positionStart, itemCount)*/
        dataChanged()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        super.onItemRangeRemoved(positionStart, itemCount)
        dataRemoved(positionStart, itemCount)
    }

    override fun onChanged() {
        super.onChanged()
        dataChanged()
    }
}