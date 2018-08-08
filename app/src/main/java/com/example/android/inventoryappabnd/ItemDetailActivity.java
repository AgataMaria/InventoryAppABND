package com.example.android.inventoryappabnd;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry;

public class ItemDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    /*
   Detail view form fields elements
    */
    TextView detailviewItemNameTV;
    TextView detailviewItemPriceTV;
    TextView detailviewItemQntTV;
    ImageButton lessQntButton;
    ImageButton moreQntButton;
    TextView detailviewItemSuppTV;
    TextView detailviewItemSuppNoTV;

    /*
    Variables
     */
    private Uri currentItemUri;
    public static final int DETAILVIEW_LOADER_ID = 2;
    int itemQnt = 0;

    /*
    Lifecycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //retrieve item uri from the Intent that started the activity
        //store the uri in selectedItemUri variable
        Intent detailViewIntent = getIntent();
        currentItemUri = detailViewIntent.getData();

        // Assign the form elements to views by ID and set / apply any visual changes
        detailviewItemNameTV = findViewById(R.id.detailview_item_name_tv);
        detailviewItemPriceTV = findViewById(R.id.detailview_item_prc_tv);
        detailviewItemQntTV = findViewById(R.id.detailview_item_qnt_tv);
        detailviewItemSuppTV = findViewById(R.id.detailview_item_suppname_tv);
        detailviewItemSuppNoTV = findViewById(R.id.detailview_item_suppno_tv);
        //TODO: setup an intent to call the supplier

        //Find Button elements and listen for clicks to reduce or increase the quantity of the product accordingly
        lessQntButton = findViewById(R.id.reduce_qnt_button);
        moreQntButton = findViewById(R.id.add_qnt_button);

    }

    private void viewItem() {
    //TODO: setup a method that will increase or decrease the itemQnt when the buttons are pressed and update the database
    }

    /*
     Methods required by the Loader Manager
      */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //loader to query the db for item values in the background thread
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
            case DETAILVIEW_LOADER_ID:

                //create a CursorLoader - the cursor to return db query on a specific item
                return new CursorLoader(
                        this, //context
                        currentItemUri,           //queried db URI
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor loaderCursor) {
        //if cursor is null / no rows then just return
        if (loaderCursor == null || loaderCursor.getCount() < 1) {
            return;
        }
        //move the cursor to the first position and get column index numbers
        if (loaderCursor.moveToFirst()) {
            int idColumnIndex = loaderCursor.getColumnIndex(InventoryEntry._ID);
            int itemNameColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int itemPriceColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
            int itemQntColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QNT);
            int itemSuppNameColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPP_NAME);
            int itemSuppNoColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPP_NO);

            //now get values from the cursor
            int id = loaderCursor.getInt(idColumnIndex);
            String itemName = loaderCursor.getString(itemNameColumnIndex);
            double itemPrice = loaderCursor.getDouble(itemPriceColumnIndex);
            int itemQnt = loaderCursor.getInt(itemQntColumnIndex);
            String itemSuppName = loaderCursor.getString(itemSuppNameColumnIndex);
            String itemSuppNo = loaderCursor.getString(itemSuppNoColumnIndex);

            //update the textview fields in the list items layout with data returned
            detailviewItemNameTV.setText(itemName);
            detailviewItemPriceTV.setText(Double.toString(itemPrice));
            detailviewItemQntTV.setText(Integer.toString(itemQnt));
            detailviewItemSuppTV.setText(itemSuppName);
            detailviewItemSuppNoTV.setText(itemSuppNo);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();

    }
/*
MENU methods
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


