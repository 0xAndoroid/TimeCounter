package xyz.andoroid.timecounter.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import xyz.andoroid.timecounter.MainActivity;
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
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("xyz.andoroid.timecounter.notifyId", id);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context, "0")
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                //.addAction(0, "Action Button", pIntent)
                .setContentIntent(pIntent)
                .build();
        notificationManager.notify(id, notification);
    }

    public void onStop() {
        notificationManager.cancel(0);
    }
}
