package com.example.maria.basestationapp;

/**
 * Created by maria on 06/01/17.
 */

public class Goal {
    public String emoji;
    public String name;
    public String day;

    public Goal(String emoji, String name, String day){
        this.emoji=emoji;
        this.name=name;
        this.day=day;
    }

    public String toString(){
        return emoji+" "+name+" "+day;
    }
}
