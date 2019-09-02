package com.example.aleclock.budgetmanager

import android.text.Editable
import android.text.TextUtils.concat
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import java.text.NumberFormat
import java.util.*
import java.util.Collections.replaceAll




class MoneyTextWatcher(val editText : EditText) : TextWatcher {

    private var current = ""

    override fun afterTextChanged(s: Editable?) {
        if(s.toString() != current){
            editText.removeTextChangedListener(this)

/*          US DOLLAR CODE
            val cleanString = s.toString().replace("""[$,.]""".toRegex(), "")
            val parsed = cleanString.toDouble()
            val formatted = NumberFormat.getCurrencyInstance(Locale.US).format((parsed/100))*/

            // EURO CODE
            val cleanString = s.toString().replace("""[,. €]""".toRegex(), "")
            Log.d("textChanged", s.toString() + " , " + cleanString)
            val parsed = cleanString.toDouble()
            val formatted = concat ("€ " ,(parsed/100).toString()).toString()

            //Log.d("textChanged", s.toString() + " , " + cleanString + " , " + parsed + ", " + formatted)


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
