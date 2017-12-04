package com.pepg.todolist.DataBase;

import com.pepg.todolist.Manager;

/**
 * Created by pengu on 2017-12-01.
 */

public class DataTodo {

    int id, position, ach, dday, ach_finish, ach_max;
    String title, category, date, createdate, memo;

    public DataTodo(){
    }

    public DataTodo(int id, int position, String title, String category, String date, String memo, String createDate) {
        this.id = id;
        this.position = position;
        this.title = title;
        this.category = category;
        this.date = date;
        this.memo = memo;
        this.createdate = createDate;
        dday = Manager.calculateDday(date);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAch(int ach) {
        this.ach = ach;
    }

    public void setAch_finish(int ach_finish) {
        this.ach_finish = ach_finish;
    }

    public void setAch_max(int ach_max) {
        this.ach_max = ach_max;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public void setDday(int dday) {
        this.dday = dday;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public int getAch() {
        return ach;
    }

    public int getAch_finish() {
        return ach_finish;
    }

    public int getAch_max() {
        return ach_max;
    }

    public int getDday() {
        return dday;
    }

    public int getPosition() {
        return position;
    }

    public String getCategory() {
        return category;
    }

    public String getCreatedate() {
        return createdate;
    }

    public String getMemo() {
        return memo;
    }
}
