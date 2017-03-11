package ap.ky.stepcounter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by kylin25 on 2016/5/15.
 */
public class DataAdapter extends BaseAdapter {

    Context context;
    ArrayList<SteperDB.StepCount> data;
    public DataAdapter(Context context,ArrayList<SteperDB.StepCount> data) {
        SQLiteDatabase dbHelper = DBHelper.getDatabase(context);
        this.context = context;
        this.data = data;
    }
    public void setMonth(int Month){

    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.daily_adapter_layout,viewGroup,false);
        TextView txtDate = (TextView) v.findViewById(R.id.txtDate);
        TextView txtSteps = (TextView) v.findViewById(R.id.txtSteps);
        txtDate.setText(data.get(i).date);
        txtSteps.setText(String.valueOf(data.get(i).count));

        return v;
    }
}
