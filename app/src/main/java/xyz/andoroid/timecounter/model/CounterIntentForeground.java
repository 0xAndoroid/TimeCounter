package xyz.andoroid.timecounter.model;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;
import xyz.andoroid.timecounter.MainActivity;
import xyz.andoroid.timecounter.NotifyService;
import xyz.andoroid.timecounter.R;

public class CounterIntentForeground extends CounterIntent {
    private MainActivity mainActivity;

    private String font;
    private int fontSize;

    public CounterIntentForeground(Context context, MainActivity activity) {
        super(context);
        mainActivity = activity;
        font = "";
        fontSize = 50;
        enableNotification = preferences.getBoolean("notify",true);
    }

    public void startThreadForView() {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        mainActivity.runOnUiThread(() -> {
                            if (!classCode.equalsIgnoreCase(preferences.getString("class", "0"))) updateClassCode();
                            if (!font.equalsIgnoreCase(preferences.getString("font", "tnm")) || fontSize != preferences.getInt("fontSize", 50)) updateFont();
                            if (preferences.getBoolean("notify", true) && !enableNotification) {
                                enableNotification = true;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    context.startForegroundService(new Intent(context, NotifyService.class));
                                } else {
                                    context.startService(new Intent(context, NotifyService.class));
                                }
                            }
                            if (weekEnded) {
                                startOfWeek = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
                                startOfWeek = startOfWeek.minusDays(now.getDayOfWeek().compareTo(DayOfWeek.MONDAY));
                                weekEnded = false;
                            }
                            long secsBetweenNowAndStart = ChronoUnit.SECONDS.between(startOfWeek, now);
                            int index = -1;
                            for (int i = 0; i < events.size(); i++) {
                                if (secsBetweenNowAndStart > events.get(i).second) continue;
                                index = i;
                                break;
                            }
                            if (index != -1) {
                                long s = events.get(index).second - secsBetweenNowAndStart;
                                if (lastIndex == -1) lastIndex = index;
                                if (index != lastIndex) {
                                    lastIndex = index;
                                }

                                StringBuilder next = new StringBuilder();
                                int t = preferences.getInt("showUpcomingEvents", 10) + 1;
                                for (int i = index + 1; i < index + t && i < events.size(); i++) {
                                    if (!preferences.getBoolean("showBreaks", true) && ((events.get(i).first.equalsIgnoreCase("break") || events.get(i).first.equalsIgnoreCase("перерва"))
                                            || events.get(i).first.startsWith("Break") || events.get(i).first.contains("Кінець") || events.get(i).first.contains("Перерва"))) {
                                        t++;
                                        continue;
                                    }
                                    next.append(events.get(i).first).append(" ").append(TimeUtils.convertFromSeconds(events.get(i).second - secsBetweenNowAndStart, false)).append("\n");
                                }
                                mainActivity.alterViews(events.get(index).first, TimeUtils.convertFromSeconds(s, true), next.toString());
                            } else {
                                long s = 7 * 24 * 60 * 60 - secsBetweenNowAndStart;
                                mainActivity.alterViews(context.getString(R.string.no_further_events), TimeUtils.convertFromSeconds(s, true), "");
                                weekEnded = true;
                            }
                            now = LocalDateTime.now();
                        });
                    }
                } catch (InterruptedException ignored) {}
            }
        };
        thread.start();
    }

    public void updateFont() {
        font = preferences.getString("font","tnm");
        fontSize = preferences.getInt("fontSize", 50);
        mainActivity.updateFont(font, fontSize);
    }

}
