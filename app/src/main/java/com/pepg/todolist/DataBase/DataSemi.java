package com.pepg.todolist.DataBase;

import com.pepg.todolist.Manager;

/**
 * Created by pengu on 2017-12-01.
 */

public class DataSemi {

    private int id, position, parentId, ach, achMax;
    private String title, memo;

    public DataSemi() {
    }

    public DataSemi(int id, int position, int parentId, String title, String memo) {
        this.id = id;
        this.position = position;
        this.parentId = parentId;
        this.title = title;
        this.memo = memo;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public String getMemo() {
        return memo;
    }

    public int getPosition() {
        return position;
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
}