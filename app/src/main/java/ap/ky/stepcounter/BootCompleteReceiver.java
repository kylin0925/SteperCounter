package ap.ky.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {
    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent sensorService = new Intent(context,SensorService.class);
        context.startService(sensorService);
        //// TODO: 2016/6/25  
    }
}
