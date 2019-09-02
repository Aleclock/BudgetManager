package com.example.aleclock.budgetmanager

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.*


// https://stackoverflow.com/questions/33319898/currency-input-with-2-decimal-format
// https://stackoverflow.com/questions/5107901/better-way-to-format-currency-input-edittext/8275680

class MoneyTextWatcher(private val editText : EditText) : TextWatcher {

    private var current = ""

    override fun afterTextChanged(s: Editable?) {
        if(s.toString() != current){
            editText.removeTextChangedListener(this)

            val cleanString = s.toString().replace("""[€,.]""".toRegex(), "")
            val parsed = cleanString.toDouble()
            val value = NumberFormat.getCurrencyInstance(Locale.US).format((parsed/100))
            val formatted = value.replace("""[$]""".toRegex(), "€ ")

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
