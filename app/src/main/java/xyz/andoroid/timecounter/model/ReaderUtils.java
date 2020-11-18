package xyz.andoroid.timecounter.model;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReaderUtils {
    private Context mContext;

    public ReaderUtils(Context context) {
        this.mContext = context;
    }

    public List<String> readLine(String path) {
        List<String> mLines = new ArrayList<>();

        AssetManager am = mContext.getAssets();

        try {
            //InputStream is = am.open(path);
            //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
            String line;

            while ((line = reader.readLine()) != null)
                mLines.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mLines;
    }
}
