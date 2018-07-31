package com.example.android.inventoryappabnd;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry;
import com.example.android.inventoryappabnd.Data.InventoryCursorAdapter;

public class InventoryActivity extends AppCompatActivity {

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
        showDbInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showDbInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDbInfo();
    }

     private void showDbInfo() {

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

        // cursor to return db query - this one shows all info from the database
        Cursor cursor = getContentResolver().query(InventoryEntry.CONTENT_URI, projection, null, null, null);

        ListView inventoryListView = findViewById(R.id.listview);
        InventoryCursorAdapter adapter = new InventoryCursorAdapter(this, cursor);
        inventoryListView.setAdapter(adapter);

    }

}


