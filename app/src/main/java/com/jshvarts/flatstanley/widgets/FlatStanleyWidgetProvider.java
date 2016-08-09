package com.jshvarts.flatstanley.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.jshvarts.flatstanley.R;

public class FlatStanleyWidgetProvider extends AppWidgetProvider {
    private static String TAG = FlatStanleyWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "performing widget onUpdate()");

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.flat_stanley_appwidget);

            Intent intent = new Intent(context, FlatStanleyAppWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            views.setRemoteAdapter(appWidgetId, R.id.flat_stanley_list, intent);

            views.setEmptyView(R.id.flat_stanley_list, R.id.flat_stanley_empty_view);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
