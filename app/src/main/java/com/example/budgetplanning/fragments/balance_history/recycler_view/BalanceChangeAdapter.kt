package com.example.budgetplanning.fragments.balance_history.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetplanning.data.entities.BalanceChange
import com.example.budgetplanning.data.entities.Transaction
import com.example.budgetplanning.data.viewmodels.BalanceChangeViewModel
import com.example.budgetplanning.data.viewmodels.TransactionViewModel
import com.example.budgetplanning.databinding.BalanceHistoryItemBinding
import com.example.budgetplanning.databinding.TransactionItemBinding


class BalanceChangeAdapter(
    private var balanceChanges: MutableList<BalanceChange>,
    val balanceChangeViewModel: BalanceChangeViewModel
) : RecyclerView.Adapter<BalanceChangeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceChangeViewHolder {
        return BalanceChangeViewHolder(
            BalanceHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BalanceChangeViewHolder, position: Int) {
        holder.bind(this, position)
    }

    override fun getItemCount(): Int {
        return balanceChanges.size
    }

    fun getItemAt(position: Int): BalanceChange {
        return balanceChanges[position]
    }

    fun getItems(): MutableList<BalanceChange> {
        return balanceChanges
    }

    fun removeItemAt(position: Int): BalanceChange {
//        notifyItemRemoved(position)
        notifyDataSetChanged()
        return balanceChanges.removeAt(position)
    }

    fun updateItemAt(position: Int, newBalanceChange: BalanceChange) {
        balanceChanges[position] = newBalanceChange
        notifyDataSetChanged()
        // notifyItemChanged(position)
    }

    fun setBalanceChanges(vararg balanceChanges: BalanceChange) {
        this.balanceChanges = balanceChanges.toMutableList()
        notifyDataSetChanged()
    }

}