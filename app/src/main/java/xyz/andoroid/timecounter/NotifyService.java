package xyz.andoroid.timecounter;

import android.app.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;
import xyz.andoroid.timecounter.model.*;

import java.io.IOException;
import java.util.List;

public class NotifyService extends IntentService {
    private CounterIntentBackground ci;

    private LocalDateTime now;
    private SharedPreferences preferences;

    private boolean isRunning = false;

    public NotifyService() {
        super("NotifyService");

    }

    @Override
    public void onHandleIntent(Intent i) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel("0", "Permanent notification", NotificationManager.IMPORTANCE_MAX);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(1, buildForegroundNotification(1, "TimeCounter", "Running"));
        isRunning = true;
        ci = new CounterIntentBackground(this, this);
        now = LocalDateTime.now();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ci.startThreadForBackground();
        return START_STICKY;
    }

    public void stop() {
        isRunning = false;
        stopForeground(true);
    }

    private Notification buildForegroundNotification(int id, String title, String content) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("xyz.andoroid.timecounter.notifyId", id);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(getApplicationContext(), "0");
        } else {
            notification = new NotificationCompat.Builder(getApplicationContext());
        }
        notification
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setContentIntent(pIntent)
                .setOnlyAlertOnce(true);

        return(notification.build());
    }

    public boolean isRunning() {
        return isRunning;
    }
}
