package com.example.android.inventoryappabnd.Data;

import android.provider.BaseColumns;

public final class InventoryContract {
    public static abstract class InventoryEntry implements BaseColumns {

// Tabble name and column names

        public static final String TABLE_NAME = "inventory";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_TYPE = "type";
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_QNT = "quantity";
        public static final String COLUMN_ITEM_SUPP_NAME = "supp_name";
        public static final String COLUMN_ITEM_SUPP_NO = "supp_no";

//  Possible  values for item type

        public static final int ITEM_TYPE_PC = 0;
        public static final int ITEM_TYPE_TABLET = 1;
        public static final int ITEM_TYPE_MONITOR = 2;
        public static final int ITEM_TYPE_PRINTER = 3;
        public static final int ITEM_TYPE_MOBILE = 4;
        public static final int ITEM_TYPE_OTHER = 5;

    }
}
