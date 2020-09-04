package xyz.andoroid.timecounter.model;

public class TimeUtils {
    public static String convertFromSeconds(long s, boolean useSeconds) {
        long h = s/3600;
        s -=h*3600;
        long m = s/60;
        s-=m*60;
        String ans = (h/10==0?"0"+h:h)+":"+(m/10==0?"0"+m:m);
        if(useSeconds) ans+=":"+(s/10==0?"0"+s:s);
        return ans;
    }
}
