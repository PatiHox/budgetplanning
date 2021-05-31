package com.example.budgetplanning.fragments.balance_history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetplanning.data.viewmodels.BalanceChangeViewModel
import com.example.budgetplanning.databinding.FragmentBalanceHistoryBinding
import com.example.budgetplanning.fragments.balance_history.recycler_view.BalanceChangeAdapter


class BalanceHistoryFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    private lateinit var balanceChangeViewModel: BalanceChangeViewModel

    private lateinit var balanceChangeAdapter: BalanceChangeAdapter


    private var _binding: FragmentBalanceHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("FirstFragment", "onCreateView(): invoke")
        if (_binding == null)
            _binding = FragmentBalanceHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // BalanceChangeViewModel
        balanceChangeViewModel = ViewModelProvider(this).get(BalanceChangeViewModel::class.java)
        balanceChangeViewModel.getAll.observe(viewLifecycleOwner, { transactions ->
            balanceChangeAdapter.setBalanceChanges(*transactions.toTypedArray())
        })

        // TransactionAdapter
        balanceChangeAdapter = BalanceChangeAdapter(mutableListOf(), balanceChangeViewModel)
        binding.rvBalanceHistory.adapter = balanceChangeAdapter
        binding.rvBalanceHistory.layoutManager = LinearLayoutManager(context)
    }


}