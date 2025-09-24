package jymk.stream.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;

import jymk.stream.R;
import jymk.stream.activity.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    private static final String TAG = "WeatherWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, String.format("appWidgetIds=%s", Arrays.toString(appWidgetIds)));
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        appWidgetManager.updateAppWidget(appWidgetManager.getAppWidgetIds(new ComponentName(context, WeatherWidget.class)), views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//        Log.d(TAG, "receive intent");

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        updateWidget(intent, views);

        // 闹钟
        Intent alarmIntent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        alarmIntent.setPackage("com.coloros.alarmclock");
        PendingIntent piAlarm = PendingIntent.getActivity(context, 123498797, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.appwidget_time, piAlarm);

        // 日历
        Intent calendarIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.calendar/time"));
        PendingIntent piCalendar = PendingIntent.getActivity(context, 123498798, calendarIntent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.appwidget_date, piCalendar);

        // 打开自身
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent piApp = PendingIntent.getActivity(context, 123498799, appIntent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.appwidget_rightside, piApp);

        // 更新ui
        manager.updateAppWidget(manager.getAppWidgetIds(new ComponentName(context, WeatherWidget.class)), views);
    }

    void updateWidget(@NonNull Intent intent, RemoteViews views) {
        String weather = intent.getStringExtra("weather");
        String temp = intent.getStringExtra("temp");
        String region = intent.getStringExtra("region");
        boolean updateWeather = intent.getBooleanExtra("update_weather", false);
        boolean updateTime = intent.getBooleanExtra("update_time", false);

        Log.d(TAG, String.format("action=%s, updateWeather=%b, updateTime=%b, weather=%s, temp=%s, region=%s", intent.getAction(), updateWeather, updateTime, weather, temp, region));

        if (updateTime) {
            LocalDateTime now = LocalDateTime.now();
            views.setTextViewText(R.id.appwidget_time, String.format(Locale.CHINA, "%02d:%02d", now.getHour(), now.getMinute()));
            views.setTextViewText(R.id.appwidget_date, String.format(Locale.CHINA, "%04d-%02d-%02d", now.getYear(), now.getMonth().ordinal() + 1, now.getDayOfMonth()));
        }

        if (updateWeather) {
            views.setTextViewText(R.id.appwidget_weather, weather);
            views.setTextViewText(R.id.appwidget_temp, temp + " ℃");
            views.setTextViewText(R.id.appwidget_region, region);
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