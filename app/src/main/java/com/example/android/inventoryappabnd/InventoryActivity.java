package com.example.android.inventoryappabnd;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventoryappabnd.Data.DBHelperClass;
import com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry;

public class InventoryActivity extends AppCompatActivity {

    private DBHelperClass myDBHelper;
    private String TABLE_COLUMNS_SEPARATOR = " | ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EntryEditor.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
        showDbInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayDatabaseInfo();
        showDbInfo();
    }

    private void displayDatabaseInfo() {
        myDBHelper = new DBHelperClass(this);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        TextView summaryView = (TextView) findViewById(R.id.tv_main_summary);

        // define projection scope for the db query method
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_TYPE,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryEntry.COLUMN_ITEM_QNT,
                InventoryEntry.COLUMN_ITEM_SUPP_NAME,
                InventoryEntry.COLUMN_ITEM_SUPP_NO
        };

        Cursor cursor = db.query(InventoryEntry.TABLE_NAME, projection, null, null, null, null, null);
        try {
            summaryView.setText("You currently have " + cursor.getCount() + " items in stock.");
        } finally {
            cursor.close();
        }
    }

    private void showDbInfo() {
        myDBHelper = new DBHelperClass(this);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();

        // define projection scope for the db query method
        String[] projection = {
                InventoryEntry._ID, InventoryEntry.COLUMN_ITEM_TYPE, InventoryEntry.COLUMN_ITEM_NAME, InventoryEntry.COLUMN_ITEM_PRICE, InventoryEntry.COLUMN_ITEM_QNT, InventoryEntry.COLUMN_ITEM_SUPP_NAME, InventoryEntry.COLUMN_ITEM_SUPP_NO
        };

        // cursor to return db query - this one shows all info from the database
        Cursor cursor = db.query(InventoryEntry.TABLE_NAME, projection, null, null, null, null, null);
        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int itemTypeColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_TYPE);
        int itemNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int itemPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
        int itemQntColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QNT);
        int itemSuppNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPP_NAME);
        int itemSuppNoColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPP_NO);
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(idColumnIndex);
                String itemType = String.valueOf(cursor.getString(itemTypeColumnIndex));
                String itemName = cursor.getString(itemNameColumnIndex);
                int itemPrice = cursor.getInt(itemPriceColumnIndex);
                int itemQnt = cursor.getInt(itemQntColumnIndex);
                String itemSuppName = cursor.getString(itemSuppNameColumnIndex);
                int itemSuppNo = cursor.getInt(itemSuppNoColumnIndex);

                //check what has been recorded in the db so far
                Log.v("Inv Act: row added", "\n" + id + TABLE_COLUMNS_SEPARATOR
                        + itemType + TABLE_COLUMNS_SEPARATOR
                        + itemName + TABLE_COLUMNS_SEPARATOR
                        + itemPrice + TABLE_COLUMNS_SEPARATOR
                        + itemQnt + TABLE_COLUMNS_SEPARATOR
                        + itemSuppName + TABLE_COLUMNS_SEPARATOR
                        + itemSuppNo + TABLE_COLUMNS_SEPARATOR);
            }
        } finally {
            cursor.close();
            db.close();
        }
    }
}


