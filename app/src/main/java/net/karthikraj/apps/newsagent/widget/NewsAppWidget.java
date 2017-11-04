package net.karthikraj.apps.newsagent.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.feeds.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class NewsAppWidget extends AppWidgetProvider {

    public static final String EXTRA_WIDGET_SELECTION_ARTICLE_ID = "extra_widget_selection_article_id";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews  remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        // set intent for widget service that will create the views
        Intent serviceIntent = new Intent(context, NewsAppWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))); // embed extras so they don't get ignored
        remoteViews.setRemoteAdapter(appWidgetId, R.id.stackWidgetView, serviceIntent);
        remoteViews.setEmptyView(R.id.stackWidgetView, R.id.stackWidgetEmptyView);

        // set intent for item click (opens main activity)
        Intent viewIntent = new Intent(context, MainActivity.class);
        //viewIntent.setAction(HoneybuzzListActivity.ACTION_VIEW);
        viewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        viewIntent.setData(Uri.parse(viewIntent.toUri(Intent.URI_INTENT_SCHEME)));

        PendingIntent viewPendingIntent = PendingIntent.getActivity(context, 0, viewIntent, 0);
        remoteViews.setPendingIntentTemplate(R.id.stackWidgetView, viewPendingIntent);

        // update widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

