package ap.ky.stepcounter;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ap.ky.util.DateUtil;

public class MainActivity extends AppCompatActivity  {
    String TAG = "Stepcounter";
    SensorManager sensorManager;
    Sensor stepCounter;
    Button btnEnable,btnDisable,btnReset;
    TextView txtCount;
    ListView listView;
    StepRecv recv;
    IntentFilter intentFilter;


    SteperDB steperDB = new SteperDB(this);
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCount = (TextView)findViewById(R.id.txtCount);
        btnEnable = (Button)findViewById(R.id.btnEnable);
        btnDisable = (Button)findViewById(R.id.btnDisable);
        btnReset = (Button)findViewById(R.id.btnReset);
        listView = (ListView)findViewById(R.id.listView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
      //  sensorManager.registerListener(sensorEventListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);

        btnDisable.setEnabled(false);

        btnEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sensorManager.registerListener(sensorEventListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);

                btnEnable.setEnabled(false);
                btnDisable.setEnabled(true);
            }
        });

        btnDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sensorManager.unregisterListener(sensorEventListener);

                steperDB.inserStepstData(DateUtil.getFullDateTime(), count);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listData();
                    }
                });
                btnEnable.setEnabled(true);
                btnDisable.setEnabled(false);
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 0;
            }
        });
        Intent sensorService = new Intent(this,SensorService.class);
        if(SensorService.isRunning == false){
            Log.e(TAG,"Start sensor Service");
            startService(sensorService);
        }
        recv = new StepRecv();


        listData();

    }
    void listData(){
        SQLiteDatabase  db = DBHelper.getDatabase(getApplicationContext());
        ArrayList<SteperDB.StepCount> data = steperDB.queryMonthlyData(DateUtil.getMonth());

        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("txtDate", data.get(i).date);
            item.put("txtStep", Integer.toString(data.get(i).count));
            items.add(item);
            Log.e(TAG,"count " + Integer.toString(data.get(i).count));
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                items,
                R.layout.list_item,
                new String[]{"txtDate","txtStep"},
                new int[]{R.id.txtDate,R.id.txtSteps});

        listView.setAdapter(simpleAdapter);

        String m1 = DateUtil.getMonth();
        Log.e(TAG, "month of day " + m1 + " " + DateUtil.getMonthOffset(m1, 1));
    }
    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            Log.e(TAG,sensorEvent.timestamp / 1000000 + " " + sensorEvent.values[0] );
            count++;
            txtCount.setText(Integer.toString(count));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    boolean isResume = false;
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume ");
        //stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        //sensorManager.registerListener(sensorEventListener,stepCounter,SensorManager.SENSOR_DELAY_NORMAL);
        //isResume = true;

        intentFilter = new IntentFilter("STEPCOUNT");
        registerReceiver(recv,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause ");
        isResume = false;
        //sensorManager.unregisterListener(sensorEventListener);
        IntentFilter intentFilter = new IntentFilter("STEPCOUNT");
        unregisterReceiver(recv);
    }

    class StepRecv extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int count = intent.getIntExtra("COUNT", 0);
            Log.e(TAG,"action " + action + " count " + count);
            //txtCount.setText(Integer.toString(count));
        }
    }
}
