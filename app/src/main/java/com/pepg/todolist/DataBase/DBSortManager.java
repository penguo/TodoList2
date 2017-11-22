package com.pepg.todolist.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;

import com.pepg.todolist.Manager;

/**
 * Created by pengu on 2017-10-23.
 */

public class DBSortManager {

    SQLiteDatabase dbW, dbR;
    Cursor cursor;
    DBManager dbManager;
    Context context;
    int i, j;

    public DBSortManager(DBManager dbManager, Context context, SQLiteDatabase dbW, SQLiteDatabase dbR) {
        this.dbManager = dbManager;
        this.context = context;
        this.dbW = dbW;
        this.dbR = dbR;
    }

    public void sort() {
        i = 0;
        cursor = dbR.rawQuery("SELECT _id FROM TODOLIST;", null);
        while (cursor.moveToNext()) {
            dbW.execSQL("UPDATE TODOLIST SET _position = " + i + " WHERE _id = '" + cursor.getString(0) + "';");
            i++;
        }
        cursor.close();
    }

    public void sortByCategory(String category) {
        i = 0;
        cursor = dbR.rawQuery("SELECT _id FROM TODOLIST WHERE CATEGORY = '" + category + "';", null);
        while (cursor.moveToNext()) {
            dbW.execSQL("UPDATE TODOLIST SET _position = " + i + " WHERE _id = '" + cursor.getString(0) + "';");
            i++;
        }
        cursor.close();
    }

    public void sortByDate() {
        ArrayList<DataMerge> dataList = new ArrayList<>();
        ArrayList<DataMerge> result = new ArrayList<>();
        cursor = dbR.rawQuery("SELECT _id, DATE FROM TODOLIST WHERE _position > -1 ;", null);
        int length = cursor.getCount();
        i = 0;
        while (cursor.moveToNext()) {
            dataList.add(new DataMerge(i, cursor.getInt(0), Manager.calculateDday(cursor.getString(1))));
        }
        long[] list = new long[length];
        while (i < length) {
            list[i] = dataList.get(i).getNum();
            i++;
        }
        Arrays.sort(list);
        i = 0;
        while (i < length) {
            j = 0;
            while (j < length) {
                if (list[i] == dataList.get(j).getNum()) {
                    dataList.get(j).setPosition(i);
                    result.add(dataList.get(j));
                    dataList.remove(j);
                    break;
                } else {
                    j++;
                }
            }
            i++;
        }
        cursor.close();
        for (i = 0; i < length; i++) {
            dbW.execSQL("UPDATE TODOLIST SET _position = " + i + " WHERE _id = '" + result.get(i).getId() + "';");

        }
    }

    public void sortSemiByAch(int parentId) {
        cursor = dbR.rawQuery("SELECT _id, ACH FROM SEMITODO WHERE _parentId = '" + parentId + "';", null);
        ArrayList<DataMerge> dataList = new ArrayList<>();
        ArrayList<DataMerge> resultList = new ArrayList<>();
        i = 0;
        while (cursor.moveToNext()) {
            dataList.add(new DataMerge(i, cursor.getInt(0), cursor.getInt(1)));
        }
        int position = 0;
        for (i = 0; i <= 100; i++) {
            for (j = 0; j < dataList.size(); j++) {
                if (dataList.get(j).getNum() == i) {
                    dataList.get(j).setPosition(position);
                    resultList.add(dataList.get(j));
                    dataList.remove(j);
                    j--;
                    position++;
                }
            }
        }
        for(i=0;i<resultList.size();i++){
            dbW.execSQL("UPDATE SEMITODO SET _position = " + i + " WHERE _id = '" + resultList.get(i).getId() + "';");
        }
        cursor.close();
    }
}