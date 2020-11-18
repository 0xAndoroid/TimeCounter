package xyz.andoroid.timecounter.model;

import android.os.AsyncTask;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebAppUtils extends AsyncTask<String, Integer, List<String>> {
    @Override
    protected List<String> doInBackground(String... strings) {
        try {
            List<String> ret = new ArrayList<>();
            URL url = new URL(strings[0]);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                ret.add(line);
            }
            in.close();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
