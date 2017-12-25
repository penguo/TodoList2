package com.pepg.todolist.DataBase;

/**
 * Created by pengu on 2017-11-28.
 */

public class DataAlarm {

    int alarmId, id, currentState;
    String date, detail;

    public DataAlarm(int alarmId, int id, String date, int currentState, String detail) {
        this.alarmId = alarmId;
        this.id = id;
        this.date = date;
        this.currentState = currentState;
        this.detail = detail;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
