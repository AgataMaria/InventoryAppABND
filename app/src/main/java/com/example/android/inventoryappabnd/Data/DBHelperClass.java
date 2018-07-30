package com.example.android.inventoryappabnd.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry;

public class DBHelperClass extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Inventory.db";
    public static final int DATABASE_VERSION = 1;


    // Constructor for the DB Helper Class
    public DBHelperClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_ITEM_TYPE + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_QNT + " INTEGER, "
                + InventoryEntry.COLUMN_ITEM_SUPP_NAME + " TEXT,"
                + InventoryEntry.COLUMN_ITEM_SUPP_NO + " TEXT);";

        db.execSQL(SQL_CREATE_TABLE);
        Log.v("helper class", SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
