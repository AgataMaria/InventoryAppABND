package com.example.android.inventoryappabnd;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryappabnd.Data.InventoryContract;
import com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry;

public class EntryEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int EDITOR_LOADER_ID = 1;
    private Uri currentItemUri;
    private String TABLE_COLUMNS_SEPARATOR = " | ";

    // Entry editor form fields elements

    private Spinner mItemTypeSpinner;
    private EditText mItemNameET;
    private EditText mItemPriceET;
    private EditText mItemQntET;
    private EditText mItemSuppNameET;
    private EditText mItemSuppNoET;

    // Item type variable
    private int mItemTypeValue = InventoryEntry.ITEM_TYPE_PC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_editor);

        //get intent from the InventoryActivity with the selected item uri to use in Edit Mode
        //store the uri in selectedItemUri variable
        Intent editorEditModeIntent = getIntent();
        Uri selectedItemUri = editorEditModeIntent.getData();

        //Change between Edit Mode and Add Mode depending on which intent started the activity
        //if the activity was started through the Add button the selectedItemUri will be null
        //if the activity was started from clicking on the item start the activity in Edit Mode
        if (selectedItemUri == null) {
            setTitle(getString(R.string.editor_activity_add_mode_label));
        } else {
            setTitle(getString(R.string.editor_activity_edit_mode_label));
            //initialise the Loader when the Activity is created in the Edit Mode
            getLoaderManager().initLoader(EDITOR_LOADER_ID, null, this);
        }

        // Assign editor forms elements to views by ID and set up hints for EditText fields
        mItemTypeSpinner = findViewById(R.id.entry1_spinner);
        mItemNameET = findViewById(R.id.entry2_et);
        mItemPriceET = findViewById(R.id.entry3_et);
        mItemPriceET.setHint(Html.fromHtml(
                "<small>" + getString(R.string.entry_prc_hint) + "</small>"));
        mItemQntET = findViewById(R.id.entry4_et);
        mItemSuppNameET = findViewById(R.id.entry5_et);
        mItemSuppNoET = findViewById(R.id.entry6_et);

        setupSpinner();
    }

    private void setupSpinner() {
        // Create adapter for spinner to use with the values from the array resource
        ArrayAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_item_type, android.R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Set adapter on the item type spinner
        mItemTypeSpinner.setAdapter(mSpinnerAdapter);

        // Assign table values to array values
        mItemTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.item_type_pc))) {
                        mItemTypeValue = InventoryEntry.ITEM_TYPE_PC;
                    } else if (selection.equals(getString(R.string.item_type_tablet))) {
                        mItemTypeValue = InventoryEntry.ITEM_TYPE_TABLET;
                    } else if (selection.equals(getString(R.string.item_type_monitor))) {
                        mItemTypeValue = InventoryEntry.ITEM_TYPE_MONITOR;
                    } else if (selection.equals(getString(R.string.item_type_printer))) {
                        mItemTypeValue = InventoryEntry.ITEM_TYPE_PRINTER;
                    } else if (selection.equals(getString(R.string.item_type_mobile))) {
                        mItemTypeValue = InventoryEntry.ITEM_TYPE_MOBILE;
                    } else {
                        mItemTypeValue = InventoryEntry.ITEM_TYPE_OTHER;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mItemTypeValue = InventoryContract.InventoryEntry.ITEM_TYPE_PC; // default value PC if no other option selected
            }
        });
    }

    //ADD MODE - insert method for the editor
    private void insertItem() {
        ContentValues cv = new ContentValues();

        // get input values from the editor form fields
        String itemName = mItemNameET.getText().toString().trim();
        String itemPrice = String.valueOf(Double.parseDouble(mItemPriceET.getText().toString().trim()));
        String itemQnt = String.valueOf(Integer.parseInt(mItemQntET.getText().toString().trim()));
        String itemSuppName = mItemSuppNameET.getText().toString().trim();
        String itemSuppNo = mItemSuppNoET.getText().toString().trim();

        // assign input values to table columns
        cv.put(InventoryEntry.COLUMN_ITEM_TYPE, mItemTypeValue);
        cv.put(InventoryEntry.COLUMN_ITEM_NAME, itemName);
        cv.put(InventoryEntry.COLUMN_ITEM_PRICE, itemPrice);
        cv.put(InventoryEntry.COLUMN_ITEM_QNT, itemQnt);
        cv.put(InventoryEntry.COLUMN_ITEM_SUPP_NAME, itemSuppName);
        cv.put(InventoryEntry.COLUMN_ITEM_SUPP_NO, itemSuppNo);

        // insert assigned columns + their values to table
        Uri newRowUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, cv);

        // check value of the editor input
        Log.v("Editor entry value", "new row" + newRowUri);

        // display a toast message confirming successful or unsuccessful entry
        if (newRowUri == null) {
            Toast.makeText(this, R.string.save_failed_toast, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.item_saved_toast) + newRowUri, Toast.LENGTH_LONG).show();
        }
    }

    //EDIT MODE - methods required by the Loader Manager
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

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
            case EDITOR_LOADER_ID:

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
        //move cursor to first position and get column index numbers
        if (loaderCursor.moveToFirst()) {
            int idColumnIndex = loaderCursor.getColumnIndex(InventoryEntry._ID);
            int itemTypeColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_TYPE);
            int itemNameColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int itemPriceColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
            int itemQntColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QNT);
            int itemSuppNameColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPP_NAME);
            int itemSuppNoColumnIndex = loaderCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPP_NO);

            //now get values from the cursor
            int id = loaderCursor.getInt(idColumnIndex);
            String itemType = String.valueOf(loaderCursor.getString(itemTypeColumnIndex));
            String itemName = loaderCursor.getString(itemNameColumnIndex);
            int itemPrice = loaderCursor.getInt(itemPriceColumnIndex);
            int itemQnt = loaderCursor.getInt(itemQntColumnIndex);
            String itemSuppName = loaderCursor.getString(itemSuppNameColumnIndex);
            String itemSuppNo = loaderCursor.getString(itemSuppNoColumnIndex);

            //check what's being returned
            Log.v("Editor Cursor", "\n" + id + TABLE_COLUMNS_SEPARATOR
                    + itemType + TABLE_COLUMNS_SEPARATOR
                    + itemName + TABLE_COLUMNS_SEPARATOR
                    + itemPrice + TABLE_COLUMNS_SEPARATOR
                    + itemQnt + TABLE_COLUMNS_SEPARATOR
                    + itemSuppName + TABLE_COLUMNS_SEPARATOR
                    + itemSuppNo + TABLE_COLUMNS_SEPARATOR);

            //update the item type spinner with the correct value
            int selection = loaderCursor.getPosition();
            switch (selection) {
                case InventoryEntry.ITEM_TYPE_TABLET:
                    mItemTypeSpinner.setSelection(1);
                    break;
                case InventoryEntry.ITEM_TYPE_MONITOR:
                    mItemTypeSpinner.setSelection(2);
                    break;
                case InventoryEntry.ITEM_TYPE_PRINTER:
                    mItemTypeSpinner.setSelection(3);
                    break;
                case InventoryEntry.ITEM_TYPE_MOBILE:
                    mItemTypeSpinner.setSelection(4);
                    break;
                case InventoryEntry.ITEM_TYPE_OTHER:
                    mItemTypeSpinner.setSelection(5);
                    default:
                //ITEM_TYPE_PC
                    mItemTypeSpinner.setSelection(0);
            }

            //update the textview fields in the list items layout with data returned
            mItemNameET.setText(itemName);
            mItemPriceET.setText(itemPrice);
            mItemQntET.setText(itemQnt);
            mItemSuppNameET.setText(itemSuppName);
            mItemSuppNoET.setText(itemSuppNo);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
loader.reset();

    }

    //MENU methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    // Set Editor menu options item selected behaviour
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.editor_menu_action_save:

                insertItem();
                finish();

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}