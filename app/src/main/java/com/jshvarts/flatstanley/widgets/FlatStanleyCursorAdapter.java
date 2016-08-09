package com.jshvarts.flatstanley.widgets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.jshvarts.flatstanley.R;

public class FlatStanleyCursorAdapter extends CursorAdapter {
    public static final int COLUMN_CAPTION_NAME = 0;
    public static final int COLUMN_TIMESTAMP_NAME = 1;

    public FlatStanleyCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    private static class ViewHolder{
        public TextView caption;
        public TextView timestamp;

        public ViewHolder(View view) {
            caption = (TextView) view.findViewById(R.id.caption);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.flat_stanley_appwidget_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        return mItem;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        mHolder.caption.setText(cursor.getString(COLUMN_CAPTION_NAME));
        mHolder.timestamp.setText(cursor.getString(COLUMN_TIMESTAMP_NAME));
    }
}
