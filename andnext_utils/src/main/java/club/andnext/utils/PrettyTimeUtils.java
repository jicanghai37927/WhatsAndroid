package club.andnext.utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

public class PrettyTimeUtils {

    static PrettyTime sInstance;
    static Date sDate;

    static {
        sInstance = new PrettyTime();
        sDate = new Date();
    }

    public static final String format(long time) {
        sDate.setTime(time);
        String str = sInstance.format(sDate);

        return str;
    }
}
