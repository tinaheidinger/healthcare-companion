package com.example.maria.basestationapp;

/**
 * Created by maria on 06/01/17.
 */

public class Goal {
    public String emoji;
    public String name;

    public Goal(String emoji, String name){
        this.emoji=emoji;
        this.name=name;
    }

    public String toString(){
        return emoji+" "+name;
    }
}
