package com.example.budgetplanning.fragments.first

import android.text.Editable
import android.text.TextWatcher
import android.util.Log

class MoneyNumberWatcher : TextWatcher {
    lateinit var changedTextPart: CharSequence
    var newTextStart: Int = 0
    var newTextLength: Int = 0

    var isTextMatching: Boolean = true
    var setToZero: Boolean = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        Log.d("MoneyNumberWatcher", "beforeTextChanged(): unchanged string: \"$s\" " +
                "changes start at: $start with count: $count and inserted string length: $after")

        // If trying to remove last symbol
        Log.d("MoneyNumberWatcher", " after = $after")
        if (Regex("[-,+]?\\d").matches(s!!) && after == 0) {
            Log.d("MoneyNumberWatcher", "Removing last digit!")
            setToZero = true
        }
        if (!setToZero) {
            // Gather info for regex check
            changedTextPart = s?.substring(start, count + start)!!
            newTextStart = start
            newTextLength = after
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!setToZero) {
            val regex = Regex("^[-,+]?\\d{1,7}(\\.\\d{0,2})?")
            Log.d("MoneyNumberWatcher", "onTextChanged(): changed string: \"$s\" " +
                    "changes start at: $start with count: $count and replaced string length: $before")
            Log.d("MoneyNumberWatcher", "onTextChanged(): s matches regex: ${regex.matches(s!!)}")
            isTextMatching = regex.matches(s!!)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        Log.d("MoneyNumberWatcher", "afterTextChanged(): s: ${s.toString()}")
        if(setToZero){
            setToZero = false
            s?.replace(0, s.length, "0")
        }
        else if (!isTextMatching) {
            Log.d("MoneyNumberWatcher", "New text needs rollback. Replacing in editable " +
                    "starting at $newTextStart for $newTextLength with $changedTextPart")
            s?.replace(newTextStart, newTextStart + newTextLength, changedTextPart)
            isTextMatching = true
        }
    }
}