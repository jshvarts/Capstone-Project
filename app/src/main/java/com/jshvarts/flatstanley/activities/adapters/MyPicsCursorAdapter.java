package com.jshvarts.flatstanley.activities.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.model.FlatStanley;

import java.io.IOException;
import java.io.InputStream;

import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_PATH;
import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_CAPTION;
import static com.jshvarts.flatstanley.data.MyPicsContract.MyPicsEntry.COLUMN_TIMESTAMP;

public class MyPicsCursorAdapter extends android.support.v4.widget.CursorAdapter {
    private static final String TAG = "MyPicsCursorAdapter";

    private Context context;
    private Cursor cursor;

    public MyPicsCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public void bindView(View row, Context context, Cursor cursor) {
        Log.d(TAG, "bindView called");

        ImageView imageView = (ImageView) row.findViewById(R.id.flatStanleyImage);

        Uri picUri = Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)));
        InputStream inputStream = loadBitmap(picUri);

        if (inputStream != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            imageView.setLayoutParams(layoutParams);
        }

        TextView captionTextView = (TextView) row.findViewById(R.id.caption);
        String caption = cursor.getString(cursor.getColumnIndex(COLUMN_CAPTION));
        if (TextUtils.isEmpty(caption)) {
            captionTextView.setVisibility(View.GONE);
        } else {
            captionTextView.setText(caption);
        }

        TextView timestampTextView = (TextView) row.findViewById(R.id.timestamp);
        timestampTextView.setText("Created on: " + cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.browse_flat_stanley_list_item, null);
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        Log.d(TAG, "getItem");
        FlatStanley flatStanley = null;
        if(cursor.moveToPosition(position)) {
            flatStanley = new FlatStanley(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CAPTION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
            flatStanley.setId(String.valueOf(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))));
        }
        return flatStanley;
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "getItemId called");
        if (cursor.moveToPosition(position)) {
            return cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        }
        return -1;
    }

    private InputStream loadBitmap(Uri photoUri) {
        InputStream is;
        try {
            is = context.getContentResolver().openInputStream(photoUri);
        } catch (IOException e) {
            Log.e(TAG, "Unable to open photo for uri " + photoUri);
            return null;
        }
        return is;
    }
}
