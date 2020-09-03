package xyz.andoroid.timecounter.model;

public class TimeUtils {
    public static String convertFromSeconds(long s, boolean useSeconds) {
        long h = s/3600;
        s -=h*3600;
        long m = s/60;
        s-=m*60;
        String ans = h+":"+m;
        if(useSeconds) ans+=":"+s;
        return ans;
    }
}
