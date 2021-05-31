package com.example.budgetplanning.fragments.balance_history.recycler_view

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetplanning.R
import com.example.budgetplanning.data.entities.BalanceChange
import com.example.budgetplanning.data.entities.Transaction
import com.example.budgetplanning.databinding.BalanceHistoryItemBinding
import com.example.budgetplanning.databinding.DatePickerBinding
import com.example.budgetplanning.databinding.TimePickerBinding
import com.example.budgetplanning.utils.DateConverter
import com.example.budgetplanning.utils.TextUtils
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.properties.Delegates


class BalanceChangeViewHolder(val binding: BalanceHistoryItemBinding) :
    RecyclerView.ViewHolder(binding.root),
    TextWatcher {

    private fun setDateText(date: String) {
        binding.tvDate.text = date
    }

    private fun setTimeText(time: String) {
        binding.tvTime.text = time
    }

    private fun setChangeAmountText(changeAmount: String) {
        binding.tvChangeAmount.text = changeAmount
    }

    lateinit var parentAdapter: BalanceChangeAdapter
    var positionInAdapter by Delegates.notNull<Int>()
    lateinit var boundBalanceChange: BalanceChange

    fun bind(adapter: BalanceChangeAdapter, position: Int) {
        parentAdapter = adapter
        positionInAdapter = position
        boundBalanceChange = parentAdapter.getItemAt(positionInAdapter)

        setDateText(boundBalanceChange.dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
        setTimeText(boundBalanceChange.dateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)))
        setChangeAmountText(
            TextUtils.doubleToMoney(
                boundBalanceChange.newValue,
                binding.root.context,
                false
            )
        )

        onBind()
    }

    private fun onBind() {
        val context = binding.root.context
        // bMore
        binding.bMore.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.iRemove -> {
                        val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                        alertBuilder.setTitle(context.getString(R.string.remove) + "?")

                        alertBuilder.setPositiveButton(context.getString(R.string.yes)
                        ) { _, _ ->
                            parentAdapter.balanceChangeViewModel.delete(boundBalanceChange)
                            parentAdapter.removeItemAt(adapterPosition)
                        }

                        alertBuilder.setNegativeButton(context.getString(R.string.cancel)
                        ) { dialog, _ -> dialog.cancel() }

                        alertBuilder.show()
                        true
                    }
                    // АХТУНГ
                    // Дальше много кода
                    R.id.iEdit -> {
                        val iEditAlertDialog: AlertDialog = AlertDialog.Builder(context).create()
                        iEditAlertDialog.setTitle(context.getString(R.string.edit))

                        val linearLayout = LinearLayout(context)
                        linearLayout.orientation = LinearLayout.VERTICAL
                        val etChangeAmount = EditText(context).also {
                            it.hint = context.getString(R.string.change_sum)
                            val decFor = DecimalFormat("#0.00")
                            it.setText(decFor.format(boundBalanceChange.newValue))
                            it.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
                            it.inputType =
                                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                            it.addTextChangedListener(this)
                        }
                        val etChangeDateTime = EditText(binding.root.context).also {
                            it.hint = context.getString(R.string.change_sum_date)
                            it.setText(boundBalanceChange.dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                            it.isFocusable = false
                        }

                        // Вот в этом слушателе
                        // Это всё что бы отобразить меню выбора даты и времени
                        etChangeDateTime.setOnClickListener {
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
                                    datePicker.month + 1,
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
                                etChangeDateTime.setText(
                                    dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                        .toString()
                                )
                                alertDialog.dismiss()
                            }

                            tpBinding.bSkip.setOnClickListener {
                                etChangeDateTime.setText(
                                    dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                        .toString()
                                )
                                alertDialog.dismiss()
                            }


                            alertDialog.setView(dialogView)
                            alertDialog.show()
                        }


                        linearLayout.addView(etChangeAmount)
                        linearLayout.addView(etChangeDateTime)

                        val horizontalLinearLayout = LinearLayout(context).also {
                            it.orientation = LinearLayout.HORIZONTAL
                            it.addView(
                                Button(context).also { bCancel ->
                                    bCancel.layoutParams = LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1.0f
                                    )
                                    bCancel.setText(context.getText(R.string.cancel))
                                    bCancel.setOnClickListener { bCancel ->
                                        iEditAlertDialog.cancel()
                                    }
                                }
                            )
                            it.addView(
                                Button(context).also { bSave ->
                                    bSave.layoutParams = LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1.0f
                                    )
                                    bSave.text = context.getString(R.string.save)
                                    bSave.setOnClickListener { bSave ->
                                        if (etChangeAmount.text.isEmpty()) {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.change_sum_not_empty),
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        } else {
                                            val newBalanceChange = BalanceChange(
                                                boundBalanceChange.id,
                                                etChangeAmount.text.toString().toDouble(),
                                                DateConverter.toLocalDateTime(etChangeDateTime.text.toString())!!,
                                            )

                                            parentAdapter.balanceChangeViewModel.update(newBalanceChange)
                                            parentAdapter.updateItemAt(
                                                positionInAdapter,
                                                newBalanceChange
                                            )
                                            iEditAlertDialog.dismiss()
                                        }
                                    }
                                    this.bSave = bSave
                                }
                            )
                        }

                        linearLayout.addView(horizontalLinearLayout)

                        iEditAlertDialog.setView(linearLayout)
                        iEditAlertDialog.show()

                        true
                    }
                    R.id.iInfo -> {

                        val iEditAlertDialogBuilder: AlertDialog.Builder =
                            AlertDialog.Builder(context)
                        iEditAlertDialogBuilder.setTitle(context.getString(R.string.info))

                        val linearLayout = LinearLayout(context)
                        linearLayout.orientation = LinearLayout.VERTICAL

                        val tvChangeAmount = TextView(context).also {
                            it.setText(boundBalanceChange.newValue.toString())
                        }
                        val tvChangeDateTime = TextView(binding.root.context).also {
                            it.setText(boundBalanceChange.dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        }


                        for (tv in mutableListOf(
                            tvChangeAmount,
                            tvChangeDateTime
                        )) {
                            tv.paint.textSize = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                20.0f,
                                context.resources.displayMetrics
                            )
//                            tv.gravity = Gravity.CENTER_HORIZONTAL
                            val lp = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                0f
                            ).also {
                                val left = TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    5.0f,
                                    context.resources.displayMetrics
                                ).toInt()
                                val top = TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    5.0f,
                                    context.resources.displayMetrics
                                ).toInt()
                                val right = TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    5.0f,
                                    context.resources.displayMetrics
                                ).toInt()
                                val bottom = TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    15.0f,
                                    context.resources.displayMetrics
                                ).toInt()
                                it.setMargins(left, top, right, bottom)
                            }
                            tv.layoutParams = lp
                        }


                        linearLayout.addView(TextView(context).also {
                            it.setText(R.string.change_sum)
                        })
                        linearLayout.addView(tvChangeAmount)
                        linearLayout.addView(TextView(context).also {
                            it.setText(R.string.change_sum_date)
                        })
                        linearLayout.addView(tvChangeDateTime)

                        iEditAlertDialogBuilder.setNeutralButton(android.R.string.ok) { _, _ ->
                        }

                        iEditAlertDialogBuilder.setView(linearLayout)

                        iEditAlertDialogBuilder.create()
                        iEditAlertDialogBuilder.show()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            popupMenu.inflate(R.menu.menu_transaction_more)

            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("TransactionViewHolder", "Error showing menu icons.", e)
            } finally {
                popupMenu.show()
            }
        }
    }

    var bSave: Button? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (bSave != null /*&& s != null*/) {
            @Suppress("RegExpRedundantEscape") val regex = Regex("^[-,+]?\\d{1,6}([\\.,]\\d{0,2})?")
            bSave!!.isEnabled = s!!.isNotEmpty() && regex.matches(s)
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

}