package club.andnext.utils;

import java.util.Calendar;

public class CalendarUtils {

    /**
     *
     * @param t
     * @return
     */
    public static final boolean isToday(long t) {
        return isSameDay(t, System.currentTimeMillis());
    }

    /**
     * @param t1
     * @param t2
     * @return
     */
    public static boolean isSameDay(long t1, long t2) {
        int gap = getGapCount(t1, t2);
        if (gap == 0) {
            return true;
        }

        return false;
    }

    /**
     *
     * 获取两个日期之间的间隔天数
     * @return
     */
    public static final int getGapCount(long startDate, long endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTimeInMillis(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTimeInMillis(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }
}
