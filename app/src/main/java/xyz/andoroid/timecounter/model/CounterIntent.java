package xyz.andoroid.timecounter.model;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;
import xyz.andoroid.timecounter.NotifyService;
import xyz.andoroid.timecounter.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CounterIntent {
    protected Context context;

    protected List<Triplet> events;

    protected LocalDateTime now;
    protected LocalDateTime startOfWeek;

    protected SharedPreferences preferences;

    protected String classCode;
    protected boolean enableNotification;

    protected boolean weekEnded = false;
    protected int lastIndex = -1;

    protected boolean classCodeJustUpdated = false;

    public CounterIntent(Context context) {
        this.context = context;
        now = LocalDateTime.now();
        events = new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        updateClassCode();
        startOfWeek = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0,0);
        startOfWeek = startOfWeek.minusDays(now.getDayOfWeek().compareTo(DayOfWeek.MONDAY));
    }

    protected void updateClassCode() {
        events.clear();
        classCode = preferences.getString("class","0");
        classCodeJustUpdated = true;

        ReaderUtils readerUtils = new ReaderUtils(context);
        try {
            String suffix = preferences.getBoolean("evenWeek", false) ? "_even" : "_odd";
            List<String> allTextLines = readerUtils.readLine(context.getAssets().open(classCode + suffix + ".csv"));
            for (String s : allTextLines) {
                String[] ss = s.split(",");
                Triplet pr = new Triplet(ss[0], Long.parseLong(ss[1]), Integer.parseInt(ss[2]));
                events.add(pr);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
