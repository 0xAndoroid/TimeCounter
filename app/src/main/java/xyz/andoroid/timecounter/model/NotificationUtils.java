package xyz.andoroid.timecounter.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import xyz.andoroid.timecounter.R;

public class NotificationUtils {
    private Context context;
    private NotificationManager notificationManager;
    private NotificationChannel channel;

    public NotificationUtils(NotificationManager nm, Context context) {
        this.notificationManager = nm;
        this.context = context;
        channel = new NotificationChannel("0", "Permanent notification", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
    }

    public void showNotification(int id, String title, String content) {
        Notification notification = new Notification.Builder(context, "0")
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .build();
        notificationManager.notify(id, notification);
    }

    public void onStop() {
        notificationManager.cancel(0);
    }
}
