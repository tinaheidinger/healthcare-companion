package com.example.maria.basestationapp;

/**
 * Created by maria on 06/01/17.
 */

public class Goal {
    public int id;
    public String emoji;
    public String name;

    public Goal(String emoji, String name){
        this.id=id;
        this.emoji=emoji;
        this.name=name;
    }

    public Goal(int id, String emoji, String name){
        this.id=id;
        this.emoji=emoji;
        this.name=name;
    }

    public String toString(){
        return emoji+" "+name;
    }
}
