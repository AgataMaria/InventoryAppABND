package com.example.android.inventoryappabnd;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry;
import com.example.android.inventoryappabnd.Data.InventoryCursorAdapter;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    /*
    Variables and elements
     */
    public static final int INVENTORY_LOADER_ID = 0;
    private InventoryCursorAdapter adapter;
    private TextView emptyStateTV;
    private FloatingActionButton addFab;

    /*
    Lifecycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //setup a FAB to open Entry Activity, which allows to add items to the Inventory
        addFab = findViewById(R.id.fab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EntryEditor.class);
                startActivity(intent);
            }
        });

        //setup an empty state view
        emptyStateTV = findViewById(R.id.empty_state_view_tv);

        //setup a ListView to be populated with data from the loaded cursor
        ListView inventoryListView = findViewById(R.id.listview);
        inventoryListView.setEmptyView(emptyStateTV);
        adapter = new InventoryCursorAdapter(this, null);
        //set adapter to the ListView and set an OnItemClickListener on the adapter
        inventoryListView.setAdapter(adapter);
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the selected item content uri by appending base content uri with the ListView item id from the adapter
                Uri selectedItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                Intent editorEditModeIntent = new Intent(InventoryActivity.this, EntryEditor.class);
                //check the value of the uri
                Log.v("item uri: ", String.valueOf(selectedItemUri));
                //pass the uri to the EntryEditor activity and start activity
                editorEditModeIntent.setData(selectedItemUri);
                startActivity(editorEditModeIntent);
            }
        });

        //initialise the Loader when the Activity is created
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

    /*
    Methods required by the LoaderManager
     */
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
        emptyStateTV.setText(R.string.empty_state_textview_text);
        adapter.swapCursor(data);
        }


    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }
}


