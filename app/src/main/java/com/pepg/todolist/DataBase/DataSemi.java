package com.pepg.todolist.DataBase;

import com.pepg.todolist.Manager;

/**
 * Created by pengu on 2017-12-01.
 */

public class DataSemi {

    private int id, parentId, ach, achMax, number;
    private String title, memo, date;
    private boolean isNew;

    public DataSemi(int parentId) {
        this.id = 0;
        this.parentId = parentId;
        this.title = "";
        this.memo = "";
        this.number = 0;
        this.date = "";
        this.isNew = true;
    }

    public DataSemi(int id, int position, int parentId, String title, String memo, int number, String date) {
        this.id = id;
        this.parentId = parentId;
        this.title = title;
        this.memo = memo;
        this.number = number;
        this.date = date;
        this.isNew = false;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setAch(int ach) {
        this.ach = ach;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAchMax(int achMax) {
        this.achMax = achMax;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMemo() {
        return memo;
    }

    public int getAch() {
        return ach;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public int getAchMax() {
        return achMax;
    }

    public int getParentId() {
        return parentId;
    }

    public int getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }

    public boolean isNew() {
        return isNew;
    }

}