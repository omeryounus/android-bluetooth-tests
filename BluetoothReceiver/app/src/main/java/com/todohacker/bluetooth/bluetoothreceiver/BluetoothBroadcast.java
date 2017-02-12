package com.todohacker.bluetooth.bluetoothreceiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by 4m1g0 on 15/03/15.
 */
public class BluetoothBroadcast extends BroadcastReceiver {
    private final String ADDRESS = "2014:10:310190";
    private final String TAG = "BluetoothBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        //if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            Log.d(TAG, "Action connected");
            Intent i = new Intent(context, BluetoothService.class);
            context.startService(i);
            Log.d(TAG, "Action connected22");
            //BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //if (device.getAddress().equals(ADDRESS)){
                //BluetoothChatService mChatServicemChatService = new BluetoothChatService(context/*, mHandler*/);
                //Intent i = new Intent(context, MainActivity.class);
                //context.startActivity(i);
//            }
        //}
    }
}
