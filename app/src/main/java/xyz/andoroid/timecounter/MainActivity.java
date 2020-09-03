package xyz.andoroid.timecounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

public class MainActivity extends AppCompatActivity {
    LocalDateTime now;

    List<Pair<String, Long>> events;

    LocalDateTime startOfWeek;
    Vibrator v;
    boolean played = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        now = LocalDateTime.now();
        events = new ArrayList<>();
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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
                //v.setBackground(getResources().getDrawable(R.drawable.test));
            }
        });

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.stop();
                button.setVisibility(View.INVISIBLE);
            }
        });

        QuoteBank qb = new QuoteBank(this);
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
                                if(s <= 5 && !played) {
                                    v.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
                                    mPlayer.start();
                                    played = true;
                                    button.setVisibility(View.VISIBLE);
                                } else if(s > 5){
                                    played = false;
                                }
                                long h = s/3600;
                                s -=h*3600;
                                long m = s/60;
                                s-=m*60;
                                eventTime.setText(h+":"+m+":"+s);

                                String next = "";
                                for(int i=index+1;i<index+9 && i<events.size();i++) {
                                    s = events.get(i).second-secsBetweenNowAndStart;
                                    h = s/3600;
                                    s -=h*3600;
                                    m = s/60;
                                    s-=m*60;
                                    next+=events.get(i).first+" ";
                                    next+=h+":"+m+":"+s;
                                    next+="\n";
                                }

                                nextEvent.setText(next);

                                now = LocalDateTime.now();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();
    }
}
