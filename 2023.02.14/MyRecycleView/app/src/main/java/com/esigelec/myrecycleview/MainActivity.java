package com.esigelec.myrecycleview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    GroceryItemArrayAdapter arrayAdapter = new GroceryItemArrayAdapter();
    FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        for (int i = 0; i < 20; i++) {
            Singleton.getInstance().groceryItems.add(
                    new GroceryItem("Item "+i,i)
            );
        }
        recyclerView.setAdapter(arrayAdapter);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(MainActivity.this)
        );
        recyclerView.addItemDecoration(
                new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL)
        );
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(view -> {
            Singleton.getInstance().itemSelected = -1;
            Intent intent = new Intent(MainActivity.this,ItemDetailActivity.class);
            startActivity(intent);
        });
        arrayAdapter.setClickListener(new GroceryItemArrayAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Singleton.getInstance().itemSelected = position;
                Intent intent = new Intent(MainActivity.this,ItemDetailActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Singleton.getInstance().groceryItems.remove(position);
                arrayAdapter.notifyItemRemoved(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        arrayAdapter.notifyDataSetChanged();
    }
}