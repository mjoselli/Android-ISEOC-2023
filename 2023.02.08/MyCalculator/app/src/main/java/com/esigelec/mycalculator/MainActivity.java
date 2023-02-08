package com.esigelec.mycalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText firstNumberEditText;
    EditText secondNumberEditText;
    TextView resultTextView;
    Button plusButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstNumberEditText = findViewById(R.id.firstNumberEditText);
        secondNumberEditText = findViewById(R.id.secondNumberEditText);
        resultTextView = findViewById(R.id.resultTextView);
        createOperation(R.id.plusButton);
        createOperation(R.id.minusButton);
        createOperation(R.id.multiButton);
        createOperation(R.id.divButton);
    }
    private void createOperation(int id){
        Button button = findViewById(id);
        button.setOnClickListener(view -> {
            calculateOperation(view.getId());
        });
    }
    private void calculateOperation(int id){
        double num1 = getDoubleFromEditText(firstNumberEditText);
        double num2 = getDoubleFromEditText(secondNumberEditText);
        double result = 0.0;
        if(id == R.id.plusButton)
            result = num1+num2;
        else if(id == R.id.minusButton)
            result = num1-num2;
        else if(id == R.id.multiButton)
            result = num1*num2;
        else if(id == R.id.divButton)
            result = num1/num2;
        resultTextView.setText("Result:"+result);
    }
    private double getDoubleFromEditText(EditText editText){
        String s = editText.getText().toString();
        double num = 0.0;
        try {
            num = Double.valueOf(s);
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Field should not be empty", Toast.LENGTH_LONG).show();
        }
        return num;
    }
}