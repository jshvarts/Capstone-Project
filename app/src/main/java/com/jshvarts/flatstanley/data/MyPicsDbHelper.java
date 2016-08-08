package com.jshvarts.flatstanley.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.text.TextUtils;

import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.TABLE_NAME;
import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_PATH;
import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_CAPTION;
import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_TIMESTAMP;

/**
 * Database helper to store and retrieve pics created by user.
 */
public class MyPicsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="pics.db";
    private static final int SCHEMA=1;

    private static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME +
            " ("+BaseColumns._ID+ "  integer primary key," + COLUMN_PATH +" TEXT, "+ COLUMN_CAPTION +" TEXT, "+ COLUMN_TIMESTAMP +" TEXT)";

    private static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String DEFAULT_SORT_ORDER = COLUMN_TIMESTAMP;

    private static final String SQL_FIND_TABLE = "SELECT name FROM sqlite_master WHERE type='table' " +
            "AND name='"+TABLE_NAME+"'";


    public MyPicsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Cursor c = db.rawQuery(SQL_FIND_TABLE, null);

        if (c.getCount() == 0) {
            db.execSQL(SQL_CREATE);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        onCreate(db);
    }

    public Cursor getPics(String id, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(TABLE_NAME);

        if (id != null) {
            sqliteQueryBuilder.appendWhere(BaseColumns._ID + "=" + id);
        }

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = DEFAULT_SORT_ORDER;
        }

        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        return cursor;
    }

    public long addPic(ContentValues values) throws SQLException {
        long id = getWritableDatabase().insert(TABLE_NAME, "", values);
        if(id <= 0 ) {
            throw new SQLException("Failed to add a pic");
        }

        return id;
    }

    public int deletePic(String id) {
        if(id == null) {
            return getWritableDatabase().delete(TABLE_NAME, null , null);
        } else {
            return getWritableDatabase().delete(TABLE_NAME, BaseColumns._ID +"=?", new String[]{id});
        }
    }

    public int updatePic(String id, ContentValues values) {
        if(id == null) {
            return getWritableDatabase().update(TABLE_NAME, values, null, null);
        } else {
            return getWritableDatabase().update(TABLE_NAME, values, BaseColumns._ID +"=?", new String[]{id});
        }
    }
}
