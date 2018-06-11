package ap.ky.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    Button btnEnable,btnDisable,btnReset,btnStep,btnDaily;
    ImageButton imgBtnStart;
    ImageButton imgBtnPause;
    ImageButton imgBtnReset;

    TextView txtCount;
    ListView listView;
    TextView txtToday;
    StepRecv recv;
    IntentFilter intentFilter;

    LinearLayout llDaily,llSingle,llHistory;
    DrawerLayout drawerLayout;

    SteperDB steperDB = new SteperDB(this);
    int count = 0;

    int drawerSelect = 0;
    static String DRAWER="DRAWER";
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(DRAWER,drawerSelect);
        Log.e(TAG,"onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getActionBar().setDisplayHomeAsUpEnabled(true);
       // getActionBar().setHomeButtonEnabled(true);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        txtCount = (TextView)findViewById(R.id.txtCount);
        //int cnt = steperDB.queryDailyStep(DateUtil.getDateTime());
        //txtCount.setText(String.valueOf(cnt));
        btnEnable = (Button)findViewById(R.id.btnEnable);
        btnDisable = (Button)findViewById(R.id.btnDisable);

        imgBtnStart = (ImageButton)findViewById(R.id.imgBtnStart);
        imgBtnPause = (ImageButton)findViewById(R.id.imgBtnPause);


        btnReset = (Button)findViewById(R.id.btnReset);
        btnStep = (Button)findViewById(R.id.btnStep);
        listView = (ListView)findViewById(R.id.listView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
      //  sensorManager.registerListener(sensorEventListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);

        btnDisable.setEnabled(false);
        imgBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.registerListener(sensorEventListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);

                btnEnable.setEnabled(false);
                btnDisable.setEnabled(true);
            }
        });

        imgBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(sensorEventListener);

                steperDB.inserStepstData(DateUtil.getFullDateTime(), count);


                btnEnable.setEnabled(true);
                btnDisable.setEnabled(false);
            }
        });
//        btnEnable.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                sensorManager.registerListener(sensorEventListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
//
//                btnEnable.setEnabled(false);
//                btnDisable.setEnabled(true);
//            }
//        });
//
//        btnDisable.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                sensorManager.unregisterListener(sensorEventListener);
//
//                steperDB.inserStepstData(DateUtil.getFullDateTime(), count);
//
//
//                btnEnable.setEnabled(true);
//                btnDisable.setEnabled(false);
//            }
//        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 0;
                txtCount.setText("0");
            }
        });
        Intent sensorService = new Intent(this,SensorService.class);
        if(SensorService.isRunning == false){
            Log.e(TAG,"Start sensor Service");
            startService(sensorService);
        }
        recv = new StepRecv();


        //listData(getCountData());
        //int todaySteps = steperDB.getTodayCount();
        txtToday = (TextView)findViewById(R.id.txtToday);
        //txtToday.setText("Today :" + todaySteps);

        btnDaily = (Button)findViewById(R.id.btnDaily);
        btnDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase  db = DBHelper.getDatabase(getApplicationContext());
                ArrayList<SteperDB.StepCount> data = steperDB.queryAllDailyData();

                listData(data);
            }
        });
        btnStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listData(getCountData());
                    }
                });
            }
        });
        //test
        //steperDB.testDailyData();

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.mipmap.drawer_shadow, Gravity.START);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,toolbar,
                R.string.open,
                R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("");
                Log.e(TAG,"onDrawerOpened");
            }
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle("Close");
//				invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
                Log.e(TAG,"onDrawerClosed");
            }
        };
        String[] drawer_menu = this.getResources().getStringArray(R.array.drawer_menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawer_menu);
        final ListView lstDrawer = (ListView)findViewById(R.id.listView2);
        lstDrawer.setAdapter(adapter);
        actionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        llDaily = (LinearLayout)findViewById(R.id.llDaily);
        llSingle = (LinearLayout)findViewById(R.id.llSingle);
        llHistory = (LinearLayout)findViewById(R.id.history);
        llHistory.setVisibility(View.GONE);
        lstDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "position " + i);
                if(i==0){
                    llDaily.setVisibility(View.VISIBLE);
                    llSingle.setVisibility(View.GONE);
                    llHistory.setVisibility(View.GONE);
                }else if(i==1){
                    llDaily.setVisibility(View.GONE);
                    llSingle.setVisibility(View.VISIBLE);
                    llHistory.setVisibility(View.GONE);
                }else if(i==2){
                    llDaily.setVisibility(View.GONE);
                    llSingle.setVisibility(View.GONE);
                    llHistory.setVisibility(View.VISIBLE);
                }
                drawerSelect = i;
                drawerLayout.closeDrawers();
            }
        });
        if(savedInstanceState!=null){
            drawerSelect = savedInstanceState.getInt(DRAWER);
            if(drawerSelect==0){
                llDaily.setVisibility(View.VISIBLE);
                llSingle.setVisibility(View.GONE);
                llHistory.setVisibility(View.GONE);
            }else if(drawerSelect==1){
                llDaily.setVisibility(View.GONE);
                llSingle.setVisibility(View.VISIBLE);
                llHistory.setVisibility(View.GONE);
            }else if(drawerSelect==2){
                llDaily.setVisibility(View.GONE);
                llSingle.setVisibility(View.GONE);
                llHistory.setVisibility(View.VISIBLE);
            }

        }
    }
    ArrayList<SteperDB.StepCount> getCountData(){
        SQLiteDatabase  db = DBHelper.getDatabase(getApplicationContext());
        ArrayList<SteperDB.StepCount> data = steperDB.queryMonthlyData(DateUtil.getMonth());
        return data;
    }
    void listData(ArrayList<SteperDB.StepCount> data){

//        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
//        for (int i = 0; i < data.size(); i++) {
//            Map<String, Object> item = new HashMap<String, Object>();
//            item.put("txtDate", data.get(i).date);
//            item.put("txtStep", Integer.toString(data.get(i).count));
//            items.add(item);
//            Log.e(TAG,"count " + Integer.toString(data.get(i).count));
//        }
//        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
//                items,
//                R.layout.list_item,
//                new String[]{"txtDate","txtStep"},
//                new int[]{R.id.txtDate,R.id.txtSteps});
        DataAdapter dataAdapter = new DataAdapter(this,data);
        listView.setAdapter(dataAdapter);

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
   /* @Override
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
    }*/
    boolean isResume = false;
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume ");

        intentFilter = new IntentFilter("STEPCOUNT");
        registerReceiver(recv,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause ");
        isResume = false;
        IntentFilter intentFilter = new IntentFilter("STEPCOUNT");
        unregisterReceiver(recv);
    }

    class StepRecv extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int count = intent.getIntExtra("COUNT", 0);
            Log.e(TAG,"action " + action + " count " + count);
            txtToday.setText(Integer.toString(count));
        }
    }
}
