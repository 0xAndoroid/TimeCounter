package xyz.andoroid.timecounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import xyz.andoroid.timecounter.model.NotificationUtils;
import xyz.andoroid.timecounter.model.ReaderUtils;
import xyz.andoroid.timecounter.model.TimeUtils;

public class MainActivity extends AppCompatActivity {
    private LocalDateTime now;

    private List<Pair<String, Long>> events;

    private LocalDateTime startOfWeek;
    private Vibrator v;
    private NotificationUtils notificationUtils;

    private boolean played = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        now = LocalDateTime.now();
        events = new ArrayList<>();
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        notificationUtils = new NotificationUtils((NotificationManager)getSystemService(NOTIFICATION_SERVICE), this);

        startOfWeek = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0,0);
        startOfWeek = startOfWeek.minusDays(now.getDayOfWeek().compareTo(DayOfWeek.MONDAY));


        final MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.ring);
        mPlayer.setLooping(true);

        final TextView eventName = findViewById(R.id.EventName);
        final TextView eventTime = findViewById(R.id.EventTime);
        final TextView nextEvent = findViewById(R.id.NextEvent);
        ConstraintLayout constraintLayout = findViewById(R.id.wdwadwa);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor((int)(Math.random()*0xFF000000));
            }
        });

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.pause();
                button.setVisibility(View.INVISIBLE);
            }
        });

        ReaderUtils qb = new ReaderUtils(this);
        List<String> allTextLines = qb.readLine("schedule.csv");
        String[] ss;Pair<String, Long> pr;
        for(String s : allTextLines) {
            ss = s.split(",");
            pr = new Pair<>(ss[0], Long.parseLong(ss[1]));
            events.add(pr);
        }

        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                long secsBetweenNowAndStart = ChronoUnit.SECONDS.between(startOfWeek, now);
                                int index = -1;
                                for(int i = 0;i<events.size();i++) {
                                    if(secsBetweenNowAndStart > events.get(i).second) continue;
                                    index = i;
                                    break;
                                }

                                eventName.setText(events.get(index).first);
                                long s = events.get(index).second-secsBetweenNowAndStart;
                                if(s <= 0 && !played) {
                                    v.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
                                    mPlayer.start();
                                    played = true;
                                    button.setVisibility(View.VISIBLE);
                                } else if(s > 0) played = false;
                                eventTime.setText(TimeUtils.convertFromSeconds(s,true));
                                notificationUtils.showNotification(0, events.get(index).first, TimeUtils.convertFromSeconds(s,true));


                                StringBuilder next = new StringBuilder();
                                for(int i=index+1;i<index+11 && i<events.size();i++)
                                    next.append(events.get(i).first).append(" ").append(TimeUtils.convertFromSeconds(events.get(i).second-secsBetweenNowAndStart,false)).append("\n");

                                nextEvent.setText(next);

                                now = LocalDateTime.now();
                            }
                        });
                    }
                } catch (InterruptedException ignored) {}
            }
        };

        thread.start();
    }
}
