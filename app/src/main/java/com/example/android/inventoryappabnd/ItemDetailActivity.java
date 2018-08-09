package com.example.android.inventoryappabnd;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappabnd.Data.InventoryContract;
import com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry;

public class ItemDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final int DETAILVIEW_LOADER_ID = 2;
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
        detailviewItemSuppNoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String suppNo = detailviewItemSuppNoTV.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", suppNo, null));
                startActivity(intent);
            }

        });

        //Find Button elements and listen for clicks to reduce or increase the quantity of the product accordingly
        lessQntButton = findViewById(R.id.reduce_qnt_button);
        lessQntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQnt(currentItemUri);
            }
        });
        moreQntButton = findViewById(R.id.add_qnt_button);
        moreQntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQnt(currentItemUri);
            }
        });

        getLoaderManager().initLoader(DETAILVIEW_LOADER_ID, null, this);
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
            int itemNameColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int itemPriceColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
            int itemQntColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QNT);
            int itemSuppNameColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPP_NAME);
            int itemSuppNoColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPP_NO);

            //now get values from the cursor
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
            detailviewItemSuppNoTV.setText(getString(R.string.supp_phone_prefix) + " " +
                    itemSuppNo);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }

    /*
    Method for the Deletion warning
     */
    private void showDeletionWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deletion_warning);
        builder.setPositiveButton(R.string.deletion_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();    //delete helper method in this activity, not ContentProvider
            }
        });
        builder.setNegativeButton(R.string.deletion_void, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
    Deleting a single item from the database
    */
    private void deleteItem() {
        // this should only be used in the Edit Mode so check that currentItemUri is not null
        if (currentItemUri != null) {
            //use the provider's delete() method through ContentResolver, specifying item's uri
            int rowsDeleted = getContentResolver().delete(currentItemUri, null, null);
            // display a toast message to confirm whether deletion was successful or not
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.delete_failed_toast,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.item_deleted_toast,
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    /*
    MENU and NAVIGATION
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.detailview_action_delete:
                showDeletionWarning();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*
    Helper methods for changing quantity
    */
    private void decreaseQnt(Uri uri) {
        //setup scope for the cursor query and then get cursor
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_QNT};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        ContentValues cv = new ContentValues();
        if (!cursor.moveToFirst())
            cursor.moveToFirst();
        //find itemQnt value using the cursor and then update the db with a new lessQnt value
        itemQnt = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_ITEM_QNT));
        if (itemQnt > 0) {
            //as long as the quantity is equal or greater than 1, decrease quantity
            int lessQnt = itemQnt - 1;
            //and then store the new value for the item with this uri using content values
            cv.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QNT, lessQnt);
            getContentResolver().update(currentItemUri, cv, null, null);
        }
    }

    private void increaseQnt(Uri uri) {
        //setup scope for the cursor query and then get cursor
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_QNT};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        ContentValues cv = new ContentValues();
        if (!cursor.moveToFirst())
            cursor.moveToFirst();
        //find itemQnt value using the cursor and then update the db with a new moreQnt value
        itemQnt = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_ITEM_QNT));
        int moreQnt = itemQnt + 1;
        //and then store the new value for the item with this uri using content values
        cv.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QNT, moreQnt);
        getContentResolver().update(currentItemUri, cv, null, null);

    }
}


