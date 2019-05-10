package com.example.aleclock.budgetmanager

import android.util.Log

class Course (var nome: String, var codice: String, var descrizione: String, var materiale: String) {}

class Model private constructor() {

    private val dbHelper = TransationDBHelper(MainActivity.applicationContext())

    fun getCourseDetail(code:String): Course {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            TransationDBHelper.TransationTable.TABLE_NAME,
            arrayOf(
                TransationDBHelper.TransationTable.COLUMN_TRANSATION_ID,
                TransationDBHelper.TransationTable.COLUMN_CODICE,
                TransationDBHelper.TransationTable.COLUMN_TIMESTAMP
            ),
            "${TransationDBHelper.TransationTable.COLUMN_CODICE}=?",
            arrayOf(code),
            "",
            "",
            ""
        )

        val course = Course(code,"","","")

        if (cursor.moveToNext()) {
            course.nome=cursor.getString(0)
            course.codice=cursor.getString(1)
            course.materiale=cursor.getString(2)
            course.descrizione=cursor.getString(3)
        }
        cursor.close()

        Log.d("", course.toString())

        return course
    }
}