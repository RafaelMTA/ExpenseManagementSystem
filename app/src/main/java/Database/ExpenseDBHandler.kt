package Database

import Models.Budget
import Models.Expense
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class ExpenseDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    private val TABLE_EXPENSE = "EXPENSES"
    private val TABLE_CATEGORY = "CATEGORIES"

    private val CREATE_EXPENSE = ("CREATE TABLE IF NOT EXISTS " + TABLE_EXPENSE
            + "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "TITLE TEXT UNIQUE NOT NULL, "
            + "COST DECIMAL(10,2),"
            + "CATEGORY_ID INTEGER NOT NULL,"
            + "FOREIGN KEY(CATEGORY_ID) REFERENCES " + TABLE_CATEGORY + "(_ID)"
            + "ON UPDATE CASCADE"
            + " "
            + "ON DELETE CASCADE"
            + ");")

    companion object {
        val COLUMN_ID = "_ID"
        val COLUMN_TITLE = "TITLE"
        val COLUMN_COST = "COST"
        val COLUMN_CATEGORY_ID = "CATEGORY_ID"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_EXPENSE)
        db.execSQL("PRAGMA foreign_keys=ON")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSE")
        onCreate(db)
    }

    fun insert(expense: Expense){
        val values = ContentValues().apply {
            put(COLUMN_TITLE, expense.title)
            put(COLUMN_COST, expense.cost)
            put(COLUMN_CATEGORY_ID, expense.category_id)
        }

        val db = this.writableDatabase

        db.insert(TABLE_EXPENSE, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun read(id: String): ArrayList<Expense> {
        val expenses = ArrayList<Expense>()
        val db = this.writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_EXPENSE WHERE ${COLUMN_ID}='$id'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(CREATE_EXPENSE)
            return ArrayList()
        }

        var id: Int
        var title: String
        var cost: Double
        var category_id: Int

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                cost = cursor.getDouble(cursor.getColumnIndex(COLUMN_COST))
                category_id = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID))

                expenses.add(Expense(title, cost, category_id, id))
                cursor.moveToNext()
            }
        }
        return expenses
    }

    @SuppressLint("Range")
    fun readAll(): ArrayList<Expense> {
        val expenses = ArrayList<Expense>()
        val db = this.writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_EXPENSE", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(CREATE_EXPENSE)
            return ArrayList()
        }

        var id: Int
        var title: String
        var cost: Double
        var category_id: Int

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                cost = cursor.getDouble(cursor.getColumnIndex(COLUMN_COST))
                category_id = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID))

                expenses.add(Expense(title, cost, category_id, id))
                cursor.moveToNext()
            }
        }
        return expenses
    }

    fun update(id: String, expense: Expense){
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_TITLE, expense.title)
            put(COLUMN_COST, expense.cost)
            put(COLUMN_CATEGORY_ID, expense.category_id)
        }

        val selection = COLUMN_ID + " LIKE ?"
        val selectionArgs = arrayOf(id)
        val count = db.update(
            TABLE_EXPENSE,
            values,
            selection,
            selectionArgs)
    }

    fun delete(id: String){
        val db = this.writableDatabase

        val selection = COLUMN_ID + " LIKE ?"

        val selectionArgs = arrayOf(id)

        db.delete(TABLE_EXPENSE, selection, selectionArgs)
    }

    @SuppressLint("Range")
    fun getTotalCost(categoryId: Int): Double {
        val db = this.writableDatabase

        var cursor = db.rawQuery("SELECT SUM(COST) AS TOTAL FROM EXPENSES WHERE CATEGORY_ID = $categoryId", null)

        var total = 0.0

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                total += cursor.getDouble(0)
                cursor.moveToNext()
            }
        }
        return total
    }

    @SuppressLint("Range")
    fun getTotalCostOnUpdate(categoryId: Int, expenseId: Int): Double {
        val db = this.writableDatabase

        var cursor = db.rawQuery("SELECT SUM(COST) AS TOTAL FROM EXPENSES WHERE CATEGORY_ID = $categoryId AND _ID != $expenseId", null)

        var total = 0.0

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                total += cursor.getDouble(0)
                cursor.moveToNext()
            }
        }
        return total
    }
}