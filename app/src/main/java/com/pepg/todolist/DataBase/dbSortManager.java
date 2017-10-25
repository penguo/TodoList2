package com.pepg.todolist.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.pepg.todolist.R;

/**
 * Created by pengu on 2017-10-23.
 */

public class dbSortManager {

    SQLiteDatabase dbW, dbR;
    Cursor cursor;
    dbManager dbManager;
    Context context;
    int i, j;

    public dbSortManager(dbManager dbManager, Context context, SQLiteDatabase dbW, SQLiteDatabase dbR) {
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.");
        Date readDate;
        ArrayList<DataMerge> dataList = new ArrayList<>();
        ArrayList<DataMerge> result = new ArrayList<>();
        String[] strings;
        ArrayList<Integer> ListofStringdate = new ArrayList<>();
        int sizeLoS = 0;
        cursor = dbR.rawQuery("SELECT _id, DATE FROM TODOLIST WHERE _position > -1 ;", null);
        int length = cursor.getCount();
        i = 0;
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(context.getString(R.string.unregistered)) || cursor.getString(1).equals(context.getString(R.string.date_forever))) {
                ListofStringdate.add(cursor.getInt(0));
                sizeLoS++;
                length--;
            } else {
                try {
                    strings = cursor.getString(1).split(" ");
                    readDate = format.parse(strings[1] + strings[2] + strings[3]);
                    dataList.add(new DataMerge(i, cursor.getInt(0), readDate));
                    i++;
                } catch (ParseException e) {
                    length--;
                }
            }
        }
        i = 0;
        long[] list = new long[length];
        while (i < length) {
            list[i] = dataList.get(i).getDate().getTime();
            i++;
        }
        Arrays.sort(list);
        i = 0;
        while (i < length) {
            j = 0;
            while (j < length) {
                if (list[i] == dataList.get(j).getDate().getTime()) {
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
            dbW.execSQL("UPDATE TODOLIST SET _position = " + result.get(i).getPosition() + " WHERE _id = '" + result.get(i).getId() + "';");
        }
        for (i = 0; i < sizeLoS; i++) {
            dbW.execSQL("UPDATE TODOLIST SET _position = " + length + " WHERE _id = '" + ListofStringdate.get(i) + "';");
            ListofStringdate.get(i);
            length++;
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
                if (dataList.get(j).getAch() == i) {
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

    public void sortTest() {
        // TODO : Sort를 써보려했지만 실패...
//        for (j = 0; j < length; j++) {
//            key = dataList.get(j);
//            i = j - 1;
//            while (i >= 0 && dataList.get(i).getDate().getTime() > key.getDate().getTime()) {
//                dataList.get(i + 1).setMerge(dataList.get(i));
//                i--;
//            }
//            dataList.get(i + 1).setMerge(key);
//            Log.e("dbSM", j + ": " + dataList.get(j).getPosition() + ":" + dataList.get(j).getDate());
//        }
    }
}