package xyz.andoroid.timecounter.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import xyz.andoroid.timecounter.MainActivity;
import xyz.andoroid.timecounter.R;

public class NotificationUtils {
    private Context context;
    private NotificationManager notificationManager;
    private boolean setOngoing = true;

    public NotificationUtils(NotificationManager nm, Context context) {
        this.notificationManager = nm;
        this.context = context;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("0", "Permanent notification", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification(int id, String title, String content) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("xyz.andoroid.timecounter.notifyId", id);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(context, "0");
        } else {
            notification = new NotificationCompat.Builder(context);
        }
        notification
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(setOngoing)
                .setContentIntent(pIntent)
                .setOnlyAlertOnce(true);
        notificationManager.notify(id, notification.build());
    }

    public void cancelNotification(int id) {
        notificationManager.cancel(id);
    }

    public boolean isNotificationShowed() {
        return notificationManager.getActiveNotifications().length != 0;
    }

    public void onStop() {
    }
}
