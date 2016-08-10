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
import android.widget.BaseAdapter;
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

public class MyPicsCursorAdapter extends BaseAdapter {
    private static final String TAG = "MyPicsCursorAdapter";

    private Context context;
    private Cursor cursor;

    public MyPicsCursorAdapter(Context context, Cursor cursor) {
        Log.d(TAG, "MyPicsCursorAdapter called");
        this.context = context;
        this.cursor = cursor;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView called");

        ViewHolder holder = null;

        if (convertView == null || convertView.getId() == -1) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.browse_flat_stanley_list_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.flatStanleyImage);
            holder.caption = (TextView) convertView.findViewById(R.id.caption);
            holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (cursor.moveToPosition(position)) {
            Uri picUri = Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)));
            InputStream inputStream = loadBitmap(picUri);

            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                holder.imageView.setImageBitmap(bitmap);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(bitmap.getWidth()/2, bitmap.getHeight()/2);
                holder.imageView.setLayoutParams(layoutParams);

                String caption = cursor.getString(cursor.getColumnIndex(COLUMN_CAPTION));
                if (TextUtils.isEmpty(caption)) {
                    holder.caption.setVisibility(View.GONE);
                } else {
                    holder.caption.setText(caption);
                }

                holder.timestamp.setText("Created on: " + cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
        private TextView caption;
        private TextView timestamp;
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
