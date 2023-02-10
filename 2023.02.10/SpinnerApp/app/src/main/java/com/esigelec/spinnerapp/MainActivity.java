package com.esigelec.spinnerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Spinner spinner;
    EditText nameEditText;
    Button button;
    ArrayList<String>itemList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = findViewById(R.id.spinner);
        nameEditText = findViewById(R.id.nameEditText);
        button = findViewById(R.id.button);
        button.setOnClickListener(view -> {
            String item = nameEditText.getText().toString();
            itemList.add(item);
            saveItensToFile();
            updateSpinner();
            nameEditText.setText("");
        });

        loadItensFromFile();
        updateSpinner();

    }
    private void updateSpinner(){
        int itemSelected = spinner.getSelectedItemPosition();
        ArrayAdapter<String>adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_spinner_item,
                itemList
        );
        spinner.setAdapter(adapter);
        spinner.setSelection(itemSelected);
    }
    private void loadItensFromFile(){
        try{
            InputStream inputStream = openFileInput("itens.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            itemList.clear();
            String line;
            while((line = bufferedReader.readLine())!= null){
                itemList.add(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void saveItensToFile(){
        try {
            OutputStream outputStream = openFileOutput("itens.txt",
                    MODE_PRIVATE);
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
            for (String item:itemList) {
                streamWriter.write(item);
                streamWriter.write("\n");
            }
            streamWriter.flush();
            streamWriter.close();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}