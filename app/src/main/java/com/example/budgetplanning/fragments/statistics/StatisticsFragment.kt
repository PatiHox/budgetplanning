package com.example.budgetplanning.fragments.statistics

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.budgetplanning.R
import com.example.budgetplanning.databinding.FragmentStatisticsBinding


class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.setGroupVisible(R.id.statistics_group, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }


}