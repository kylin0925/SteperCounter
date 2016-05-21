package ap.ky.stepcounter;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

import ap.ky.util.DateUtil;

public class SensorService extends Service {
    private String TAG="SensorService";
    public static boolean isRunning = false;
    SensorManager sensorManager;
    Sensor stepCounter;

    int count=0,count5=0;
    int step1=0,step5=0;
    Timer timer;

    SQLiteDatabase db;
    SteperDB steperDB = new SteperDB(this);
    String countInsert = "insert into data";
    public SensorService() {
//

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        isRunning = true;
        if(sensorManager == null) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(sensorEventListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
            timer = new Timer();
            timer.schedule(timerTask,0,1000);
        }
        //return super.onStartCommand(intent, flags, startId);
        Notification n = new Notification();

        startForeground(123,n);
        if(db == null){
            db = DBHelper.getDatabase(this);
        }
        return START_NOT_STICKY;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String getDateTime(){

        Date dt = new Date();
        String dts = sdf.format(dt);
        return dts;
    }

    String getDateTimeDayOffset(String dts,int day){
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
    void queryData(SQLiteDatabase db,String date){
        String dts1 = getDateTimeDayOffset(date, 1);

        String sql = "select sum(count) from data where date >= '"+ date + "' and date<='"+dts1 + "'";
        Log.e(TAG, "query " + sql);
        Cursor c = db.rawQuery(sql, null);

        while(c.moveToNext()){
            //Log.e(TAG," "+c.getInt(0) + " " + c.getString(1) + " " +c.getInt(2));
            Log.e(TAG,"daily count "+c.getInt(0) );
        }
    }
    String getSecond(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = new Date();
        String dts = sdf.format(dt);

        sdf = new SimpleDateFormat("ss");
        String sec = sdf.format(new Date());
        return sec;
    }
    String getMin(){
        SimpleDateFormat sdf = sdf = new SimpleDateFormat("mm");
        String mm = sdf.format(new Date());

        return mm;
    }
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            String sec = getSecond();
            String dts = DateUtil.getDateTime();

            if(sec.equals("00")) {
                //Log.e(TAG,"sec " + sec);
                step1 = count;
                Log.e(TAG, dts + " count " + step1);

                steperDB.insertData(dts, step1);
                steperDB.queryData(dts);

                count5+=step1;
                count = 0;

                String mm = getMin();
                if(mm.substring(1,2).equals("0") ||  mm.subSequence(1,2).equals("5") ){
                    Log.e(TAG,dts + " count5 " + count5);
                    step5 = count5;
                    count5 = 0;
                }
            }



        }
    };
    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            count ++;
            Log.e(TAG, " " + count + " " + sensorEvent.timestamp/1000000);
            Intent intent = new Intent("STEPCOUNT");
            intent.putExtra("COUNT",count);
            sendBroadcast(intent);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Intent intent = new Intent("STEPCOUNT");
                    intent.putExtra("COUNT",count);
                    sendBroadcast(intent);
                    break;
                default:
                    break;
            }
        }
    };

}
