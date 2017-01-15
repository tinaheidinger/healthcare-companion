package com.example.maria.basestationapp;

/**
 * Created by maria on 15/01/17.
 */

public class Reminder {

    public String emoji;
    public String name;
    public int[] date_time;

    public Reminder(String emoji, String name, int[] date_time){
        this.emoji=emoji;
        this.name=name;
        this.date_time=date_time;
    }

    public String toString(){
        return emoji+" "+name+" "+date_time;
    }
}
