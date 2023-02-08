package com.esigelec.myactivities;

public class Singleton {
    private static final Singleton instance = new Singleton();
    private Singleton(){

    }
    public static Singleton getInstance(){
        return instance;
    }
    public String message = "";
    public boolean isOn = false;
}
