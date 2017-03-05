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

    int count=0;//,count5=0;
    //int step1=0,step5=0;
    //Timer timer;

    SteperDB steperDB = new SteperDB(this);

    public SensorService() {
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
            //timer = new Timer();
            //timer.schedule(timerTask,0,1000);
        }

        return START_NOT_STICKY;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            count ++;
            Log.e(TAG, "SensorEventListener " + count + " " + sensorEvent.timestamp/1000000);
            int cnt = steperDB.queryDailyStep(DateUtil.getDateTime());
            steperDB.updateDailyStep(cnt + 1);

            Intent intent = new Intent("STEPCOUNT");
            intent.putExtra("COUNT",cnt+1);
            sendBroadcast(intent);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };



}
