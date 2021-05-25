package com.example.budgetplanning.fragments.first.recycler_view

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetplanning.R
import com.example.budgetplanning.data.entities.Transaction
import com.example.budgetplanning.data.viewmodels.TransactionViewModel
import com.example.budgetplanning.databinding.TransactionItemBinding
import com.example.budgetplanning.utils.TextUtils
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class TransactionAdapter(
    private var transactions: MutableList<Transaction>,
    val transactionViewModel: TransactionViewModel
) : RecyclerView.Adapter<TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(this, position)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun getItemAt(position: Int): Transaction {
        return transactions[position]
    }

    fun getItems(): MutableList<Transaction> {
        return transactions
    }

    fun removeItemAt(position: Int): Transaction {
        notifyItemRemoved(position)
        return transactions.removeAt(position)
    }

    fun updateItemAt(position: Int, newTransaction: Transaction) {
        transactions[position] = newTransaction
        notifyDataSetChanged()
        // notifyItemChanged(position)
    }

    fun setTransactions(vararg transactions: Transaction) {
        this.transactions = transactions.toMutableList()
        notifyDataSetChanged()
    }

}