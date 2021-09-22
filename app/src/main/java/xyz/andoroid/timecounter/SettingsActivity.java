package xyz.andoroid.timecounter;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import xyz.andoroid.timecounter.model.ReaderUtils;

import java.io.IOException;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            ListPreference preference = findPreference("class");
            try {
                ReaderUtils readerUtils = new ReaderUtils(getContext());
                List<String> lines = readerUtils.readLine(getContext().getAssets().open("list.csv"));
                String[] entries = new String[lines.size()];
                String[] values = new String[lines.size()];
                for (int i = 0; i < lines.size(); i++) {
                    String[] split = lines.get(i).split(",");
                    entries[i] = split[0];
                    values[i] = split[1];
                }
                try {
                    assert preference != null;
                    preference.setEntryValues(values);
                    preference.setEntries(entries);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Preference versionCode = findPreference("version");
            Preference versionName = findPreference("release_name");
            assert versionName != null;
            versionName.setSummary(BuildConfig.BUILD_TYPE+" "+BuildConfig.VERSION_NAME);
            assert versionCode != null;
            versionCode.setSummary(BuildConfig.VERSION_CODE+"");
        }
    }
}