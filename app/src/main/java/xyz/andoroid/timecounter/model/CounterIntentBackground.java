package xyz.andoroid.timecounter.model;

import android.content.Context;
import android.content.SharedPreferences;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;
import xyz.andoroid.timecounter.NotifyService;
import xyz.andoroid.timecounter.R;

public class CounterIntentBackground extends CounterIntent{
    private NotifyService notifyService;

    private NotificationUtils notificationUtils;
    protected MusicUtils musicUtils;

    private SharedPreferences sharedPreferences;

    protected String ringtone;

    public CounterIntentBackground(Context context, NotifyService notifyService) {
        super(context);
        this.notifyService = notifyService;
        notificationUtils = new NotificationUtils(context);
        updateMusicPlayer();
        enableNotification = preferences.getBoolean("notify",true);
        ringtone = preferences.getString("ringtone", "ring");
        sharedPreferences = context.getSharedPreferences("bg", 0);
        sharedPreferences.edit().putBoolean("running", true).apply();
    }

    public void startThreadForBackground() {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        if(!classCode.equalsIgnoreCase(preferences.getString("class", "0"))) updateClassCode();
                        if(!ringtone.equalsIgnoreCase(preferences.getString("ringtone", "ring"))) updateMusicPlayer();
                        if(enableNotification != preferences.getBoolean("notify",true)) {
                            enableNotification = !enableNotification;
                            if(!enableNotification && notifyService.isRunning()) {
                                sharedPreferences.edit().putBoolean("running", false).apply();
                                notifyService.stop();
                                interrupt();
                            }
                        }
                        if(evenWeek != preferences.getBoolean("evenWeek", false)) updateClassCode();
                        if(onlineLectures != preferences.getBoolean("onlineLectures", false)) updateClassCode();
                        startOfWeek = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0,0);
                        startOfWeek = startOfWeek.minusDays(now.getDayOfWeek().compareTo(DayOfWeek.MONDAY));
                        long secsBetweenNowAndStart = ChronoUnit.SECONDS.between(startOfWeek, now);
                        int index = -1;
                        for(int i = 0;i<events.size();i++) {
                            if(secsBetweenNowAndStart > events.get(i).second) continue;
                            index = i;
                            break;
                        }
                        if(index != -1) {
                            long s = events.get(index).second-secsBetweenNowAndStart;
                            if(lastIndex == -1) lastIndex = index;
                            if(index != lastIndex) {
                                if(preferences.getBoolean("ring",true) && events.get(index).third == 1 && !classCodeJustUpdated)
                                    musicUtils.play(preferences.getInt("ringtoneDuration",4)*1000);
                                if(classCodeJustUpdated) classCodeJustUpdated = false;
                                lastIndex = index;
                            }
                            if(enableNotification) {
                                if(notifyService.isRunning()) notificationUtils.showNotification(1, events.get(index).first, TimeUtils.convertFromSeconds(s,true));
                            } else if(notifyService.isRunning()) {
                                notifyService.stop();
                            }
                        } else {
                            long s = 7 * 24 * 60 * 60 - secsBetweenNowAndStart;
                            notificationUtils.showNotification(1, context.getString(R.string.no_further_events), TimeUtils.convertFromSeconds(s, true));
                        }
                        now = LocalDateTime.now();
                    }
                } catch (InterruptedException ignored) {}
            }
        };
        thread.start();
    }

    private void updateMusicPlayer() {
        ringtone = preferences.getString("ringtone", "ring");
        switch (ringtone) {
            case "ukrhymn":
                musicUtils = new MusicUtils(context, R.raw.ukrhymn);
                break;
            case "upmlhymn":
                musicUtils = new MusicUtils(context, R.raw.upmlhymn);
                break;
            case "monkeys":
                musicUtils = new MusicUtils(context, R.raw.monkey);
                break;
            default:
                musicUtils = new MusicUtils(context, R.raw.ring);
                break;
        }
    }
}
