package ap.ky.stepcounter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by kylin25 on 2016/5/15.
 */
public class DataAdapter extends BaseAdapter {


    public DataAdapter(Context context) {
        SQLiteDatabase dbHelper = DBHelper.getDatabase(context);
    }
    public void setMonth(int Month){

    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        return null;
    }
}
