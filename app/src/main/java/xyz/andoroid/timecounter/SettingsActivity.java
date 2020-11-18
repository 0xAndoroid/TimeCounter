package xyz.andoroid.timecounter;

import android.os.Bundle;
import android.util.Pair;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import xyz.andoroid.timecounter.model.UpdateUtils;

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
            if(UpdateUtils.isInternetAvailable()) {
                Pair<List<String>, List<String>> get =  UpdateUtils.getAllClasses();
                if(get==null) return;
                String[] entries = new String[get.first.size()];
                String[] values = new String[get.first.size()];
                for(int i=0;i<get.first.size();i++) {
                    values[i] = get.first.get(i);
                    entries[i] = get.second.get(i);
                }
                try {
                    assert preference != null;
                    preference.setEntryValues(values);
                    preference.setEntries(entries);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}