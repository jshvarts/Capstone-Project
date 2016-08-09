package com.jshvarts.flatstanley.data;

import android.net.Uri;
import android.provider.BaseColumns;

import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_CAPTION;
import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_PATH;
import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_TIMESTAMP;

/**
 * Constants used by the current provider and its clients.
 */
public final class MyPicsContract {
    public static final String PROVIDER_NAME = "com.jshvarts.flatstanley.data";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/mypics");

    // prevent others from instantiating by making the constructor private and empty
    private MyPicsContract() {}

    public static abstract class MyPicsEntry implements BaseColumns {
        public static final String TABLE_NAME = "my_pics";
        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_CAPTION = "caption";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static String[] getProjection() {
        return new String[] {BaseColumns._ID, COLUMN_PATH, COLUMN_CAPTION, COLUMN_TIMESTAMP};
    }
}
