package com.jshvarts.flatstanley.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jshvarts.flatstanley.Constants;
import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.model.FlatStanley;

import java.util.ArrayList;
import java.util.List;

public class FlatStanleyAppWidgetService extends RemoteViewsService {
    private static final String TAG = FlatStanleyAppWidgetService.class.getSimpleName();

    private Context serviceContext;

    private int appWidgetId;

    private List<FlatStanley> flatStanleys = new ArrayList();;

    private Firebase firebase;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FlatStanleyAppWidgetRemoteViewsFactory(getApplicationContext(), intent);
    }

    public class FlatStanleyAppWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        public FlatStanleyAppWidgetRemoteViewsFactory(Context context, Intent intent) {
            serviceContext = context;

            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);        }

        @Override
        public void onCreate() {
            firebase = new Firebase(Constants.getEntrytUri());
            // TODO change sort order to display latest first and add a limit
            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    flatStanleys = new ArrayList();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        FlatStanley flatStanley = dataSnapshot.getValue(FlatStanley.class);
                        flatStanley.setId(dataSnapshot.getKey());
                        flatStanleys.add(flatStanley);
                    }

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(serviceContext);
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(TAG, "The read failed: " + firebaseError.getMessage());
                }
            });
        }

        @Override
        public void onDataSetChanged() {
            Log.d(TAG, "onDataSetChanged called");
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "onDestroy called");
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount called. " + flatStanleys.size());
            return flatStanleys.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(serviceContext.getPackageName(), R.layout.widget_list_item);
            FlatStanley flatStanley = flatStanleys.get(position);
            remoteViews.setTextViewText(R.id.caption, flatStanley.getCaption());
            remoteViews.setTextViewText(R.id.timestamp, flatStanley.getTimestamp());

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
