package com.example.budgetplanning.fragments.statistics

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.budgetplanning.R
import com.example.budgetplanning.data.viewmodels.BalanceChangeViewModel
import com.example.budgetplanning.data.viewmodels.TransactionViewModel
import com.example.budgetplanning.databinding.FragmentStatisticsBinding
import com.example.budgetplanning.enums.Period
import com.example.budgetplanning.enums.StatisticsMode
import com.example.budgetplanning.utils.ChartUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class StatisticsFragment : Fragment(), AdapterView.OnItemSelectedListener{
    private var _binding: FragmentStatisticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(_binding == null)
            _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // SelectChartType
        ArrayAdapter.createFromResource(
            view.context,
            R.array.statistics_modes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sSelectChartType.adapter = adapter
        }
        binding.sSelectChartType.onItemSelectedListener = this
    }

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var balanceChangeViewModel: BalanceChangeViewModel
    private lateinit var chart: LineChart
    private lateinit var currentMode: StatisticsMode

    private var _sharedPref: SharedPreferences? = null

    private val sharedPref: SharedPreferences get() = _sharedPref!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // SharedPreferences
        _sharedPref = activity?.getPreferences(AppCompatActivity.MODE_PRIVATE)

        // LaneChart
        chart = binding.chart

        // TransactionViewModel
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        // BalanceChangeViewModel
        balanceChangeViewModel = ViewModelProvider(this).get(BalanceChangeViewModel::class.java)

        // TODO: This could be removed when OnCreateOptions is done
        changeCurrentMode(StatisticsMode.Transactions, Period.DAY)
//        currentMode = StatisticsMode.BalanceChanges
    }


    private fun changeCurrentMode(newMode: StatisticsMode, chartPeriod: Period){
        if(newMode != currentMode){
            transactionViewModel.getAll.removeObservers(viewLifecycleOwner)
            showStatisticsChart(newMode)
            currentMode = newMode
        }
    }

    private fun showStatisticsChart(mode: StatisticsMode){
        when (mode) {
            StatisticsMode.BalanceChanges -> {

            }
            else -> {
                Log.d("StatisticsFragment", "Getting data from Room")
                transactionViewModel.getAll.observe(viewLifecycleOwner) { allTransactions ->
                    Log.d("StatisticsFragment", "Data received")
                    val posEntries = mutableListOf<Entry>()
                    val negEntries = mutableListOf<Entry>()

                    ChartUtils.initForDataArray(allTransactions)

                    for (t in allTransactions) {
                        if (t.changeAmount >= 0) {
                            posEntries.add(Entry(ChartUtils.getXPosOfTransaction(t), t.changeAmount))
                        } else {
                            negEntries.add(Entry(ChartUtils.getXPosOfTransaction(t), t.changeAmount))
                        }
                    }

                    val posLineDataSet = LineDataSet(posEntries, "Incomes")
                    posLineDataSet.color = Color.BLUE
                    val negLineDataSet = LineDataSet(negEntries, "Outlays")
                    negLineDataSet.color = Color.RED

                    val lineData = LineData(posLineDataSet, negLineDataSet)
                    chart.data = lineData
                    chart.invalidate()
                    Log.d("StatisticsFragment", "Updating chart")

                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.setGroupVisible(R.id.navigation_group, false)
        menu.setGroupVisible(R.id.settings_group, false)

        // TODO: Make loading of period call func changeCurrentMode() and maybe rename it
        /*val period = sharedPref.getInt("statistics_period", Period.MONTH.ordinal)
        when (period) {
            Period.DAY.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_day).isChecked =
                    true
                binding.tvForPeriod.setText(R.string.day)
                R.id.select_day
            }
            Period.THREE_DAYS.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_three_days).isChecked =
                    true
                binding.tvForPeriod.setText(R.string.three_days)
                R.id.select_three_days
            }
            Period.WEEK.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_week).isChecked =
                    true
                binding.tvForPeriod.setText(R.string.week)
                R.id.select_week
            }
            Period.THREE_MONTHS.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_three_months).isChecked =
                    true
                binding.tvForPeriod.setText(R.string.three_months)
                R.id.select_three_months
            }
            Period.HALF_YEAR.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_half_year).isChecked =
                    true
                binding.tvForPeriod.setText(R.string.half_year)
                R.id.select_half_year
            }
            Period.YEAR.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_year).isChecked =
                    true
                binding.tvForPeriod.setText(R.string.year)
                R.id.select_month
            }
            else -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_month).isChecked =
                    true
                binding.tvForPeriod.setText(R.string.month)
                R.id.select_month
            }
        }*/
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        TODO("Not yet implemented")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
//        TODO("Not yet implemented")
    }


}