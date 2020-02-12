package com.lit.litmoments.Widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.lit.litmoments.DispJournal.DisplayJournal;
import com.lit.litmoments.Main.MainActivity;
import com.lit.litmoments.R;

public class CollectionWidget extends AppWidgetProvider {

    public static final String ACTION_DATA_UPDATED = "data_updated";




    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION_DATA_UPDATED)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.list_widget);

            // WidgetListService intent for populating collection views.
            Intent collectionIntent = new Intent(context, WidgetService.class);
            rv.setRemoteAdapter(R.id.widget_list, collectionIntent);
            rv.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Intent for opening the main app from the widget title.
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, 0);
            rv.setOnClickPendingIntent(R.id.widget_title, pendingIntent);





            appWidgetManager.updateAppWidget(appWidgetId, rv);



        }

    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.list_widget);
            rv.removeAllViews(appWidgetId);
        }
    }
}
