package com.example.budgetplanning.fragments.balance_history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.budgetplanning.R


class BalanceHistoryFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_balance_history, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BalanceHistoryFragment().apply {

            }
    }
}