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
import com.example.budgetplanning.utils.DateUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*


class StatisticsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FragmentStatisticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null)
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
    private lateinit var chart: BarChart

    //    private lateinit var currentChartMode: StatisticsMode
    private val currentChartPeriod get() = _currentChartPeriod!!
    private var _currentChartMode: StatisticsMode? = null

    //    private lateinit var currentChartPeriod: Period
    private var _currentChartPeriod: Period? = null
    private val currentChartMode get() = _currentChartMode!!


    private var _sharedPref: SharedPreferences? = null
    private val sharedPref: SharedPreferences get() = _sharedPref!!

    private lateinit var mainMenu: Menu

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // SharedPreferences
        _sharedPref = activity?.getPreferences(AppCompatActivity.MODE_PRIVATE)

        // BarChart
        chart = binding.chart
        //TODO: Найти способ увеличить текст на графике
//        chart.text

        // TransactionViewModel
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        // BalanceChangeViewModel
        balanceChangeViewModel = ViewModelProvider(this).get(BalanceChangeViewModel::class.java)
    }


    private fun changeCurrentMode(newMode: StatisticsMode) {
        if (newMode != _currentChartMode) {
            _currentChartMode = newMode
            transactionViewModel.getAll.removeObservers(viewLifecycleOwner)
            updateChartScreen()
        }
    }

    private fun changeCurrentPeriod(newPeriod: Period) {
        if (newPeriod != _currentChartPeriod) {
            _currentChartPeriod = newPeriod
            updateChartScreen()
        }
    }

    private fun updateChartScreen() {
        when (currentChartMode) {
            StatisticsMode.BalanceChanges -> {

            }
            StatisticsMode.Transactions -> {
                Log.d("StatisticsFragment", "Getting data from Room")
                transactionViewModel.getAll.observe(viewLifecycleOwner) { allTransactions ->
                    Log.d("StatisticsFragment", "Data received")
                    if (allTransactions.isNotEmpty()) {
                        val posEntries = mutableListOf<BarEntry>()
                        val negEntries = mutableListOf<BarEntry>()

                        val firstTransaction = DateUtils.getFirstTransaction(allTransactions)

                        for (transaction in allTransactions) {
                            val transactionXPos = ChartUtils.getXPosOfTransaction(
                                firstTransaction,
                                currentChartPeriod,
                                transaction
                            )
                            if (transaction.changeAmount >= 0) {
                                val sameEntry = posEntries.find { entry ->
                                    entry.x == transactionXPos
                                }
                                if (sameEntry != null)
                                    sameEntry.y += transaction.changeAmount.toFloat()
                                else {
                                    posEntries.add(
                                        BarEntry(
                                            transactionXPos,
                                            transaction.changeAmount.toFloat()
                                        )
                                    )
                                }
                            } else {
                                val sameEntry = negEntries.find { entry ->
                                    entry.x == transactionXPos
                                }
                                if (sameEntry != null)
                                    sameEntry.y += transaction.changeAmount.toFloat()
                                else {
                                    negEntries.add(
                                        BarEntry(
                                            transactionXPos,
                                            transaction.changeAmount.toFloat()
                                        )
                                    )
                                }
                            }
                        }

                        val posBarDataSet = BarDataSet(posEntries, "Incomes")
                        posBarDataSet.color = Color.BLUE
                        val negBarDataSet = BarDataSet(negEntries, "Outlays")
                        negBarDataSet.color = Color.RED

                        val barData = BarData(posBarDataSet, negBarDataSet)
                        chart.data = barData
                        chart.invalidate()
                        Log.d("StatisticsFragment", "Updating chart")
                    }
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
        mainMenu = menu
        menu.setGroupVisible(R.id.navigation_group, false)
        menu.setGroupVisible(R.id.settings_group, false)

        // Init mode from shared preferences
        val modeOrdinal = sharedPref.getInt("statistics_mode", StatisticsMode.Transactions.ordinal)
        changeCurrentMode(StatisticsMode.values()[modeOrdinal])

        // Init period from shared preferences
        val periodOrdinal = sharedPref.getInt("statistics_period", Period.MONTH.ordinal)
        changeCurrentPeriod(Period.values()[periodOrdinal])

        lastChecked = when (periodOrdinal) {
            Period.DAY.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_day).isChecked =
                    true
//                binding.tvForPeriod.setText(R.string.day)
                R.id.select_day
            }
            Period.THREE_DAYS.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_three_days).isChecked =
                    true
//                binding.tvForPeriod.setText(R.string.three_days)
                R.id.select_three_days
            }
            Period.WEEK.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_week).isChecked =
                    true
//                binding.tvForPeriod.setText(R.string.week)
                R.id.select_week
            }
            Period.THREE_MONTHS.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_three_months).isChecked =
                    true
//                binding.tvForPeriod.setText(R.string.three_months)
                R.id.select_three_months
            }
            Period.HALF_YEAR.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_half_year).isChecked =
                    true
//                binding.tvForPeriod.setText(R.string.half_year)
                R.id.select_half_year
            }
            Period.YEAR.ordinal -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_year).isChecked =
                    true
//                binding.tvForPeriod.setText(R.string.year)
                R.id.select_month
            }
            else -> {
                mainMenu.findItem(R.id.action_show_for_period).subMenu.findItem(R.id.select_month).isChecked =
                    true
//                binding.tvForPeriod.setText(R.string.month)
                R.id.select_month
            }
        }
    }

    private var lastChecked: Int? = null
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.select_day,
            R.id.select_half_year,
            R.id.select_month,
            R.id.select_three_days,
            R.id.select_three_months,
            R.id.select_week,
            R.id.select_year -> {
                if (lastChecked != null && item.itemId != lastChecked) {
                    val subMenu = mainMenu.findItem(R.id.action_show_for_period).subMenu
                    lastChecked?.let { subMenu.findItem(it).isChecked = false }
                    item.isChecked = true
                    lastChecked = item.itemId

                    val newPeriod: Period = when (item.itemId) {
                        R.id.select_day -> {
//                            binding.tvForPeriod.setText(R.string.day)
                            Period.DAY
                        }
                        R.id.select_half_year -> {
//                            binding.tvForPeriod.setText(R.string.half_year)
                            Period.HALF_YEAR
                        }
                        R.id.select_three_days -> {
//                            binding.tvForPeriod.setText(R.string.three_days)
                            Period.THREE_DAYS
                        }
                        R.id.select_three_months -> {
//                            binding.tvForPeriod.setText(R.string.three_months)
                            Period.THREE_MONTHS
                        }
                        R.id.select_week -> {
//                            binding.tvForPeriod.setText(R.string.week)
                            Period.WEEK
                        }
                        R.id.select_year -> {
//                            binding.tvForPeriod.setText(R.string.year)
                            Period.YEAR
                        }
                        else -> {
//                            binding.tvForPeriod.setText(R.string.month)
                            Period.MONTH
                        }
                    }
                    sharedPref.edit().putInt("statistics_period", newPeriod.ordinal).apply()
                    changeCurrentPeriod(newPeriod)
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        TODO("Not yet implemented")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
//        TODO("Not yet implemented")
    }


}