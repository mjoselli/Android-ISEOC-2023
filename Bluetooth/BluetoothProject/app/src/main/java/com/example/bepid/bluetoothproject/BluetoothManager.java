package com.example.bepid.bluetoothproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothManager {


    private static final BluetoothManager instance = new BluetoothManager();

    public static BluetoothManager getInstance() {
        return instance;
    }

    //CHANGE THE UUID FOR YOUR CODE
    private static UUID MY_UUID;
    private static String SEARCH_NAME;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_VISIBLE_BT = 2;
    private BluetoothSocket mBtSocket;


    private BluetoothAdapter myBluetoothAdapter;

    public boolean initializeBluetooth(Activity activity, String uuid, String name) {

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            return false;
        }
        MY_UUID = UUID.fromString(uuid);
        SEARCH_NAME = name;

        activity.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        activity.registerReceiver(bReceiver, new IntentFilter(BluetoothAdapter
                .ACTION_SCAN_MODE_CHANGED));
        activity.registerReceiver(bReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        activity.registerReceiver(bReceiver, new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));

        activity.registerReceiver(bReceiver, new IntentFilter(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
        activity.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST));
        activity.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_UUID));
        activity.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));


        return true;
    }

    public void closeBluetooth(Activity activity) {
        if (mBtSocket != null) {
            try {
                mBtSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mBtSocket = null;
        }

        activity.unregisterReceiver(bReceiver);
    }

    public int turnOnBluetooth(Activity activity) {
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            return REQUEST_ENABLE_BT;
        }

        return 0;

    }

    public void turnOffBluetooth() {
        myBluetoothAdapter.disable();
    }

    public boolean isBluetoothOn(){
        return myBluetoothAdapter.isEnabled();
    }

    public void makeBluetoothDiscoverable(Activity activity,int time,BluetoothCallback bluetoothCallback){
        Intent turnVisibleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        turnVisibleIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
        activity.startActivityForResult(turnVisibleIntent, REQUEST_VISIBLE_BT);
        callback = bluetoothCallback;
    }

    public void startDiscover(BluetoothCallback bluetoothCallback){
        callback = bluetoothCallback;
        if(myBluetoothAdapter.isDiscovering())
            return;
        Log.d("BT","startDiscovery");

        myBluetoothAdapter.startDiscovery();
    }
    BluetoothCallback callback;
    void startListening(BluetoothCallback bluetoothCallback){
        callback = bluetoothCallback;
        AcceptTask task = new AcceptTask();
        task.execute(MY_UUID);
    }

    //AsyncTask to accept incoming connections
    private class AcceptTask extends AsyncTask<UUID,Void,BluetoothSocket> {

        @Override
        protected BluetoothSocket doInBackground(UUID... params) {
            String name = myBluetoothAdapter.getName();
            try {
                //While listening, set the discovery name to a specific value
                myBluetoothAdapter.setName(SEARCH_NAME);
                BluetoothServerSocket socket = myBluetoothAdapter
                        .listenUsingRfcommWithServiceRecord("Server", params[0]);
                BluetoothSocket connected = socket.accept();
                //Reset the BT adapter name
                myBluetoothAdapter.setName(name);
                return connected;
            } catch (IOException e) {
                e.printStackTrace();


                return null;
            }


        }
        @Override
        protected void onPostExecute(BluetoothSocket socket) {
            if(socket == null) {
                if(callback != null){
                    callback.onBluetoothConnection(BLUETOOTH_CONNECTED_ERROR);
                }
                return;
            }

            if(callback != null){
                callback.onBluetoothConnection(BLUETOOTH_CONNECTED);
            }

            mBtSocket = socket;

        }

    }



    public static final int BLUETOOTH_ON = 1000;
    public static final int BLUETOOTH_OFF = -1000;

    public static final int BLUETOOTH_DISCOVERY_LISTEN = 1001;
    public static final int BLUETOOTH_DISCOVERY_CANCELED = -1001;

    public static final int BLUETOOTH_CONNECTED = 1002;
    public static final int BLUETOOTH_CONNECTED_ERROR = -1002;

    public static final int BLUETOOTH_DISCOVERABLE = 1003;
    public static final int BLUETOOTH_CONNECTABLE = 1004;
    public static final int BLUETOOTH_NOT_CONNECTABLE = 1004;

    public int CheckActivityResult(int requestCode, int resultCode){
        Log.d("BT","CheckActivityResult");

        if(requestCode == REQUEST_ENABLE_BT) {
            if (myBluetoothAdapter.isEnabled()) {
                return BLUETOOTH_ON;
            } else {
                return BLUETOOTH_OFF;
            }
        }
        if(requestCode == REQUEST_VISIBLE_BT){
            if(resultCode == Activity.RESULT_CANCELED){
                Log.d("BT","RESULT_CANCELED");
                return BLUETOOTH_DISCOVERY_CANCELED;
            }else{
                Log.d("BT","BLUETOOTH_DISCOVERY_LISTEN");

                startListening(callback);
                return BLUETOOTH_DISCOVERY_LISTEN;
            }
        }

        return 0;
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d("BT","bReceiver:"+action);

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("BT","ACTION_FOUND:"+device.getName());

                // add the name and the MAC address of the object to the arrayAdapter
                if(TextUtils.equals(device.getName(), SEARCH_NAME)) {
                    //Matching device found, connect
                    myBluetoothAdapter.cancelDiscovery();
                    try {
                        mBtSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                        mBtSocket.connect();

                        if(callback != null){
                            callback.onBluetoothConnection(BLUETOOTH_CONNECTED);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        if(callback != null){
                            callback.onBluetoothConnection(BLUETOOTH_CONNECTED_ERROR);
                        }

                    }
                }
            }else if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,
                        BluetoothAdapter.ERROR);


                switch(mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        if(callback != null){
                            callback.onBluetoothDiscovery(BLUETOOTH_DISCOVERABLE);
                        }
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        if(callback != null){
                            callback.onBluetoothDiscovery(BLUETOOTH_CONNECTABLE);
                        }
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        if(callback != null){
                            callback.onBluetoothDiscovery(BLUETOOTH_NOT_CONNECTABLE);
                        }
                        break;
                }


            }



        }
    };

    public void sendData(BluetoothCallback bluetoothCallback,String data){
        callback = bluetoothCallback;
        SendDataTask task = new SendDataTask();
        task.execute(data);
    }
    ReadThread readThread;
    public void startReadingData(BluetoothCallback bluetoothCallback){
        callback = bluetoothCallback;
        readThread = new ReadThread(mBtSocket,callback);
        readThread.start();
    }

    public void stopReadingData(){
        if(readThread != null){
            readThread.interrupt();
            readThread = null;
        }

    }


    //AsyncTask to receive a single line of data and post
    private class SendDataTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            InputStream in = null;
            OutputStream out = null;
            try {
                //Send your data
                out = mBtSocket.getOutputStream();
                out.write(params[0].getBytes());
                //Receive the other's data


                in = mBtSocket.getInputStream();
                //byte[] buffer = new byte[1024];
                //in.read(buffer);
                //Create a clean string from results
                //String result = new String(buffer);
                //Close the connection
                //mBtSocket.close();
                return "SENDED";
            } catch (Exception exc) {

                return exc.getMessage();
            }
        }



        @Override
        protected void onPostExecute(String result) {
            if(callback != null)
                callback.onReceiveData(result);
        }

    }

    private class ReadThread extends Thread {
        BluetoothCallback callback;
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;

        public ReadThread(BluetoothSocket socket,BluetoothCallback bluetoothCallback) {
            mmSocket = socket;
            callback = bluetoothCallback;
            InputStream tmpIn = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();

            } catch (IOException e) {
                Log.e("IOException", "temp sockets not created", e);
            }

            mmInStream = tmpIn;
        }

        public void run() {

            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String result = new String(buffer);
                    Log.d("DATA_RECEIVED",result.trim());
                    callback.onReceiveData(result.trim());

                } catch (IOException e) {
                    Log.e("IOException", "disconnected", e);
                    callback.onBluetoothConnection(BLUETOOTH_CONNECTED_ERROR);
                    break;
                }
            }
        }

    }


}

