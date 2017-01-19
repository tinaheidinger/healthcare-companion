package com.example.maria.basestationapp;

/**
 * Created by maria on 15/01/17.
 */

public class Reminder {

    public String emoji;
    public String name;
    public int[] date_time;
    public String day;

    public Reminder(String emoji, String name, int[] date_time){
        this.emoji=emoji;
        this.name=name;
        this.date_time=date_time;
    }

    public Reminder(String emoji, String name){
        this.emoji=emoji;
        this.name=name;
        this.date_time=new int[5];
    }

    public void setDate_time(int year, int month, int day, int hour, int minute){
        date_time = new int[]{year, month, day, hour, minute};
    }

    public void setDay(String day){
        this.day=day;
    }

    public String toString(){
        return emoji+" "+name+" "+date_time;
    }
}
