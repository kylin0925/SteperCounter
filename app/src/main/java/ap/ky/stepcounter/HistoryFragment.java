package ap.ky.stepcounter;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import ap.ky.util.DateUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    View layoutInflate;
    Button btnStep,btnDaily;
    ListView listView;
    SteperDB steperDB = new SteperDB(getActivity());
    String TAG = "HistoryFragment";
    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutInflate =  inflater.inflate(R.layout.fragment_history, container, false);
        listView = (ListView)layoutInflate.findViewById(R.id.listView);
        btnDaily = (Button)layoutInflate.findViewById(R.id.btnDaily);
        btnDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = DBHelper.getDatabase(getActivity().getApplicationContext());
                ArrayList<SteperDB.StepCount> data = steperDB.queryAllDailyData();

                listData(data);
            }
        });
        btnStep = (Button)layoutInflate.findViewById(R.id.btnStep);
        btnStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listData(getCountData());
            }
        });
        return layoutInflate;
    }
    ArrayList<SteperDB.StepCount> getCountData(){
        SQLiteDatabase  db = DBHelper.getDatabase(getActivity().getApplicationContext());
        ArrayList<SteperDB.StepCount> data = steperDB.queryMonthlyData(DateUtil.getMonth());
        return data;
    }
    void listData(ArrayList<SteperDB.StepCount> data){
        DataAdapter dataAdapter = new DataAdapter(getActivity(),data);
        listView.setAdapter(dataAdapter);
        String m1 = DateUtil.getMonth();
        Log.e(TAG, "month of day " + m1 + " " + DateUtil.getMonthOffset(m1, 1));
    }
}
