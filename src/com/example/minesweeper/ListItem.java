package com.example.minesweeper;

/**
 * Created by neeraj on 9/5/17.
 */

public class ListItem { //ScoreData

    private String name;
    private int score;
    private int time;

    ListItem() {
        name = "";
        score = 0;
        time = -1;
    }

    ListItem(String name, int score, int time) {
        this.name = name;
        this.score = score;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

}
