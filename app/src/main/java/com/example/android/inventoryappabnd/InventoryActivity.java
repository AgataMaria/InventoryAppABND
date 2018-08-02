package com.example.android.inventoryappabnd;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry;
import com.example.android.inventoryappabnd.Data.InventoryCursorAdapter;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // ID for the inventoryCursorLoader
    public static final int INVENTORY_LOADER_ID = 0;
    InventoryCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //setup a FAB to open Entry Activity, which allows to add items to the Inventory
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EntryEditor.class);
                startActivity(intent);
            }
        });

        //setup a ListView to be populated with data from the loaded cursor
        ListView inventoryListView = findViewById(R.id.listview);
        adapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(adapter);

        //initiate the Loader when the Activity is created
        getLoaderManager().initLoader(INVENTORY_LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // methods required by the LoaderManager
    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        // define projection scope for the db operation
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_TYPE,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryEntry.COLUMN_ITEM_QNT,
                InventoryEntry.COLUMN_ITEM_SUPP_NAME,
                InventoryEntry.COLUMN_ITEM_SUPP_NO
        };

        // create a Loader with relevant ID
        switch (id) {
            case INVENTORY_LOADER_ID:
                //create a CursorLoader - the cursor to return db query on all info from the database
                return new CursorLoader(
                        this, //context
                        InventoryEntry.CONTENT_URI,           //queried db URI
                        projection,    //projection for the query
                        null,   //selection
                        null,//selection arguments
                        null    //sort order
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);

    }
}


