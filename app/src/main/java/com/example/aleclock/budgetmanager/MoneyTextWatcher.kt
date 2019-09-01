package com.example.aleclock.budgetmanager

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat


class MoneyTextWatcher(val editText : EditText) : TextWatcher {

    private var current = ""

    override fun afterTextChanged(s: Editable?) {
        if(s.toString() != current){
            editText.removeTextChangedListener(this)

            // val cleanString = s.toString().replace("[$,.]", "")

            var replaceable = String.format("[%s,.]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
            var cleanString = s.toString().replace(replaceable, "")

            val parsed = cleanString.toDouble()
            val formatted = NumberFormat.getCurrencyInstance().format((parsed/100))



            current = formatted
            editText.setText(formatted)
            editText.setSelection(formatted.length)

            editText.addTextChangedListener(this)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

}
