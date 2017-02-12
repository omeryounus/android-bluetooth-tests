package com.todohacker.bluetooth.bluetoothlistener;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private static final UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Button but_paired;
    public BluetoothAdapter mBluetoothAdapter;
    final int MESSAGE_READ = 1;
    ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtState = (TextView) findViewById(R.id.txtState);
        txtState.setText("hola");
        CustomBroadcastReceiver receiver = new CustomBroadcastReceiver();
        // TEST

        registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));

        this.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        this.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
        this.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));

        receivers.add(receiver);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        but_paired = (Button) findViewById(R.id.but_paired);
        sv = (ScrollView) findViewById(R.id.scrollView);


        but_paired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pairedDevices = MainActivity.this.mBluetoothAdapter.getBondedDevices();
                // If there are paired devices
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        // Add the name and address to an array adapter to show in a ListView
                        setStatusText(device.getName() + " MAC: " + device.getAddress());
                        ParcelUuid uuids[] = device.getUuids();
                        for (ParcelUuid uuid : uuids) {
                            setStatusText(uuid.toString());
                        }

                    }
                }

            }
         });

        /*AcceptThread mAcceptThread = new AcceptThread();
        mAcceptThread.start();*/



        /*try {
            BluetoothServerSocket bss = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Proba", mDeviceUUID);
            // bss.accept();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (BroadcastReceiver r : receivers) {
            unregisterReceiver(r);
        }
    }

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private TextView txtState;

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private void setStatusText(String text) {
        txtState.setText(txtState.getText().toString() + '\n' + sdf.format(new Date()) + ": " + text + '\n');
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void setMsgText(String text) {
        txtState.setText(txtState.getText().toString() + text);
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private List<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();

    private class CustomBroadcastReceiver extends BroadcastReceiver {

        // private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        setStatusText("Bluetooth on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        setStatusText("Turning Bluetooth on...");
                        break;
                }
            } else if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                setStatusText("ACTION_CONNECTION_STATE_CHANGED");
            } else if (action.equals(BluetoothDevice.AACTION_ACL_CONNECTED)) {

                AcceptThread mAcceptThread = new AcceptThread();
                mAcceptThread.start();
                setStatusText(action.toString());
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                setStatusText("Connected device " + device.getAddress());
            } else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                setStatusText(action.toString());
                /*AcceptThread mAcceptThread = new AcceptThread();
                mAcceptThread.start();*/
            } else {
                setStatusText(action.toString());
            }
        }
    };

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("test", UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        ConnectedThread conn = new ConnectedThread(socket);
        conn.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    setMsgText(readMessage);
                    break;
            }
        }
    };

}

