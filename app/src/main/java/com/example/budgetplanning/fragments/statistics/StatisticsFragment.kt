package com.example.budgetplanning.fragments.statistics

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
import com.example.budgetplanning.utils.MyEntryHelper
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import java.time.LocalDateTime


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
    private lateinit var barChart: BarChart
    private lateinit var lineChart: LineChart

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
        barChart = binding.barChart
        barChart.setPinchZoom(true)
        lineChart = binding.lineChart
        lineChart.setPinchZoom(true)

        //TODO: Найти способ увеличить текст на графике
//        chart.text

        // TransactionViewModel
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        // BalanceChangeViewModel
        balanceChangeViewModel = ViewModelProvider(this).get(BalanceChangeViewModel::class.java)
    }


    private fun changeCurrentMode(newMode: StatisticsMode, suspendScreenUpdate: Boolean = false) {
        when (newMode) {
            StatisticsMode.Transactions -> {
                barChart.isVisible = true
                lineChart.isVisible = false
            }
            StatisticsMode.BalanceChanges -> {
                barChart.isVisible = false
                lineChart.isVisible = true
            }
        }
        if (newMode != _currentChartMode) {
            _currentChartMode = newMode
            transactionViewModel.getAll.removeObservers(viewLifecycleOwner)
            if (!suspendScreenUpdate)
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
                Log.d("StatisticsFragment", "Getting data from Room")
                transactionViewModel.getAll.observe(viewLifecycleOwner) { allTransactions ->
                    balanceChangeViewModel.getAll.observe(viewLifecycleOwner) { allBalanceChanges ->
                        if (allTransactions.isNotEmpty() || allBalanceChanges.isNotEmpty()) {
                            val firstDateTime: LocalDateTime = when {
                                allTransactions.isEmpty() && allBalanceChanges.isNotEmpty() -> {
                                    DateUtils.getFirstBalanceChange(allBalanceChanges).dateTime
                                }
                                allTransactions.isNotEmpty() && allBalanceChanges.isEmpty() ->{
                                    DateUtils.getFirstTransaction(allTransactions).dateTime
                                }
                                else ->{
                                    val firstBalanceChange =
                                        DateUtils.getFirstBalanceChange(allBalanceChanges)
                                    val firstTransaction = DateUtils.getFirstTransaction(allTransactions)

                                    when {
                                        firstBalanceChange.dateTime.isBefore(firstTransaction.dateTime) -> {
                                            firstBalanceChange.dateTime
                                        }
                                        else -> {
                                            firstTransaction.dateTime
                                        }
                                    }
                                }
                            }


                            val entryHelpers = mutableListOf<MyEntryHelper>()


                            // find all X for transactions
                            for (transaction in allTransactions) {
                                Log.d(
                                    "StatisticsFragment",
                                    "transaction{changeAmount: ${transaction.changeAmount}, dateTime: ${transaction.dateTime.toString()}, comment: ${transaction.comment}}"
                                )
                                val transactionXPos = ChartUtils.getXPosOfDateTime(
                                    firstDateTime,
                                    currentChartPeriod,
                                    transaction.dateTime
                                )

                                Log.d("StatisticsFragment", "transaction x pos: $transactionXPos")

                                val sameEntry = entryHelpers.find { item ->
                                    item.x == transactionXPos
                                }

                                if (sameEntry == null) {
                                    Log.d("StatisticsFragment", "Entry with same x pos not found")
                                    entryHelpers.add(MyEntryHelper(transactionXPos).also {
                                        it.transactions.add(
                                            transaction
                                        )
                                    })
                                } else {
                                    Log.d("StatisticsFragment", "Entry with same x pos found")
                                    sameEntry.transactions.add(transaction)
                                }
                            }

                            // find x for balanceChanges
                            for (balanceChange in allBalanceChanges) {
                                Log.d(
                                    "StatisticsFragment",
                                    "balanceChange{newValue: ${balanceChange.newValue}, dateTime: ${balanceChange.dateTime.toString()}}"
                                )

                                val balanceChangeXPos = ChartUtils.getXPosOfDateTime(
                                    firstDateTime,
                                    currentChartPeriod,
                                    balanceChange.dateTime
                                )

                                Log.d(
                                    "StatisticsFragment",
                                    "balanceChange x pos: $balanceChangeXPos"
                                )


                                val sameEntry = entryHelpers.find { item ->
                                    item.x == balanceChangeXPos
                                }

                                if (sameEntry == null) {
                                    Log.d("StatisticsFragment", "Entry with same x pos not found")
                                    entryHelpers.add(MyEntryHelper(balanceChangeXPos).also {
                                        it.lastBalanceChange = balanceChange
                                    })
                                } else {
                                    Log.d("StatisticsFragment", "Entry with same x pos found")
                                    if (sameEntry.lastBalanceChange == null ||
                                        sameEntry.lastBalanceChange!!.dateTime
                                            .isBefore(balanceChange.dateTime)
                                    ) {
                                        Log.d(
                                            "StatisticsFragment",
                                            "balanceChange is later than balanceChange inside entry with same x"
                                        )
                                        sameEntry.lastBalanceChange = balanceChange
                                    }
                                }
                            }


                            val entries = arrayListOf<Entry>()

                            for (entryHelper in entryHelpers) {
                                Log.d("StatisticsFragment", "entryHelper with x: ${entryHelper.x}")
                                var balance = 0.0

                                // if lastBalanceChange is not null = set balance to it
                                entryHelper.lastBalanceChange?.also {
                                    balance = it.newValue
                                    Log.d(
                                        "StatisticsFragment",
                                        "lastBalanceChange{newValue: ${it.newValue}, dateTime: ${it.dateTime}}"
                                    )
                                }

                                Log.d("StatisticsFragment", "transactions inside this entry:")
                                for (transaction in entryHelper.transactions) {
                                    Log.d(
                                        "StatisticsFragment",
                                        "transaction{changeAmount: ${transaction.changeAmount}, dateTime: ${transaction.dateTime.toString()}, comment: ${transaction.comment}}"
                                    )

                                    // if lastBalanceChange is not null
                                    // and is later than current transaction
                                    if (entryHelper.lastBalanceChange?.dateTime?.isAfter(transaction.dateTime) == true) {
                                        Log.d(
                                            "StatisticsFragment",
                                            "transaction is before the lastBalanceChange, skipping..."
                                        )
                                        continue
                                    }
                                    Log.d(
                                        "StatisticsFragment",
                                        "Adding transaction.changeAmount to y"
                                    )
                                    balance += transaction.changeAmount
                                }
                                Log.d("StatisticsFragment", "Adding new entry to Entry array")
                                entries.add(Entry(entryHelper.x, balance.toFloat()))
                                Log.d(
                                    "StatisticsFragment",
                                    "Added new Entry(x: ${entryHelper.x}, y: ${balance.toFloat()})"
                                )
                            }

                            entries.sortBy {
                                it.x
                            }

                            val lineDataSet =
                                LineDataSet(entries, getString(R.string.line_graph_desc))

                            lineDataSet.setColor(Color.CYAN)
                            lineDataSet.setCircleColor(Color.DKGRAY)
                            lineDataSet.setLineWidth(4f)
                            lineDataSet.setCircleRadius(3f)
                            lineDataSet.setDrawCircleHole(false)
                            lineDataSet.setValueTextSize(15f)

                            val lineData = LineData(lineDataSet)
                            lineChart.data = lineData
                            lineChart.invalidate()


                            val xAxis = lineChart.xAxis

                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.textSize = 12f

                            Log.d("StatisticsFragment", "Updating chart")
                        }
                    }
                }

            }
            StatisticsMode.Transactions -> {
                Log.d("StatisticsFragment", "Getting data from Room")
                transactionViewModel.getAll.observe(viewLifecycleOwner) { allTransactions ->
                    Log.d("StatisticsFragment", "Data received")
                    if (allTransactions.isNotEmpty()) {
                        val posEntries = mutableListOf<BarEntry>()
                        val negEntries = mutableListOf<BarEntry>()

                        val firstDateTime = DateUtils.getFirstTransaction(allTransactions).dateTime

                        for (transaction in allTransactions) {
                            val transactionXPos = ChartUtils.getXPosOfDateTime(
                                firstDateTime,
                                currentChartPeriod,
                                transaction.dateTime
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

                        posEntries.sortBy {
                            it.x
                        }

                        negEntries.sortBy {
                            it.x
                        }

                        val posBarDataSet = BarDataSet(posEntries, getString(R.string.income))
                        posBarDataSet.color = Color.BLUE
                        val negBarDataSet = BarDataSet(negEntries, getString(R.string.outlay))
                        negBarDataSet.color = Color.RED

                        val barData = BarData(posBarDataSet, negBarDataSet)
                        barChart.data = barData
                        barChart.invalidate()
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
        changeCurrentMode(StatisticsMode.values()[modeOrdinal], suspendScreenUpdate = true)

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
        when (position) {
            0 -> changeCurrentMode(StatisticsMode.Transactions)
            1 -> changeCurrentMode(StatisticsMode.BalanceChanges)
            else -> changeCurrentMode(StatisticsMode.Transactions)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        changeCurrentMode(StatisticsMode.Transactions)
    }


}