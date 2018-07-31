package com.example.android.inventoryappabnd.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry;

import static com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry.CONTENT_AUTHORITY;
import static com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry.PATH_INVENTORY_TABLE;
import static com.example.android.inventoryappabnd.Data.InventoryContract.InventoryEntry.TABLE_NAME;

public class InventoryProvider extends ContentProvider {

    //SQLite DataBase Helper object
    private DBHelperClass myDbHelper;

    // Code pattern for the Uri Matcher
    private static final int INVENTORY = 201;
    private static final int INVENTORY_ROW_ID = 202;

    // Uri Matcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INVENTORY_TABLE, INVENTORY);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INVENTORY_TABLE + "/#", INVENTORY_ROW_ID);
    }

    //methods required by the ContentProvider
    //onCreate
    @Override
    public boolean onCreate() {
        myDbHelper = new DBHelperClass(getContext());
        return true;
    }

    //create
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("The insertion was not valid for " + uri);
        }
    }

    //read
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case INVENTORY_ROW_ID:
                selection = InventoryEntry._ID;
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot resolve this URI query" + uri);
        }
        return cursor;
    }

    //update
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateItem(uri, values, selection, selectionArgs);
            case INVENTORY_ROW_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Could not update the values for " + uri);
        }
    }


    //delete
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
            case INVENTORY_ROW_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("You cannot delete: ");
        }
    }

    //getType
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryEntry.CONTENT_DIR_TYPE;
            case INVENTORY_ROW_ID:
                return InventoryEntry.CONTENT_ITEM_MIME_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    // Helper method for the insert() method
    private Uri insertItem(Uri uri, ContentValues values) {
        //check content values entered by the user before accessing the database
        String itemName = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
        if (itemName == null || itemName.trim().length() == 0) {
            throw new IllegalArgumentException("Cannot save an item without a name");
        }
        Double itemPrice = values.getAsDouble(InventoryEntry.COLUMN_ITEM_PRICE);
        if (itemPrice == null) {
            throw new IllegalArgumentException("Cannot save an item without a price");
        }
        Integer itemQnt = values.getAsInteger(InventoryEntry.COLUMN_ITEM_QNT);
        if (itemQnt == null) {
            throw new IllegalArgumentException("Must specify item quantity");
        }

        // get a database instance
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        // insert an item and store the returned row ID in 'id' variable
        long id = db.insert(TABLE_NAME, null, values);
        if (id == -1) {
            Log.e("ContentProvider", "Failed to insert row for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    // helper method for the update() method
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //check content values entered by the user before accessing the database
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_NAME)) {
            String itemName = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if (itemName == null || itemName.trim().length() == 0) {
                throw new IllegalArgumentException("Cannot save an item without a name");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_PRICE)) {
            Double itemPrice = values.getAsDouble(InventoryEntry.COLUMN_ITEM_PRICE);
            if (itemPrice == null) {
                throw new IllegalArgumentException("Cannot save an item without a price");
            }
        }

        if (values.containsKey(InventoryEntry.COLUMN_ITEM_QNT)) {
            Integer itemQnt = values.getAsInteger(InventoryEntry.COLUMN_ITEM_QNT);
            if (itemQnt == null) {
                throw new IllegalArgumentException("Must specify item quantity");
            }
        }

        // get a database instance
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        // find and update an item and store the returned row ID in 'id' variable
        int updatedRowId = db.update(TABLE_NAME, values, selection, selectionArgs);

        if (updatedRowId != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedRowId;
    }
}

