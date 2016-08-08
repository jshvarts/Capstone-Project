package com.jshvarts.flatstanley.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.jshvarts.flatstanley.data.MyPicsContract.PROVIDER_NAME;
import static com.jshvarts.flatstanley.data.MyPicsContract.CONTENT_URI;

public class MyContentProvider extends ContentProvider {

    private static final int MY_PICS = 1;
    private static final int MY_PIC_ID = 2;
    private static final UriMatcher uriMatcher = getUriMatcher();

    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "mypics", MY_PICS);
        uriMatcher.addURI(PROVIDER_NAME, "mypicks/#", MY_PIC_ID);
        return uriMatcher;
    }

    private final String LOG_TAG = getClass().getSimpleName();

    MyPicsDbHelper db;

    @Override
    public boolean onCreate() {
        db=new MyPicsDbHelper(getContext());
        return (db == null) ? false : true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String id = null;

        if (uriMatcher.match(uri) == MY_PIC_ID) {
            //this query is for a single pic. Get the id from the URI.
            id = uri.getPathSegments().get(1);
        }

        return db.getPics(id, projection, selection, selectionArgs, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MY_PICS:
                return "vnd.android.cursor.dir/vnd.com.jshvarts.flatstanley.provider.mypics";
            case MY_PIC_ID:
                return "vnd.android.cursor.item/vnd.com.jshvarts.flatstanley.provider.mypics";
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        try {
            id = db.addPic(values);
        } catch(SQLException e) {
            Log.e(LOG_TAG, "error inserting my pic. " + e.getMessage());
            return null;
        }
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String id = null;
        if(uriMatcher.match(uri) == MY_PIC_ID) {
            //delete is for a single pic. Get the id from the URI.
            id = uri.getPathSegments().get(1);
        }

        int count = db.deletePic(id);
        if (count == 0) {
            Log.e(LOG_TAG, "Failed to delete a pic " + uri);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String id = null;
        if(uriMatcher.match(uri) == MY_PIC_ID) {
            //update is for a single pic. Get the id from the URI.
            id = uri.getPathSegments().get(1);
        }

        int count = db.updatePic(id, values);
        if (count == 0) {
            Log.e(LOG_TAG, "Failed to update a pic " + uri);
        }
        return count;
    }
}
