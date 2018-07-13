package com.example.android.inventoryappabnd;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.inventoryappabnd.Data.InventoryContract;
import com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry;

public class EntryEditor extends AppCompatActivity {

    // Entry editor form fields elements
    private Spinner mItemTypeSpinner;
    private EditText mItemTagNET;
    private EditText mItemSNET;
    private EditText mItemUserET;

    // Item type variable
    private int mItemTypeValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_editor);

        // Assign editor forms elements to views by ID and set up hints for EditText fields
        mItemTypeSpinner = (Spinner) findViewById(R.id.entry1_spinner);
        mItemTagNET = (EditText) findViewById(R.id.entry2_et);
        mItemTagNET.setHint(Html.fromHtml(
                   "<small>" + getString(R.string.entry_tagn_hint) + "</small>" ));
        mItemSNET = (EditText) findViewById(R.id.entry3_et);
        mItemUserET = (EditText) findViewById(R.id.entry4_et);
        mItemUserET.setHint(Html.fromHtml(
                    "<small>" + getString(R.string.entry_user_hint) + "</small>" ));

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

                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.editor_menu_action_delete:

                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}