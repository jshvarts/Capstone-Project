package com.jshvarts.flatstanley.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.data.MyPicsContract;

public class FlatStanleyAppWidgetService extends RemoteViewsService {
    private static final String TAG = FlatStanleyAppWidgetService.class.getSimpleName();

    private Context serviceContext;
    private ContentResolver contentResolver;
    private int appWidgetId;
    private Cursor cursor = null;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FlatStanleyAppWidgetRemoteViewsFactory(getApplicationContext(), intent);
    }

    public class FlatStanleyAppWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        public FlatStanleyAppWidgetRemoteViewsFactory(Context context, Intent intent) {
            serviceContext = context;
            contentResolver = serviceContext.getContentResolver();
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (cursor != null) {
                cursor.close();
                Log.d(TAG, "cursor is not null in dataset changed");
            }

            Binder.clearCallingIdentity();

            cursor = contentResolver.query(
                    MyPicsContract.CONTENT_URI,
                    new String[]{MyPicsContract.MyPicsEntry.COLUMN_CAPTION, MyPicsContract.MyPicsEntry.COLUMN_TIMESTAMP},
                    null,
                    null,
                    null);

            Log.d(TAG, "cursor is: " + cursor.toString());
            Log.d(TAG, "cursor count is " + cursor.getCount());
        }

        @Override
        public void onDestroy() {
            if (cursor != null) {
                cursor.close();
            }
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount of cursor is : " + cursor.getCount());
            return cursor == null ? 0 : cursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(serviceContext.getPackageName(), R.layout.widget_list_item);
            assert cursor != null;

            if (cursor.moveToPosition(position)) {
                remoteViews.setTextViewText(R.id.caption, cursor.getString(FlatStanleyCursorAdapter.COLUMN_CAPTION_NAME));
                remoteViews.setTextViewText(R.id.timestamp, cursor.getString(FlatStanleyCursorAdapter.COLUMN_TIMESTAMP_NAME));
            }
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
