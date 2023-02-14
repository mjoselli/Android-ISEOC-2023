package com.esigelec.myrecycleview;

import java.util.ArrayList;

public class Singleton {
    private static Singleton instance = new Singleton();
    private Singleton(){

    }
    public static Singleton getInstance(){
        return instance;
    }
    public ArrayList<GroceryItem>groceryItems = new ArrayList<>();
    public int itemSelected = -1;
}
