package jymk.stream.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // nothing
        Intent intentTime = new Intent();
        intentTime.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        intentTime.putExtra("update_weather", false);
        intentTime.putExtra("update_time", true);
        context.sendBroadcast(intentTime);
    }
}
