package com.esigelec.myactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ToggleButton;

public class SecondActivity extends AppCompatActivity {
    ToggleButton toggleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setTitle(Singleton.getInstance().message);

        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setChecked(Singleton.getInstance().isOn);
    }

    @Override
    public void onBackPressed() {

        Singleton.getInstance().isOn = toggleButton.isChecked();
        finish();
    }
}