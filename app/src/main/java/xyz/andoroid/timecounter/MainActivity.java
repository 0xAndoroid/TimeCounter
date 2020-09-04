package xyz.andoroid.timecounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

import xyz.andoroid.timecounter.model.MusicUtils;
import xyz.andoroid.timecounter.model.NotificationUtils;
import xyz.andoroid.timecounter.model.ReaderUtils;
import xyz.andoroid.timecounter.model.TimeUtils;

public class MainActivity extends AppCompatActivity {
    private LocalDateTime now;

    private List<Pair<String, Long>> events;

    private LocalDateTime startOfWeek;
    private NotificationUtils notificationUtils;

    private MusicUtils musicUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);
        now = LocalDateTime.now();
        events = new ArrayList<>();
        notificationUtils = new NotificationUtils((NotificationManager)getSystemService(NOTIFICATION_SERVICE), this);

        startOfWeek = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0,0);
        startOfWeek = startOfWeek.minusDays(now.getDayOfWeek().compareTo(DayOfWeek.MONDAY));

        musicUtils = new MusicUtils(this, R.raw.ring);

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
        final Switch ringSwitch = findViewById(R.id.ringSwitch);


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
                                if(s <= 0 && ringSwitch.isChecked()) {
                                    musicUtils.play(5000);
                                }
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
