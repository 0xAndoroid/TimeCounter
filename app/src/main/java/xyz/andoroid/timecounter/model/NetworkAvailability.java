package xyz.andoroid.timecounter.model;

import android.os.AsyncTask;

import java.net.InetAddress;

public class NetworkAvailability extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }
}
