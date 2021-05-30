package com.example.budgetplanning.fragments.second

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.budgetplanning.R
import com.example.budgetplanning.data.entities.Transaction
import com.example.budgetplanning.data.viewmodels.TransactionViewModel
import com.example.budgetplanning.databinding.DatePickerBinding
import com.example.budgetplanning.databinding.FragmentSecondBinding
import com.example.budgetplanning.databinding.TimePickerBinding
import com.example.budgetplanning.utils.KeyboardUtils
import com.example.budgetplanning.utils.TextUtils
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class SecondFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var isSumNegative = true

    private lateinit var transactionViewModel: TransactionViewModel
    var localDateTime: LocalDateTime = LocalDateTime.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        binding.bAdd.isEnabled = false
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
//        menu.setGroupVisible(R.id.statistics_group, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class ChangeSumTextWatcher(val binding: FragmentSecondBinding) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val regex = Regex("^\\d{1,6}(\\.\\d{0,2})?")
            binding.bAdd.isEnabled = binding.etChangeSum.text.isNotEmpty() && regex.matches(s!!)
        }

        override fun afterTextChanged(s: Editable?) {}

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bAdd
        binding.bAdd.setOnClickListener {
            insertIntoDatabase()
        }

        // etChangeSum
        val etChangeSum: EditText = binding.etChangeSum
        etChangeSum.hint = etChangeSum.hint.toString() + "(${TextUtils.getMoneySymbol()})"
        etChangeSum.addTextChangedListener(ChangeSumTextWatcher(binding))

        // etChangeDateTime
        val etChangeDateTime = binding.etChangeDateTime
        etChangeDateTime.setText(getString(R.string.now))

        etChangeDateTime.setOnClickListener {
            displayDateTimePicker(view.context)
        }

        // sSign
        ArrayAdapter.createFromResource(
            view.context,
            R.array.signs,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sSign.adapter = adapter
        }
        binding.sSign.onItemSelectedListener = this
    }

    private fun insertIntoDatabase() {

        var changeAmount =
            binding.etChangeSum.text.toString().removeSuffix(".").toDouble()
        if (isSumNegative){
            changeAmount = 0 - changeAmount
        }

        val comment = binding.etChangeComment.text.toString()

        val transaction = Transaction(changeAmount = changeAmount, comment = comment, dateTime = localDateTime)
        transactionViewModel.insertAll(transaction)
        Toast.makeText(requireContext(), "Successfully added new transaction", Toast.LENGTH_LONG)
            .show()
        Log.d("SecondFragment", "successfully added new transaction")
        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }

    private fun updateLocalDateTime(newDateTime: LocalDateTime) {
        localDateTime = newDateTime
        binding.etChangeDateTime.setText(
            localDateTime.format(
                DateTimeFormatter.ofLocalizedDateTime(
                    FormatStyle.MEDIUM
                )
            )
        )
    }


    override fun onStop() {
        super.onStop()
        Log.d("SecondFragment", "onStop(): Wait! Don't kill me! No-o-o-o!")
        KeyboardUtils.hideKeyboard(activity as Activity)
    }

    private fun displayDateTimePicker(context: Context) {
        val dpBinding = DatePickerBinding.inflate(LayoutInflater.from(context))
        val tpBinding = TimePickerBinding.inflate(LayoutInflater.from(context))
        val dialogView = dpBinding.root
        dpBinding.dpDate.maxDate = System.currentTimeMillis()
        var alertDialog: AlertDialog = AlertDialog.Builder(context).create()

        var dateTime: LocalDateTime = LocalDateTime.now()


        dpBinding.bSet.setOnClickListener {
            val datePicker = dpBinding.dpDate
            dateTime = LocalDateTime.of(
                datePicker.year,
                datePicker.month+1,
                datePicker.dayOfMonth,
                0, 0
            )
            alertDialog.dismiss()
            alertDialog = AlertDialog.Builder(context).create()
            alertDialog.setView(tpBinding.root)
            alertDialog.show()
        }

        tpBinding.bSet.setOnClickListener {
            val timePicker = tpBinding.tpTime
            dateTime = LocalDateTime.of(
                dateTime.year,
                dateTime.month,
                dateTime.dayOfMonth,
                timePicker.hour,
                timePicker.minute
            )
//            dateTime.plusHours(timePicker.hour.toLong())
//            dateTime.plusMinutes(timePicker.minute.toLong())

            updateLocalDateTime(dateTime)
            alertDialog.dismiss()
        }

        tpBinding.bSkip.setOnClickListener {
            updateLocalDateTime(dateTime)
            alertDialog.dismiss()
        }


        alertDialog.setView(dialogView)
        alertDialog.show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        isSumNegative = position == 1
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        isSumNegative = false
    }


}