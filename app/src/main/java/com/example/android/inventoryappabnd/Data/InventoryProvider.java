package com.example.android.inventoryappabnd.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryappabnd.Data.InventoryContract;

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
                db.query(InventoryContract.InventoryEntry.TABLE_NAME,  projection, selection, selectionArgs, null, null, null);
                break;
            case INVENTORY_ROW_ID:
                selection = InventoryContract.InventoryEntry._ID;
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                db.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot resolve this URI query" + uri);
        }
        return cursor;
    }

    //update
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    //delete
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    //getType
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    // Helper method for the insert() method
    private Uri insertItem(Uri uri, ContentValues values){
        SQLiteDatabase db = myDbHelper.getWritableDatabase();
long id = db.insert(TABLE_NAME, null, values);
        if (id == -1) {
            Log.e("ContentProvider", "Failed to insert row for " + uri);
            return null;}
        return ContentUris.withAppendedId(uri, id);
    }
}
