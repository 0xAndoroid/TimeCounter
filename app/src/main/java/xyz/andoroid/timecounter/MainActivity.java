package xyz.andoroid.timecounter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xyz.andoroid.timecounter.model.*;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

        CounterIntentForeground ci = new CounterIntentForeground(this, this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ConstraintLayout constraintLayout = findViewById(R.id.MainLayout);
        constraintLayout.setOnClickListener(v -> v.setBackgroundColor((int)(Math.random()*0xFF000000)));

        ci.startThreadForView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, NotifyService.class));
        } else {
            startService(new Intent(this, NotifyService.class));
        }
    }

    public void alterViews(String eventN, String eventT, String nextE) {
        final TextView eventName = findViewById(R.id.EventName);
        final TextView eventTime = findViewById(R.id.EventTime);
        final TextView nextEvent = findViewById(R.id.NextEvent);
        eventName.setText(eventN);
        eventTime.setText(eventT);
        nextEvent.setText(nextE);
    }

    public void updateFont(String font, int size) {
        final TextView eventName = findViewById(R.id.EventName);
        final TextView eventTime = findViewById(R.id.EventTime);
        final TextView nextEvent = findViewById(R.id.NextEvent);

        if(font.equalsIgnoreCase("anime_ace")) {
            eventName.setTypeface(ResourcesCompat.getFont(this, R.font.anime_ace));
            eventTime.setTypeface(ResourcesCompat.getFont(this, R.font.anime_ace));
            nextEvent.setTypeface(ResourcesCompat.getFont(this, R.font.anime_ace));
            eventName.setTextSize(TypedValue.COMPLEX_UNIT_SP,24f*size/50f);
            eventTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,50f*size/50f);
            nextEvent.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f*size/50f);
        } else if(font.equalsIgnoreCase("serif")) {
            eventName.setTypeface(Typeface.SANS_SERIF);
            eventTime.setTypeface(Typeface.SANS_SERIF);
            nextEvent.setTypeface(Typeface.SANS_SERIF);
            eventName.setTextSize(TypedValue.COMPLEX_UNIT_SP,(24*1.35f*size/50f));
            eventTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,(50*1.35f*size/50f));
            nextEvent.setTextSize(TypedValue.COMPLEX_UNIT_SP,(18*1.35f*size/50f));
        } else {
            eventName.setTypeface(ResourcesCompat.getFont(this, R.font.times));
            eventTime.setTypeface(ResourcesCompat.getFont(this, R.font.times));
            nextEvent.setTypeface(ResourcesCompat.getFont(this, R.font.times));
            eventName.setTextSize(TypedValue.COMPLEX_UNIT_SP,(24*1.35f*size/50f));
            eventTime.setTextSize(TypedValue.COMPLEX_UNIT_SP,(50*1.35f*size/50f));
            nextEvent.setTextSize(TypedValue.COMPLEX_UNIT_SP,(18*1.35f*size/50f));
        }
    }


    //Check if those two needed

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings_item) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
