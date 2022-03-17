package com.example.ibeacondemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;
import timber.log.Timber;

//import android.support.annotation.Nullable;

public class MainService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBus.getDefault().post(MessageWrap.getInstance(true));

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int anhour = 1000 * 5; //由于google对电量做了优化，5S是最小轮询单位5S以内不管怎么设置时间，都是5S执行一次,凡是>5S的，都是可以在5S以上时间执行的
        long triggerAtMillis = SystemClock.elapsedRealtime() + anhour;

        Intent alarmIntent = new Intent(this, MainService.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//  4.4
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Timber.i("MainService--onDestroy");
        super.onDestroy();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, MainService.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}