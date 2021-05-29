package com.example.budgetplanning.fragments.first

import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetplanning.R
import com.example.budgetplanning.data.entities.BalanceChange
import com.example.budgetplanning.data.viewmodels.BalanceChangeViewModel
import com.example.budgetplanning.data.viewmodels.TransactionViewModel
import com.example.budgetplanning.databinding.FragmentFirstBinding
import com.example.budgetplanning.enums.Period
import com.example.budgetplanning.fragments.first.recycler_view.TransactionAdapter
import com.example.budgetplanning.fragments.first.recycler_view.TransactionAdapterDataObserver
import com.example.budgetplanning.utils.DateUtils
import com.example.budgetplanning.utils.TextUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


//TODO: Поправить гороизонтальное расположение экрана

class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var balanceChangeViewModel: BalanceChangeViewModel

    private lateinit var transactionAdapter: TransactionAdapter

    private lateinit var mainMenu: Menu

    private val transactionDataObserver =
        TransactionAdapterDataObserver(::dataInserted, ::dataRemoved, ::dataChanged)

    private var _sharedPref: SharedPreferences? = null

    private val sharedPref: SharedPreferences get() = _sharedPref!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("FirstFragment" ,"onCreateView(): invoke")
        if (_binding == null)
            _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var balance: Float = 0f
    private var income: Float = 0f
    private var outlay: Float = 0f
    private val change: Float
        get() {
            return income + outlay
        }


    private fun updateScreen() {
        binding.tvBal.text = TextUtils.floatToMoney(balance, resources, false)
        binding.tvChange.text = TextUtils.floatToMoney(change, resources)
        when{
            change >= 0 -> binding.tvChange.setTextColor(Color.parseColor("#174300"))
            else -> binding.tvChange.setTextColor(Color.parseColor("#7C0000"))
        }
        binding.tvIncome.text = TextUtils.floatToMoney(income, resources)
        binding.tvOutlay.text = TextUtils.floatToMoney(outlay, resources)
    }

    private fun saveNewBalance(newBal: Float) {
        Log.d("FirstFragment", "saveNewBalance() call with newBal = $newBal")
        balanceChangeViewModel.insertAll(
            BalanceChange(
                newValue = newBal,
                dateTime = LocalDateTime.now()
            )
        )
        balance = newBal
        updateScreen()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FloatingActionButton
        binding.fabAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        // ibEditBalance
        binding.ibEditBalance.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
            builder.setTitle(getString(R.string.change_balance))

            // Set up the input
            val input: EditText = EditText(view.context)
            input.hint = getString(R.string.new_balance)
            input.setText(balance.toString())
            input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_NUMBER_FLAG_DECIMAL

            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_VARIATION_NORMAL)

            input.addTextChangedListener(MoneyNumberWatcher())

            builder.setView(input)

            // Set up the buttons
            builder.setPositiveButton(getString(R.string.save),
                DialogInterface.OnClickListener { _, _ ->
                    saveNewBalance(input.text.toString().toFloat())
                })

            builder.setNegativeButton(getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

            builder.show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // SharedPreferences
        _sharedPref = activity?.getPreferences(AppCompatActivity.MODE_PRIVATE)

        // TransactionViewModel
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        transactionViewModel.getAll.observe(viewLifecycleOwner, { transactions ->
            transactionAdapter.setTransactions(*transactions.toTypedArray())
        })

        // BalanceChangeViewModel
        balanceChangeViewModel = ViewModelProvider(this).get(BalanceChangeViewModel::class.java)

        // TransactionAdapter
        transactionAdapter = TransactionAdapter(mutableListOf(), transactionViewModel)
        binding.rvTransactions.adapter = transactionAdapter
        binding.rvTransactions.layoutManager = LinearLayoutManager(context)
        transactionAdapter.registerAdapterDataObserver(transactionDataObserver)
    }

    @Deprecated("Outdated. Needs rework")
    private fun dataInserted(posStart: Int, count: Int) {
        transactionAdapter.apply {
            for (i in posStart until posStart + count) {
                val changeAmount = getItemAt(i).changeAmount
                balance += changeAmount
                when {
                    changeAmount > 0 -> {
                        income += changeAmount
                    }
                    changeAmount < 0 -> {
                        outlay += changeAmount
                    }
                }
            }
        }
        updateScreen()
    }

    private lateinit var periodStartDate: LocalDateTime

    // Only works if this func is called BEFORE actual data removal
    private fun dataRemoved(posStart: Int, count: Int) {
        balanceChangeViewModel.getLast.observe(viewLifecycleOwner, {
            // Кастыль :Р
            val balanceChange: BalanceChange = when {
                (it == null) -> BalanceChange(null, balance, LocalDateTime.MIN)
                else -> it
            }

            transactionAdapter.apply {
                for (i in posStart until posStart + count) {
                    val removedTransaction = getItemAt(i)
                    // If removedTransaction even changed anything
                    if (removedTransaction.dateTime.isAfter(periodStartDate)) {
                        // If removedTransaction affected balance
                        if (removedTransaction.dateTime.isAfter(balanceChange.dateTime))
                            balance -= removedTransaction.changeAmount
                        when {
                            removedTransaction.changeAmount > 0 -> {
                                income -= removedTransaction.changeAmount
                            }
                            removedTransaction.changeAmount < 0 -> {
                                outlay -= removedTransaction.changeAmount
                            }
                        }
                    }
                }
            }
            updateScreen()
        })

    }

    // TODO: Сделать так что бы функции обновления учитывали ещё и то что входит ли последнее изменение баланса в текущий период
    private fun dataChanged() {
        Log.d("FirstFragmentDataObserverMethods", "dataChanged() called")


        periodStartDate = DateUtils.getPeriodStartDateFromNow(
            Period.values()[sharedPref.getInt(
                "period",
                Period.MONTH.ordinal
            )]
        )

        clearBalData()

        val transactions = transactionAdapter.getItems()
        balanceChangeViewModel.getLast.observe(viewLifecycleOwner, {
            // Кастыль :Р
            val balanceChange: BalanceChange = when {
                (it == null) -> BalanceChange(null, balance, LocalDateTime.MIN)
                else -> it
            }

            Log.d(
                "FirstFragment",
                "dataChanged(): got last balanceChange entry: {" +
                        "id: ${balanceChange.id}, " +
                        "newValue: ${balanceChange.newValue}, " +
                        "dateTime: ${
                            balanceChange.dateTime.format(
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            )
                        }}"
            )

            balance = balanceChange.newValue
            for (t in transactions) {
                Log.d("FirstFragment", "dataChanged(): Processing transaction with id: ${t.id}")
                if (t.dateTime.isAfter(periodStartDate)) {
                    Log.d(
                        "FirstFragment",
                        "dataChanged(): Transaction was accepted by current period settings"
                    )
                    val changeAmount = t.changeAmount
                    if (t.dateTime.isAfter(balanceChange.dateTime)) {
                        Log.d(
                            "FirstFragment",
                            "dataChanged(): Transaction happened later than the latest balanceChange"
                        )
                        balance += changeAmount
                    } else
                        Log.d(
                            "FirstFragment",
                            "dataChanged(): Transaction happened prior to the latest balanceChange, ignoring changes to balance"
                        )
                    when {
                        changeAmount > 0 -> {
                            income += changeAmount
                        }
                        changeAmount < 0 -> {
                            outlay += changeAmount
                        }
                    }
                } else {
                    Log.d("FirstFragment", "dataChanged(): Transaction was skipped")
                }
            }
            updateScreen()

            // Удаляет сам себя что бы наблюдатель больше не срабатывал
            // И да, это кастыль
            // :Р
            balanceChangeViewModel.getLast.removeObservers(viewLifecycleOwner)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.mainMenu = menu
        val period = sharedPref.getInt("period", Period.MONTH.ordinal)
        lastChecked = when (period) {
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

                    val periodId: Int = when (item.itemId) {
                        R.id.select_day -> {
                            binding.tvForPeriod.setText(R.string.day)
                            Period.DAY.ordinal
                        }
                        R.id.select_half_year -> {
                            binding.tvForPeriod.setText(R.string.half_year)
                            Period.HALF_YEAR.ordinal
                        }
                        R.id.select_three_days -> {
                            binding.tvForPeriod.setText(R.string.three_days)
                            Period.THREE_DAYS.ordinal
                        }
                        R.id.select_three_months -> {
                            binding.tvForPeriod.setText(R.string.three_months)
                            Period.THREE_MONTHS.ordinal
                        }
                        R.id.select_week -> {
                            binding.tvForPeriod.setText(R.string.week)
                            Period.WEEK.ordinal
                        }
                        R.id.select_year -> {
                            binding.tvForPeriod.setText(R.string.year)
                            Period.YEAR.ordinal
                        }
                        else -> {
                            binding.tvForPeriod.setText(R.string.month)
                            Period.MONTH.ordinal
                        }
                    }
                    sharedPref.edit().putInt("period", periodId).apply()
                    dataChanged()
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FirstFragment" ,"onDestroyView(): clearing screen data")
        clearBalData()
        lastChecked = null
        _binding = null
        _sharedPref = null
    }

    private fun clearBalData() {
        outlay = 0f
        income = 0f
        balance = 0f
    }
}
