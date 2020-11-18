package xyz.andoroid.timecounter.model;

import android.content.SharedPreferences;
import android.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class UpdateUtils {
    private SharedPreferences sharedPreferences;

    public UpdateUtils(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void update(String dir, String classCode) {
        String thisVersion = getVersionOfClassesFile(classCode);
        String serverVersion = getVersionFromServer(classCode);
        if(!thisVersion.trim().equalsIgnoreCase(serverVersion.trim())) {
            downloadClassFile(dir, classCode+".csv");
            setVersionOfClassFile(classCode, serverVersion);
        }
    }

    public String getVersionFromServer(String classCode) {
        try {
            WebAppUtils webAppUtils1 = new WebAppUtils();
            List<String> file = webAppUtils1.execute("http://tcapi.andoroid.xyz/getversion.php?class="+classCode).get();
            return file.get(0);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Pair<List<String>, List<String>> getAllClasses() {
        try {
            WebAppUtils webAppUtils = new WebAppUtils();
            Pair<List<String> , List<String>> list =new Pair<>(new ArrayList<>(),new ArrayList<>());
            List<String> file = webAppUtils.execute("http://tcapi.andoroid.xyz/getversion.php?class=-").get();
            for(int i=0;i<file.size();i+=2) {
                list.first.add(file.get(i));
                list.second.add(file.get(i+1));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void downloadClassFile(String dir, String classFile) {
        try {
            WebAppUtils webAppUtils2 = new WebAppUtils();
            FileWriter writer = new FileWriter(new File(dir + "/" + classFile));
            List<String> file = webAppUtils2.execute("http://tcapi.andoroid.xyz/getfile.php?class="+classFile).get();
            for (String s : file) {
                writer.write(s + "\n");
            }
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isInternetAvailable() {
        try {
            NetworkAvailability networkAvailability = new NetworkAvailability();
            return networkAvailability.execute().get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public String getVersionOfClassesFile(String classCode) {
        return sharedPreferences.getString(classCode, "0");
    }

    public void setVersionOfClassFile(String classCode, String version) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(classCode, version);
        editor.apply();
    }
}
