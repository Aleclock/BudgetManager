package com.example.aleclock.budgetmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class TransationDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    object TransationTable {
        const val TABLE_NAME = "transation"

        const val COLUMN_TRANSATION_ID = "transation_id"
        const val COLUMN_CODICE = "codice"
        const val COLUMN_TIMESTAMP = "timestamp"

        internal const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_TRANSATION_ID TEXT NOT NULL, " +
                "$COLUMN_CODICE TEXT NOT NULL, " +
                "$COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "CONSTRAINT ${TABLE_NAME}_pk PRIMARY KEY ($COLUMN_CODICE) )"

        internal const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }


    override fun onCreate(db: SQLiteDatabase) {
        Log.d("budgetmenager", "Creazione delle tabelle del DB e inizializzazione")

        db.execSQL(TransationTable.SQL_CREATE_TABLE)


        val row = ContentValues()

        row.clear()
        row.put(TransationTable.COLUMN_TRANSATION_ID, "MFN1348")
        row.put(TransationTable.COLUMN_CODICE, "Agenti Intelligenti")
        db.insert(TransationTable.TABLE_NAME, null, row)

    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL(TransationTable.SQL_DROP_TABLE)
        onCreate(db)
    }


    companion object {
        // Su file system: /data/data/it.unito.di.educ.pdm17app3v2/databases

        private const val DATABASE_NAME = "Budget"
        private const val DATABASE_VERSION = 1
    }
}
