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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
    DrawerLayout drawerLayout;

    int drawerSelect = 0;
    static String DRAWER="DRAWER";
    FragmentManager fragmentManager = getSupportFragmentManager();
    DailySetpFragment dailySetpFragment;
    SingleStepFragment singleStepFragment;
    HistoryFragment historyFragment;

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
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Intent sensorService = new Intent(this,SensorService.class);
        if(SensorService.isRunning == false){
            Log.e(TAG,"Start sensor Service");
            startService(sensorService);
        }
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

        dailySetpFragment = new DailySetpFragment();
        singleStepFragment = new SingleStepFragment();
        historyFragment = new HistoryFragment();

        lstDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "position " + i);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(i==0){
                    fragmentTransaction.replace(R.id.container,dailySetpFragment,"DailySetpFragment");
                }else if(i==1){
                    fragmentTransaction.replace(R.id.container,singleStepFragment,"SingleStepFragment");
                }else if(i==2){
                    fragmentTransaction.replace(R.id.container,historyFragment,"HistoryFragment");
                }
                fragmentTransaction.commit();
                drawerSelect = i;
                drawerLayout.closeDrawers();
            }
        });
        if(savedInstanceState!=null){
            drawerSelect = savedInstanceState.getInt(DRAWER);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(drawerSelect==0){
                fragmentTransaction.replace(R.id.container,dailySetpFragment,"DailySetpFragment");
            }else if(drawerSelect==1){
                fragmentTransaction.replace(R.id.container,singleStepFragment,"SingleStepFragment");
            }else if(drawerSelect==2){
                fragmentTransaction.replace(R.id.container,historyFragment,"HistoryFragment");
            }
            fragmentTransaction.commit();
        }else{
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container,dailySetpFragment,"DailySetpFragment");
            fragmentTransaction.commit();
        }
    }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause ");
        isResume = false;
    }
}
