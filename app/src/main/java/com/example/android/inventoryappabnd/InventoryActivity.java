package com.example.android.inventoryappabnd;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void displayDatabaseInfo() {
        myDBHelper = new DBHelperClass(this);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        TextView summaryView = (TextView) findViewById(R.id.tv_main_summary);

        // define projection scope for the db query method
        String[] projection = {
                InventoryEntry._ID, InventoryEntry.COLUMN_ITEM_TYPE, InventoryEntry.COLUMN_ITEM_SN, InventoryEntry.COLUMN_USER
        };

        // String selectionByItemType = InventoryEntry.COLUMN_ITEM_TYPE + "=?";
        // String[] selectionByItemTypeArgs = new String[]{String.valueOf(InventoryEntry.ITEM_TYPE_MOBILE)};

        // cursor to return db query - uses projection as the first param after the table name, if null will return ALL
        // Cursor cursor = db.query(InventoryEntry.TABLE_NAME, projection, selectionByItemType, selectionByItemTypeArgs, null, null, null);

        Cursor cursor = db.query(InventoryEntry.TABLE_NAME, projection, null, null, null, null, null);
        try {
            summaryView.setText("You currently have " + cursor.getCount() + " items in your inventory.");
        } finally {
            cursor.close();
        }
    }

    private void showDbInfo() {
        myDBHelper = new DBHelperClass(this);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        TextView dbInfo = (TextView) findViewById(R.id.tv_main_show_db);

        // define projection scope for the db query method
        String[] projection = {
                InventoryEntry._ID, InventoryEntry.COLUMN_ITEM_TYPE, InventoryEntry.COLUMN_ITEM_SN, InventoryEntry.COLUMN_USER
        };

        // cursor to return db query - this one shows all info from the database
        Cursor cursor = db.query(InventoryEntry.TABLE_NAME, projection, null, null, null, null, null);
        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int itemTypeColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_TYPE);
        int itemSNColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SN);
        int itemUserColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_USER);
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(idColumnIndex);
                String itemType = String.valueOf(cursor.getString(itemTypeColumnIndex));
                String itemSN = cursor.getString(itemSNColumnIndex);
                String itemUser = cursor.getString(itemUserColumnIndex);
                dbInfo.setText("\n" + id + TABLE_COLUMNS_SEPARATOR + itemType + TABLE_COLUMNS_SEPARATOR + itemSN + TABLE_COLUMNS_SEPARATOR + itemUser);
            }
        } finally {
                cursor.close();
            }
        }
}


