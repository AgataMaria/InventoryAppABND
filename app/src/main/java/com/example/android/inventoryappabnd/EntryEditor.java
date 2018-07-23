package com.example.android.inventoryappabnd;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.android.inventoryappabnd.Data.DBHelperClass;
import com.example.android.inventoryappabnd.Data.InventoryContract;
import com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry;

public class EntryEditor extends AppCompatActivity {

    private ContentValues cv;
    private DBHelperClass myDbHelper;

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
        myDbHelper = new DBHelperClass(this);

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

    private void insertItem() {
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        cv = new ContentValues();

        // get input values from the editor form fields

        String itemName = mItemNameET.getText().toString().trim();
        String itemPrice = String.valueOf(Double.parseDouble(mItemPriceET.getText().toString().trim()));
        String itemQnt = String.valueOf(Integer.parseInt(mItemQntET.getText().toString().trim()));
        String itemSuppName = mItemSuppNameET.getText().toString().trim();
        String itemSuppNo = String.valueOf(Integer.parseInt(mItemSuppNoET.getText().toString().trim()));

        // assign input values to table columns
        cv.put(InventoryEntry.COLUMN_ITEM_TYPE, mItemTypeValue);
        cv.put(InventoryEntry.COLUMN_ITEM_NAME, itemName);
        cv.put(InventoryEntry.COLUMN_ITEM_PRICE, itemPrice);
        cv.put(InventoryEntry.COLUMN_ITEM_QNT, itemQnt);
        cv.put(InventoryEntry.COLUMN_ITEM_SUPP_NAME, itemSuppName);
        cv.put(InventoryEntry.COLUMN_ITEM_SUPP_NO, itemSuppNo);

        // insert assigned columns + their values to table
        long newRowID = db.insert(InventoryEntry.TABLE_NAME, null, cv);

        // check value of the editor input
        Log.v("Editor entry value", "new row" + newRowID);

        // display a toast message confirming successful entry
        Toast.makeText(this, "New item added to your inventory with ID: " + newRowID, Toast.LENGTH_LONG).show();
    }

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