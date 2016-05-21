package ap.ky.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by kylin25 on 2016/5/21.
 */
public class DateUtil {
    private static final String TAG = "DateUtil";
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static String getDateTime(){
        Date dt = new Date();
        String dts = sdf.format(dt);
        return dts;
    }
    public static String getFullDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = new Date();
        String dts = sdf.format(dt);
        return dts;
    }
    public static String getMonth(){
        String dts = "";
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.e(TAG,"year " + year + " " + month + " " + days);

        //Date dt = new Date(year,month,1);
        calendar = new GregorianCalendar(year,month,1);
        dts = sdf.format(calendar.getTime());
        return dts;
    }
    public static String getDateTimeDayOffset(String dts,int day){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(dts));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DATE, day);
        String dts1 = sdf.format(calendar.getTime());
        return dts1;
    }
    public static String getMonthOffset(String dts,int month){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(dts));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.MONTH, month);
        String dts1 = sdf.format(calendar.getTime());
        return dts1;
    }
}
