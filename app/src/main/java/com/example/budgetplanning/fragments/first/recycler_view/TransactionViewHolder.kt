package com.example.budgetplanning.fragments.first.recycler_view

import android.app.ActionBar
import android.content.DialogInterface
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetplanning.R
import com.example.budgetplanning.data.entities.Transaction
import com.example.budgetplanning.databinding.DatePickerBinding
import com.example.budgetplanning.databinding.TimePickerBinding
import com.example.budgetplanning.databinding.TransactionItemBinding
import com.example.budgetplanning.utils.DateConverter
import com.example.budgetplanning.utils.TextUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.properties.Delegates


class TransactionViewHolder(val binding: TransactionItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private fun setDateText(date: String) {
        binding.tvDate.text = date
    }

    private fun setTimeText(time: String) {
        binding.tvTime.text = time
    }

    private fun setChangeAmountText(changeAmount: String) {
        binding.tvChangeAmount.text = changeAmount
    }

    lateinit var parentAdapter: TransactionAdapter
    var positionInAdapter by Delegates.notNull<Int>()
    lateinit var boundTransaction: Transaction

    fun bind(adapter: TransactionAdapter, position: Int) {
        parentAdapter = adapter
        positionInAdapter = position
        boundTransaction = parentAdapter.getItemAt(positionInAdapter)

        setDateText(boundTransaction.dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
        setTimeText(boundTransaction.dateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)))
        setChangeAmountText(
            TextUtils.floatToMoney(
                boundTransaction.changeAmount,
                binding.root.resources
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

                        alertBuilder.setPositiveButton(context.getString(R.string.yes),
                            DialogInterface.OnClickListener { _, _ ->
                                parentAdapter.transactionViewModel.delete(boundTransaction)
                                parentAdapter.removeItemAt(adapterPosition)
                            })

                        alertBuilder.setNegativeButton(context.getString(R.string.cancel),
                            DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

                        alertBuilder.show()
                        true
                    }
                    // ????????????
                    // ???????????? ?????????? ????????
                    R.id.iEdit -> {
                        val iEditAlertDialog: AlertDialog = AlertDialog.Builder(context).create()
                        iEditAlertDialog.setTitle(context.getString(R.string.edit))

                        val linearLayout = LinearLayout(context)
                        linearLayout.orientation = LinearLayout.VERTICAL
                        val etChangeAmount = EditText(context).also {
                            it.hint = context.getString(R.string.change_sum)
                            it.setText(boundTransaction.changeAmount.toString())
                            it.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
                        }
                        val etChangeComment = EditText(binding.root.context).also {
                            it.hint = context.getString(R.string.change_sum_comment)
                            boundTransaction.comment?.let { comment -> it.setText(comment) }
                        }
                        val etChangeDateTime = EditText(binding.root.context).also {
                            it.hint = context.getString(R.string.change_sum_date)
                            it.setText(boundTransaction.dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                            it.isFocusable = false
                        }

                        // ?????? ?? ???????? ??????????????????
                        // ?????? ?????? ?????? ???? ???????????????????? ???????? ???????????? ???????? ?? ??????????????
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
                                    datePicker.month,
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
                        linearLayout.addView(etChangeComment)
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
                                            val newTransaction = Transaction(
                                                boundTransaction.id,
                                                etChangeAmount.text.toString().toFloat(),
                                                DateConverter.toLocalDateTime(etChangeDateTime.text.toString())!!,
                                                etChangeComment.text.toString()
                                            )

                                            parentAdapter.transactionViewModel.update(newTransaction)
                                            parentAdapter.updateItemAt(
                                                positionInAdapter,
                                                newTransaction
                                            )
                                            iEditAlertDialog.dismiss()
                                        }
                                    }
                                }
                            )
                        }

                        linearLayout.addView(horizontalLinearLayout)

                        iEditAlertDialog.setView(linearLayout)
                        iEditAlertDialog.show()

                        true
                    }
                    R.id.iInfo -> {

                        val iEditAlertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                        iEditAlertDialogBuilder.setTitle(context.getString(R.string.info))

                        val linearLayout = LinearLayout(context)
                        linearLayout.orientation = LinearLayout.VERTICAL

                        val tvChangeAmount = TextView(context).also {
                            it.setText(boundTransaction.changeAmount.toString())
                        }
                        val tvChangeComment = TextView(binding.root.context).also {
                            boundTransaction.comment?.let { comment -> it.setText(comment) }
                        }
                        val tvChangeDateTime = TextView(binding.root.context).also {
                            it.setText(boundTransaction.dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        }


                        for(tv in mutableListOf(tvChangeAmount, tvChangeComment, tvChangeDateTime)){
                            tv.paint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.0f, context.resources.displayMetrics)
//                            tv.gravity = Gravity.CENTER_HORIZONTAL
                            val lp = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                0f
                            ).also{
                                val left = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, context.resources.displayMetrics).toInt()
                                val top = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, context.resources.displayMetrics).toInt()
                                val right = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, context.resources.displayMetrics).toInt()
                                val bottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15.0f, context.resources.displayMetrics).toInt()
                                it.setMargins(left, top, right, bottom)
                            }
                            tv.layoutParams = lp
                        }


                        linearLayout.addView(TextView(context).also {
                            it.setText(R.string.change_sum)
                        })
                        linearLayout.addView(tvChangeAmount)
                        linearLayout.addView(TextView(context).also {
                            it.setText(R.string.change_sum_comment)
                        })
                        linearLayout.addView(tvChangeComment)
                        linearLayout.addView(TextView(context).also {
                            it.setText(R.string.change_sum_date)
                        })
                        linearLayout.addView(tvChangeDateTime)

                        iEditAlertDialogBuilder.setNeutralButton(R.string.ok){
                            _,_ ->
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

}