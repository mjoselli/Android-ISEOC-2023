package com.esigelec.myactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    Button button;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);

        button = findViewById(R.id.button);
        button.setOnClickListener(view -> {
            EditText editText = findViewById(R.id.editText);
            String s = editText.getText().toString();
            Singleton.getInstance().message = s;
            Intent intent = new Intent(MainActivity.this,SecondActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Singleton.getInstance().isOn)
            imageView.setImageResource(R.drawable.rick2);
        else
            imageView.setImageResource(R.drawable.rick);
    }
}