package com.example.android.inventoryappabnd.Data;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappabnd.EntryEditor;
import com.example.android.inventoryappabnd.InventoryActivity;
import com.example.android.inventoryappabnd.ItemDetailActivity;
import com.example.android.inventoryappabnd.R;

import static android.support.v4.content.ContextCompat.startActivity;

public class InventoryCursorAdapter extends CursorAdapter {

    //Constructor
    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    //Method required by the CursorAdapter - dont need to check if view is null as the class
    //takes care of it - newView inflates view from the chosen layout source
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    //Method required by the CursorAdapter - set elements of the view
    //find elements, read data from the db and assgn it to tbe view elements
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView itemNameTV = view.findViewById(R.id.item_name_tv);
        TextView itemQntTV = view.findViewById(R.id.item_qnt_tv);
        TextView itemPriceTV = view.findViewById(R.id.item_price_tv);
        TextView itemSuppTV = view.findViewById(R.id.item_supp_tv);
        TextView saleButtonTV = view.findViewById(R.id.sale_button);
        TextView detailsButtonLinkTV = view.findViewById(R.id.product_details_link);

        String itemName = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME));
        final int itemQnt = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_ITEM_QNT));
        Double itemPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE));
        String itemSupp = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPP_NAME));

        //use the cursor to get item's id and store in
        final String id = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));
        saleButtonTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get this item's uri using the item's id returned from the cursor
                Uri currentItemUri = Uri.withAppendedPath(InventoryContract.InventoryEntry.CONTENT_URI, id);
                ContentValues cv = new ContentValues();
                if (itemQnt >= 1) {
                    //as long as the quantity is equal or greater than 1, decrease quantity
                    int lessQnt = itemQnt - 1;
                    //and then store the new value for the item with this uri using content values
                    cv.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QNT, lessQnt);
                    context.getContentResolver().update(currentItemUri, cv, null, null);
                }
                // Display toast to confirm that the quantity has decreased
                Toast.makeText(context, R.string.qnt_updated_toast, Toast.LENGTH_SHORT).show();
            }
        });

        itemNameTV.setText(itemName);
        itemQntTV.setText(String.valueOf(itemQnt));
        itemPriceTV.setText(context.getString(R.string.currency_symbol) + String.valueOf(itemPrice));
        itemSuppTV.setText(itemSupp);
        detailsButtonLinkTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the selected item content uri by appending base content uri with the item id from the adapter
                Uri selectedItemUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                Intent detailViewIntent = new Intent(context, ItemDetailActivity.class);
                //check the value of the uri
                Log.v("item uri: ", String.valueOf(selectedItemUri));
                //pass the uri to the ItemDetailActivity activity and start activity
                detailViewIntent.setData(selectedItemUri);
                startActivity(detailViewIntent);
            }
        });
    }
}
