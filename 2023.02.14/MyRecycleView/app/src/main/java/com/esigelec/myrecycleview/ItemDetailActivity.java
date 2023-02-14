package com.esigelec.myrecycleview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class ItemDetailActivity extends AppCompatActivity {
    EditText nameEditText;
    EditText quantityEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        nameEditText = findViewById(R.id.nameEditText);
        quantityEditText = findViewById(R.id.quantityEditText);

        if(Singleton.getInstance().itemSelected != -1){
            GroceryItem item = Singleton.getInstance().groceryItems.get(
                    Singleton.getInstance().itemSelected
            );
            nameEditText.setText(item.name);
            quantityEditText.setText(""+item.quantity);
        }
    }

    @Override
    public void onBackPressed() {
        if(Singleton.getInstance().itemSelected == -1) {
            Singleton.getInstance().groceryItems.add(
                    new GroceryItem(nameEditText.getText().toString(),
                            Integer.valueOf(quantityEditText.getText().toString()))
            );
        }else{
            Singleton.getInstance().groceryItems.set(
                    Singleton.getInstance().itemSelected,
                    new GroceryItem(nameEditText.getText().toString(),
                            Integer.valueOf(quantityEditText.getText().toString()))
            );
        }
        super.onBackPressed();
    }
}