package xyz.andoroid.timecounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

import xyz.andoroid.timecounter.model.*;

public class MainActivity extends AppCompatActivity {
    private LocalDateTime now;

    private List<Triplet> events;

    private LocalDateTime startOfWeek;
    private NotificationUtils notificationUtils;
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    private MusicUtils musicUtils;

    private boolean isLongPressed = false;
    private int deltaOnLongPress = 1000;
    private boolean animeMode = false;

    private int lastIndex = -1;

    private boolean dormitorySwitchJustChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);
        now = LocalDateTime.now();
        events = new ArrayList<>();
        notificationUtils = new NotificationUtils((NotificationManager)getSystemService(NOTIFICATION_SERVICE), this);
        preferences = getSharedPreferences("Prefs", MODE_PRIVATE);
        preferencesEditor = preferences.edit();

        startOfWeek = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0,0);
        startOfWeek = startOfWeek.minusDays(now.getDayOfWeek().compareTo(DayOfWeek.MONDAY));

        musicUtils = new MusicUtils(this, R.raw.ring);

        final TextView eventName = findViewById(R.id.EventName);
        final TextView eventTime = findViewById(R.id.EventTime);
        final TextView nextEvent = findViewById(R.id.NextEvent);
        final ConstraintLayout constraintLayout = findViewById(R.id.wdwadwa);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor((int)(Math.random()*0xFF000000));
            }
        });
        final Switch dormitorySwitch = findViewById(R.id.dormitorySwitch);
        dormitorySwitch.setChecked(preferences.getBoolean("dormitorySwitch", false));
        dormitorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferencesEditor.putBoolean("dormitorySwitch", isChecked);
                preferencesEditor.apply();
                dormitorySwitchJustChanged = true;
            }
        });
        final Switch ringSwitch = findViewById(R.id.ringSwitch);
        ringSwitch.setChecked(preferences.getBoolean("ringSwitch", true));
        ringSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferencesEditor.putBoolean("ringSwitch", isChecked);
                preferencesEditor.apply();
            }
        });

        ReaderUtils qb = new ReaderUtils(this);
        List<String> allTextLines = qb.readLine("schedule.csv");
        String[] ss;
        Triplet pr;
        for(String s : allTextLines) {
            ss = s.split(",");
            pr = new Triplet(ss[0], Long.parseLong(ss[1]), Integer.parseInt(ss[2]));
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
                                    if(!preferences.getBoolean("dormitorySwitch", false) && events.get(i).third == 0  ) continue;
                                    if(secsBetweenNowAndStart > events.get(i).second) continue;
                                    index = i;
                                    break;
                                }
                                eventName.setText(events.get(index).first);
                                long s = events.get(index).second-secsBetweenNowAndStart;
                                if(lastIndex == -1) lastIndex = index;
                                if(index != lastIndex && preferences.getBoolean("ringSwitch",true)) {
                                    if(dormitorySwitchJustChanged) dormitorySwitchJustChanged = false;
                                    else musicUtils.play(5000);
                                    lastIndex = index;
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

    @Override
    protected void onStop() {
        super.onStop();
    }
}
