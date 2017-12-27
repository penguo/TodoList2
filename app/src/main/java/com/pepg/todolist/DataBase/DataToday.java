package com.pepg.todolist.DataBase;

/**
 * Created by pengu on 2017-12-26.
 */

public class DataToday {

    DataTodo dataTodo;
    DataSemi dataSemi;
    int status;

    public DataToday(DataTodo dataTodo, DataSemi dataSemi, int status) {
        this.dataTodo = dataTodo;
        this.dataSemi = dataSemi;
        this.status = status;
    }

    public void setDataTodo(DataTodo dataTodo) {
        this.dataTodo = dataTodo;
    }

    public void setDataSemi(DataSemi dataSemi) {
        this.dataSemi = dataSemi;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataTodo getDataTodo() {
        return dataTodo;
    }

    public DataSemi getDataSemi() {
        return dataSemi;
    }

    public int getStatus() {
        return status;
    }
}
