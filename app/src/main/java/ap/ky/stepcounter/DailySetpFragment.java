package ap.ky.stepcounter;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DailySetpFragment extends Fragment {

    View layoutInflater;
    String TAG = "DailySetpFragment";
    TextView txtToday;
    StepRecv recv;
    IntentFilter intentFilter;
    int count = 0 ;
    public DailySetpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutInflater = inflater.inflate(R.layout.fragment_daily_setp, container, false);
        txtToday = (TextView)layoutInflater.findViewById(R.id.txtToday);
        recv = new StepRecv();
        Log.e(TAG,"onCreateView");
        return layoutInflater;
    }

    @Override
    public void onResume() {
        super.onResume();
        intentFilter = new IntentFilter("STEPCOUNT");
        getActivity().registerReceiver(recv,intentFilter);
        Log.e(TAG,"onResume");
        txtToday.setText(Integer.toString(count));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(recv);
        Log.e(TAG,"onPause");
    }

    class StepRecv extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            count = intent.getIntExtra("COUNT", 0);
            Log.e(TAG,"action " + action + " count " + count);
            txtToday.setText(Integer.toString(count));
        }
    }
}
