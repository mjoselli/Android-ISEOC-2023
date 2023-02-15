package com.example.bepid.bluetoothproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//class activity that implements the Bluetooth callback
public class ConnectedActivity extends Activity implements BluetoothCallback {
    //declaration of the UI components
    Button buttonSend;
    EditText editTextToSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        //start the thread responsible of receiving the data from the other device
        BluetoothManager.getInstance().startReadingData(this);

        //pick up the UI components
        editTextToSend = (EditText)findViewById(R.id.editTextToSend);
        buttonSend = (Button)findViewById(R.id.buttonSend);

        //when the button is clicked send the data from the EditText
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothManager.getInstance().sendData(ConnectedActivity.this,
                        editTextToSend.getText().toString());
            }
        });


    }

    @Override
    protected void onDestroy() {
        //stop the thread responsible for reading the data.
        BluetoothManager.getInstance().stopReadingData();
        super.onDestroy();
    }


    @Override
    public void onBluetoothConnection(int returnCode) {

    }

    @Override
    public void onBluetoothDiscovery(int returnCode) {

    }

    @Override
    public void onReceiveData(String data) {
        //if it receives a new data, put on a toast on the UIThread
        final String finalData = data;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), finalData,
                        Toast.LENGTH_LONG).show();
            }
        });

    }
}