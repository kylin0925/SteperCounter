package ap.ky.stepcounter;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import ap.ky.util.DateUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class SingleStepFragment extends Fragment {

    View layoutInflater;
    ImageButton imgBtnStart;
    ImageButton imgBtnPause;
    Button btnReset;
    TextView txtCount;
    SensorManager sensorManager;
    Sensor stepCounter;
    String TAG = "SingleStepFragment";
    SteperDB steperDB = new SteperDB(getActivity());
    int count = 0;
    public SingleStepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutInflater = inflater.inflate(R.layout.fragment_single_step, container, false);

        sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        imgBtnStart = (ImageButton)layoutInflater.findViewById(R.id.imgBtnStart);
        imgBtnPause = (ImageButton)layoutInflater.findViewById(R.id.imgBtnPause);
        btnReset = (Button)layoutInflater.findViewById(R.id.btnReset);
        txtCount = (TextView) layoutInflater.findViewById(R.id.txtCount);
        imgBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.registerListener(sensorEventListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
                //btnEnable.setEnabled(false);
                //btnDisable.setEnabled(true);
            }
        });

        imgBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(sensorEventListener);
                steperDB.inserStepstData(DateUtil.getFullDateTime(), count);
                //btnEnable.setEnabled(true);
                //btnDisable.setEnabled(false);
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 0;
                txtCount.setText("0");
            }
        });
        return layoutInflater;
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
}
