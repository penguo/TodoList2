package com.pepg.todolist.DataBase;

import java.util.Date;

/**
 * Created by pengu on 2017-10-23.
 */

public class DataMerge {

    int position, id;
    Date date;
    int num;

    public DataMerge(int position, int id, Date date) {
        this.position = position;
        this.id = id;
        this.date = date;
    }

    public DataMerge(int position, int id, int num) {
        this.position = position;
        this.id = id;
        this.num = num;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
