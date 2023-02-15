package com.example.bepid.bluetoothproject;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

//class activity that implements the Bluetooth callback
public class MainActivity extends Activity implements BluetoothCallback{
    //declaration of the UI components
    ToggleButton toggleButtonBluetooth,toggleButtonVisible;
    ProgressBar spinnerDiscovering;
    Button buttonConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the bluetooth with the activity,
        // an UUID from the BT module
        BluetoothManager.getInstance().initializeBluetooth(this,
                "00001101-0000-1000-8000-00805F9B34FB","HEALTH_MODULE_1");

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1001);


        //all the functions from BluetoothManager must be accessible by using getInstance in
        // order to use the singleton

        //Hint: If you are connecting to a Bluetooth serial board then try using the well-known
        // SPP UUID 00001101-0000-1000-8000-00805F9B34FB.
        // However if you are connecting to an Android peer then please generate your own unique UUID.


        //pick up the UI components
        buttonConnect = (Button)findViewById(R.id.buttonConnect);
        toggleButtonBluetooth = (ToggleButton)findViewById(R.id.toggleButtonBluetooth);
        spinnerDiscovering = (ProgressBar)findViewById(R.id.progressBarDiscover);
        toggleButtonVisible = (ToggleButton)findViewById(R.id.toggleButtonVisible);

        //set the toogleButton that will show how is the state of the bluetooth to the correct state
        toggleButtonBluetooth.setChecked(BluetoothManager.getInstance().isBluetoothOn());

        //on the click will turn on or off the bluetooth device
        toggleButtonBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    BluetoothManager.getInstance().turnOnBluetooth(MainActivity.this);
                }else{
                    BluetoothManager.getInstance().turnOffBluetooth();
                }
            }
        });

        //on the click will turn on the discovery mode on the device for 10 seconds
        toggleButtonVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!BluetoothManager.getInstance().isBluetoothOn()){
                        Toast.makeText(MainActivity.this,"The BT device is OFF!",
                                Toast.LENGTH_SHORT).show();
                        toggleButtonVisible.setChecked(false);
                        return;
                    }
                    BluetoothManager.getInstance().makeBluetoothDiscoverable(MainActivity
                            .this,10,MainActivity
                            .this);
                    toggleButtonVisible.setEnabled(false);
                    spinnerDiscovering.setVisibility(View.VISIBLE);
                    buttonConnect.setVisibility(View.INVISIBLE);
                }else{

                }
            }
        });

        //on the click will start the connect mode
        //one device must be on the Discovey mode... and another in this connect mode!
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!BluetoothManager.getInstance().isBluetoothOn()) {
                    Toast.makeText(MainActivity.this, "The BT device is OFF!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                BluetoothManager.getInstance().startDiscover(MainActivity.this);
                Log.d("BT","startDiscover");
                spinnerDiscovering.setVisibility(View.VISIBLE);

            }
        });
    }

    //on the destroy we make sure to close the connections that we made.
    @Override
    protected void onDestroy() {
        BluetoothManager.getInstance().closeBluetooth(this);
        super.onDestroy();
    }

    //on onActivityResult we must implement the CheckActivityResult of the BluetoothManager that
    // will check results about some request that were made about discovering and on/off
    // relations to bluetooth.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int resultBluetooth = BluetoothManager.getInstance().CheckActivityResult(requestCode,
                resultCode);

        if(resultBluetooth == BluetoothManager.BLUETOOTH_ON){
            toggleButtonBluetooth.setChecked(true);
        }else if(resultBluetooth == BluetoothManager.BLUETOOTH_OFF){
            toggleButtonBluetooth.setChecked(false);
        }else if(resultBluetooth == BluetoothManager.BLUETOOTH_DISCOVERY_LISTEN){
            toggleButtonVisible.setEnabled(false);
            spinnerDiscovering.setVisibility(View.VISIBLE);
            buttonConnect.setVisibility(View.INVISIBLE);
        }else if(resultBluetooth == BluetoothManager.BLUETOOTH_DISCOVERY_CANCELED){
            toggleButtonVisible.setEnabled(true);
            toggleButtonVisible.setChecked(false);
            spinnerDiscovering.setVisibility(View.INVISIBLE);
            buttonConnect.setVisibility(View.VISIBLE);
        }

        super.onActivityResult(requestCode,resultCode,data);
    }

    //callback for when the device is connected or an error occurred. When the connection os ok,
    // it starts a new Activity for the message exchange
    @Override
    public void onBluetoothConnection(int returnCode) {
        if(returnCode == BluetoothManager.BLUETOOTH_CONNECTED){
            Toast.makeText(MainActivity.this, "Connected",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ConnectedActivity.class);

            startActivity(intent);
        }else if(returnCode == BluetoothManager.BLUETOOTH_CONNECTED_ERROR){
            Toast.makeText(MainActivity.this, "ConnectionError",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //callback for when the device is discoverable or if it ended the discoverable mode.
    @Override
    public void onBluetoothDiscovery(int returnCode) {
        if(returnCode == BluetoothManager.BLUETOOTH_DISCOVERABLE){
            toggleButtonVisible.setEnabled(false);
            spinnerDiscovering.setVisibility(View.VISIBLE);
            buttonConnect.setVisibility(View.INVISIBLE);

        }else if(returnCode == BluetoothManager.BLUETOOTH_CONNECTABLE ||
                returnCode == BluetoothManager.BLUETOOTH_NOT_CONNECTABLE){
            toggleButtonVisible.setEnabled(true);
            toggleButtonVisible.setChecked(false);
            spinnerDiscovering.setVisibility(View.INVISIBLE);
            buttonConnect.setVisibility(View.VISIBLE);
        }
    }

    //callback for when the bluetooth receive some that, that we are going to deal in a separated
    // activity
    @Override
    public void onReceiveData(String data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1001: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // --->
                    Log.d("BT", "permisionGranted");
                } else {
                    //TODO re-request
                    Log.d("BT", "permisionNOTGranted");
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);

                }
                break;
            }
        }
    }
}
