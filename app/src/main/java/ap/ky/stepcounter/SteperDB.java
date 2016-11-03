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
    private int todayCount;

    SteperDB(Context context){
        this.context = context;
    }
    //DBHelper dbHelper;
    private String getDateTime(){
        Date dt = new Date();
        String dts = sdf.format(dt);
        return dts;
    }

    void insertTestData(SQLiteDatabase db,String date,int count){
        ContentValues cv = new ContentValues();
        cv.put("count",count);
        cv.put("date", date);
        db.insert("data", null, cv);
    }
//    void insertData(String date,int count){
//        SQLiteDatabase  db = DBHelper.getDatabase(context);
//        ContentValues cv = new ContentValues();
//        cv.put("count",count);
//        cv.put("date", date);
//        db.insert("data", null, cv);
//    }
    void queryData(String date){
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        String dts1 = DateUtil.getDateTimeDayOffset(date,1);

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

    int queryDailyStep(String date){
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        String dts1 = DateUtil.getDateTimeDayOffset(DateUtil.getFullDateTime(), 1);

        String sql = "select count,date from daily where date >= '"+ date + "' and date<='"+dts1 + "'";
        Log.e(TAG, "query " + sql);
        Cursor c = db.rawQuery(sql, null);
        todayCount = 0;
        while(c.moveToNext()){
            //Log.e(TAG," "+c.getInt(0) + " " + c.getString(1) + " " +c.getInt(2));
            todayCount = c.getInt(0);
            Log.e(TAG,"today count " + todayCount + "  " + c.getString(1) );

        }
        if(todayCount == 0){
            insertDailyStep(0);
        }
        return todayCount;
    }
    void updateDailyStep(int count){

        String date = getDateTime();
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        ContentValues cv = new ContentValues();
        cv.put("count",count);
        //cv.put("date", date);
        Log.e(TAG, "updateDailyStep " + date + " count " + count);
        db.update("daily", cv, "date=?" , new String[]{date});
    }
    void insertDailyStep(int count){
        String date = getDateTime();
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        ContentValues cv = new ContentValues();
        cv.put("count",count);
        cv.put("date", date);
        db.insert("daily", null, cv);
    }
    public class StepCount{
        int id;
        String date;
        int count;
    }
    ArrayList<StepCount> arrStepCount = new ArrayList<>();

    void addToStepCount(Cursor c){
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
    }
    //query date table
    ArrayList<StepCount> queryByDate(String start,String end){
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        String sql = "select * from stepcount where date >= '"+ start + "' and date<='"+end + "'";
        Log.e(TAG, "queryByDate query " + sql);
        Cursor c = db.rawQuery(sql, null);

        addToStepCount(c);
        return arrStepCount;
    }

    ArrayList<StepCount> queryAllDailyData(){
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        String sql = "select * from daily";
        Log.e(TAG, "queryByDate query " + sql);
        Cursor c = db.rawQuery(sql, null);

        addToStepCount(c);
        return arrStepCount;
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

    void testDailyData(){
        String date = getDateTime();
        SQLiteDatabase  db = DBHelper.getDatabase(context);
        db.delete("daily",null ,null);
        for(int i = 1; i < 10;i++) {
            date = DateUtil.getDateTimeDayOffset(date,0-i);
            ContentValues cv = new ContentValues();
            cv.put("count", 12+i);
            cv.put("date", date);
            db.insert("daily", null, cv);
        }
    }
}
