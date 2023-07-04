package Database

import Models.Budget
import Models.Category
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class BudgetDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    private val TABLE_BUDGET = "BUDGETS"
    private val TABLE_CATEGORY = "CATEGORIES"

    private val CREATE_BUDGET = ("CREATE TABLE IF NOT EXISTS " + TABLE_BUDGET
            + "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "TITLE TEXT UNIQUE NOT NULL, "
            + "DESCRIPTION TEXT,"
            + "BUDGET DECIMAL(10, 2),"
            + "CATEGORY_ID INTEGER NOT NULL,"
            + "FOREIGN KEY(CATEGORY_ID) REFERENCES " + TABLE_CATEGORY + "(_ID)"
            + "ON UPDATE CASCADE"
            + " "
            + "ON DELETE CASCADE"
            + ");")

    companion object {
        val COLUMN_ID = "_ID"
        val COLUMN_TITLE = "TITLE"
        val COLUMN_DESC = "DESCRIPTION"
        val COLUMN_BUDGET = "BUDGET"
        val COLUMN_CATEGORY_ID = "CATEGORY_ID"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_BUDGET)
        db.execSQL("PRAGMA foreign_keys=ON")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_BUDGET")

        onCreate(db)
    }

    fun insert(budget: Budget){
        val values = ContentValues().apply {
            put(COLUMN_TITLE, budget.title)
            put(COLUMN_DESC, budget.description)
            put(COLUMN_BUDGET, budget.budget)
            put(COLUMN_CATEGORY_ID, budget.category_id)
        }

        val db = this.writableDatabase

        db.insert(TABLE_BUDGET, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun read(id: String): ArrayList<Budget> {
        val budgets = ArrayList<Budget>()
        val db = this.writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_BUDGET WHERE $COLUMN_ID='$id'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(CREATE_BUDGET)
            return ArrayList()
        }

        var id: Int
        var title: String
        var description: String
        var budget: Double
        var category_id: Int


        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getString(cursor.getColumnIndex(COLUMN_ID)).toInt()
                title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                description = cursor.getString(cursor.getColumnIndex(COLUMN_DESC))
                budget = cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET)).toDouble()
                category_id = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID)).toInt()

                budgets.add(Budget(title, description, budget, category_id, id))
                cursor.moveToNext()
            }
        }
        return budgets
    }

    @SuppressLint("Range")
    fun readAll(): ArrayList<Budget> {
        val budgets = ArrayList<Budget>()
        val db = this.writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_BUDGET", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(CREATE_BUDGET)
            return ArrayList()
        }

        var id: Int
        var title: String
        var description: String
        var budget: Double
        var category_id: Int

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getString(cursor.getColumnIndex(COLUMN_ID)).toInt()
                title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                description = cursor.getString(cursor.getColumnIndex(COLUMN_DESC))
                budget = cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET)).toDouble()
                category_id = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID)).toInt()

                budgets.add(Budget(title, description, budget, category_id, id))
                cursor.moveToNext()
            }
        }
        return budgets
    }

    fun update(id: String, budget: Budget){
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_TITLE, budget.title)
            put(COLUMN_DESC, budget.description)
            put(COLUMN_BUDGET, budget.budget)
            put(COLUMN_CATEGORY_ID, budget.category_id)
        }

        val selection = COLUMN_ID + " LIKE ?"
        val selectionArgs = arrayOf(id)
        val count = db.update(
            TABLE_BUDGET,
            values,
            selection,
            selectionArgs)
    }

    fun delete(id: String){
        val db = this.writableDatabase

        val selection = COLUMN_ID + " LIKE ?"

        val selectionArgs = arrayOf(id)

        db.delete(TABLE_BUDGET, selection, selectionArgs)
    }

    fun exists(categoryID: String) : Boolean{
        val db = this.writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_BUDGET WHERE $COLUMN_CATEGORY_ID='$categoryID'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(CREATE_BUDGET)
            return false
        }
        if(cursor.count <= 0) return false

        return true
    }

    @SuppressLint("Range")
    fun getCategoryBudget(id: String): ArrayList<Budget> {
        val budgets = ArrayList<Budget>()
        val db = this.writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_BUDGET WHERE CATEGORY_ID ='$id'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(CREATE_BUDGET)
            return ArrayList()
        }

        var id: Int
        var title: String
        var description: String
        var budget: Double
        var category_id: Int


        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getString(cursor.getColumnIndex(COLUMN_ID)).toInt()
                title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                description = cursor.getString(cursor.getColumnIndex(COLUMN_DESC))
                budget = cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET)).toDouble()
                category_id = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID)).toInt()

                budgets.add(Budget(title, description, budget, category_id, id))
                cursor.moveToNext()
            }
        }
        return budgets
    }
}