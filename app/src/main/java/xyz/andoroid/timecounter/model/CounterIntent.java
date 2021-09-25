package xyz.andoroid.timecounter.model;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CounterIntent {
    public Context context;

    protected List<Triplet> events;

    protected LocalDateTime now;
    protected LocalDateTime startOfWeek;

    protected SharedPreferences preferences;

    protected String classCode;
    protected boolean enableNotification;
    protected boolean evenWeek;
    protected boolean onlineLectures;

    protected int lastIndex = -1;

    protected boolean classCodeJustUpdated = false;

    public CounterIntent(Context context) {
        this.context = context;
        now = LocalDateTime.now();
        events = new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        evenWeek = preferences.getBoolean("evenWeek", false);
        updateClassCode();
        startOfWeek = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0,0);
        startOfWeek = startOfWeek.minusDays(now.getDayOfWeek().compareTo(DayOfWeek.MONDAY));
    }

    protected void updateClassCode() {
        events.clear();
        classCode = preferences.getString("class","0");
        evenWeek = preferences.getBoolean("evenWeek", false);
        onlineLectures = preferences.getBoolean("onlineLectures", false);
        classCodeJustUpdated = true;

        ReaderUtils readerUtils = new ReaderUtils(context);
        try {
            List<String> allTextLines = readerUtils.readLine(context.getAssets().open(generateNameOffClass(classCode, evenWeek, onlineLectures)));
            for (String s : allTextLines) {
                String[] ss = s.split(",");
                Triplet pr = new Triplet(ss[0], Long.parseLong(ss[1]), Integer.parseInt(ss[2]));
                events.add(pr);
            }
            int i = 0;
            for (String s : allTextLines) {
                if(i >= 20) break;
                String[] ss = s.split(",");
                Triplet pr = new Triplet(ss[0], Long.parseLong(ss[1])+7*24*60*60, Integer.parseInt(ss[2]));
                events.add(pr);
                i++;
            }
        } catch (IOException ex) {
            preferences.edit().putString("class", "0").apply();
            updateClassCode();
            ex.printStackTrace();
        }
    }

    private String generateNameOffClass(String id, boolean even, boolean onlineLectures) {
        if(id.equals("0")) return "0.csv";
        String[] split = id.split("-");
        if(split.length != 3) return "0.csv";
        return split[0]+ (split[1].equals("1") ? (even ? "_even" : "_odd") : "") + (split[2].equals("1") ? (onlineLectures ? "__online_lectures" : "__std") : "") + ".csv";
    }
}
