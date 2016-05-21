package ap.ky.stepcounter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ap.ky.util.DateUtil;

/**
 * Created by kylin25 on 2016/5/15.
 */
public class SteperDB {
    String TAG = "SteperDB";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    Context context;
    SteperDB(Context context){
        this.context = context;
    }
    //DBHelper dbHelper;
    private String getDateTime(){
        Date dt = new Date();
        String dts = sdf.format(dt);
        return dts;
    }
    private String getDateTimeDayOffset(String dts,int day){
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
    void insertTestData(SQLiteDatabase db,String date,int count){
        ContentValues cv = new ContentValues();
        cv.put("count",count);
        cv.put("date", date);
        db.insert("data", null, cv);
    }
    void insertData(String date,int count){
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        ContentValues cv = new ContentValues();
        cv.put("count",count);
        cv.put("date", date);
        db.insert("data", null, cv);
    }
    void queryData(String date){
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        String dts1 = getDateTimeDayOffset(date,1);

        String sql = "select sum(count) from data where date >= '"+ date + "' and date<='"+dts1 + "'";
        Log.e(TAG, "query " + sql);
        Cursor c = db.rawQuery(sql, null);

        while(c.moveToNext()){
            //Log.e(TAG," "+c.getInt(0) + " " + c.getString(1) + " " +c.getInt(2));
            Log.e(TAG,"daily count "+c.getInt(0) );
        }
    }
    void inserStepstData(String date,int count){
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        ContentValues cv = new ContentValues();
        cv.put("count",count);
        cv.put("date", date);
        db.insert("stepcount", null, cv);
    }
    public class StepCount{
        int id;
        String date;
        int count;
    }
    ArrayList<StepCount> arrStepCount = new ArrayList<>();

    ArrayList<StepCount> queryByDate(String start,String end){
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        String sql = "select * from stepcount where date >= '"+ start + "' and date<='"+end + "'";
        Log.e(TAG, "query " + sql);
        Cursor c = db.rawQuery(sql, null);

        arrStepCount.clear();
        while(c.moveToNext()){
            //Log.e(TAG," "+c.getInt(0) + " " + c.getString(1) + " " +c.getInt(2));
            Log.e(TAG,"steps counts "+c.getInt(0) + " " + c.getString(1) + " " + c.getInt(2) );
            StepCount tmp = new StepCount();
            tmp.id = c.getInt(0);
            tmp.date = c.getString(1);
            tmp.count = c.getInt(2);
            arrStepCount.add(tmp);
        }
        return arrStepCount;
    }
    ArrayList<StepCount> queryStepsData(String date){
        String dts1 = getDateTimeDayOffset(date,1);
        return queryByDate(date,dts1);
    }
    ArrayList<StepCount> queryMonthlyData(String date){
        String dts1 = DateUtil.getMonthOffset(date, 1);
        return queryByDate(date,dts1);
    }
    void testCalculate(){
        String dts = "2016-01-01 12:00";// getDateTime();
        String dts1 = "2016-01-02 12:00";// getDateTime();
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        db.delete("data","date>=? and date<=?" ,new String[]{dts,dts1});
        insertTestData(db,dts,1);
        insertTestData(db, dts, 2);
        insertTestData(db, dts, 3);

        queryData(dts);
    }
}
