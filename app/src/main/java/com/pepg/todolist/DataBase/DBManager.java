package com.pepg.todolist.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pepg.todolist.Manager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengu on 2017-08-10.
 */

public class DBManager extends SQLiteOpenHelper {

    SQLiteDatabase db, dbr;
    Cursor cursor;
    Context context;

    public static String DATA_SORTbyCATEGORY;
    public static int DATA_SORTbyTYPE;

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE TODOLIST ( " +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " _position INTEGER DEFAULT -1, " +
                " TITLE TEXT, " +
                " CATEGORY TEXT," +
                " DATE TEXT, " +
                " ACH INTEGER DEFAULT 0," +
                " MEMO TEXT, " +
                " CREATEDATE TEXT," +
                " TYPE INTEGER DEFAULT 1 );");
        db.execSQL(" CREATE TABLE SEMITODO ( " +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " _position INTEGER DEFAULT -1, " +
                " _parentId INTEGER, " +
                " TITLE TEXT, " +
                " ACH INTEGER DEFAULT 0," +
                " ACHMAX INTEGER DEFAULT 100," +
                " MEMO TEXT," +
                " NUMBER INTEGER DEFAULT 1 );");
        db.execSQL(" CREATE TABLE SETTING ( " +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " TYPE TEXT, " +
                " TITLE TEXT );");
        db.execSQL(" CREATE TABLE ALARM ( " +
                " _alarmId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " id INTEGER, " +
                " TIME TEXT," +
                " CURRENT INTEGER," +
                " DETAIL TEXT );");
        setDefault(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case (3):
                try {
                    db.beginTransaction();
                    db.execSQL("ALTER TABLE TODOLIST ADD COLUMN TYPE Integer DEFAULT 1");
                    db.setTransactionSuccessful();
                } catch (IllegalStateException e) {
                    Log.e("dbUpgrade error", e.toString());
                } finally {
                    db.endTransaction();
                }
                break;
            case (4):
                try {
                    db.beginTransaction();
                    db.execSQL("ALTER TABLE SEMITODO ADD COLUMN NUMBER Integer DEFAULT 0");
                    db.setTransactionSuccessful();
                } catch (IllegalStateException e) {
                    Log.e("dbUpgrade error", e.toString());
                } finally {
                    db.endTransaction();
                }
                break;
        }
    }

    public void reset() {
        db = getWritableDatabase();
        db.execSQL(" DROP TABLE TODOLIST ");
        db.execSQL(" DROP TABLE SEMITODO ");
        db.execSQL(" DROP TABLE SETTING ");
        db.execSQL(" DROP TABLE ALARM ");
        onCreate(db);
        db.close();
    }

    public void insertTodo(DataTodo data) {
        db = getWritableDatabase();
        dbr = getReadableDatabase();
        db.execSQL(" INSERT INTO TODOLIST VALUES ( " +
                " null, " +
                " 0, " +
                "'" + data.getTitle() + "', " +
                "'" + data.getCategory() + "', " +
                "'" + data.getDate() + "', " +
                data.getAch() + "," +
                "'" + data.getMemo() + "'," +
                "'" + data.getCreatedate() + "'," +
                data.getType() + ");");
        cursor = dbr.rawQuery("SELECT _id FROM TODOLIST WHERE TITLE = '" + data.getTitle() + "';", null);
        int id = 0;
        while (cursor.moveToLast()) {
            id = cursor.getInt(0);
        }
        setDummyData_Semi(id);
        setDummyData_Alarm(id);
    }

    public void setDummyData_Semi(int parentId) {
        // _parentId == 0 인 더미데이터들을 해당 _parentId에 배분합니다.
        db = getWritableDatabase();
        dbr = getReadableDatabase();
        cursor = dbr.rawQuery("SELECT _id FROM SEMITODO WHERE _parentId = 0;", null);
        while (cursor.moveToNext()) {
            db.execSQL(" UPDATE SEMITODO SET " +
                    "_parentId = '" + parentId + "' " +
                    "WHERE _id = '" + cursor.getInt(0) + "'; ");
        }
    }

    public void deleteDummyData_Semi() {
        // _parentId == 0 인 더미데이터들을 삭제합니다.
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT _id FROM SEMITODO WHERE _parentId = 0;", null);
        while (cursor.moveToNext()) {
            deleteSemi(cursor.getInt(0));
        }
    }

    public void setDummyData_Alarm(int parentId) {
        // _parentId == 0 인 더미데이터들을 해당 _parentId에 배분합니다.
        db = getWritableDatabase();
        dbr = getReadableDatabase();
        cursor = dbr.rawQuery("SELECT _AlarmId FROM ALARM WHERE id = 0;", null);
        while (cursor.moveToNext()) {
            db.execSQL(" UPDATE ALARM SET " +
                    "id = '" + parentId + "' " +
                    "WHERE _AlarmId = '" + cursor.getInt(0) + "'; ");
        }
    }

    public void deleteDummyData_Alarm() {
        // _parentId == 0 인 더미데이터들을 삭제합니다.
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT _AlarmId FROM ALARM WHERE id = 0;", null);
        while (cursor.moveToNext()) {
            deleteAlarm(cursor.getInt(0));
        }
    }

    public void updateTodo(DataTodo data) {
        db = getWritableDatabase();
        db.execSQL(" UPDATE TODOLIST SET " +
                "TITLE = '" + data.getTitle() + "', " +
                "CATEGORY = '" + data.getCategory() + "', " +
                "DATE = '" + data.getDate() + "', " +
                "ACH = " + data.getAch() + "," +
                "MEMO = '" + data.getMemo() + "'," +
                "CREATEDATE = '" + data.getCreatedate() + "'," +
                "TYPE = " + data.getType() + " " +
                "WHERE _id = " + data.getId() + " ; ");
        db.close();
    }

    public void deleteTodo(int id) {
        db = getWritableDatabase();
        db.execSQL("DELETE FROM TODOLIST WHERE _id = '" + id + "';");
        db.execSQL("DELETE FROM SEMITODO WHERE _parentId = '" + id + "';");
        db.close();
    }

    public ArrayList<DataTodo> getValueList() {
        db = getReadableDatabase();
        DataTodo data;
        ArrayList<DataTodo> list = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM TODOLIST;", null);
        while (cursor.moveToNext()) {
            data = new DataTodo(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getInt(8));
            list.add(data);
        }
        cursor.close();
        for (int i = 0; i < list.size(); i++) {
            cursor = db.rawQuery("SELECT ACH, ACHMAX FROM SEMITODO WHERE _parentId = " + list.get(i).getId() + " ;", null);
            if (cursor.getCount() == 0) // Semi 데이터가 없을 경우 - 직접 설정한 퍼센트로 적용.
            {
                list.get(i).setAch_finish(0);
                list.get(i).setAch_max(0);
                list.get(i).setAch(0);
            } else { // Semi 데이터가 하나 이상 있을 경우 - Semi의 완수도에 따라 적용.
                int totalAch = 0, totalAchMax = 0;
                while (cursor.moveToNext()) {
                    totalAch += cursor.getInt(0);
                    totalAchMax += cursor.getInt(1);
                }
                list.get(i).setAch_finish(totalAch);
                list.get(i).setAch_max(totalAchMax);
                list.get(i).setAch((totalAch * 100) / totalAchMax);
            }
            cursor.close();
        }
        list = setPosition(list);
        return list;
    }

    public DataTodo getValue(int id) {
        db = getReadableDatabase();
        DataTodo data = new DataTodo();
        cursor = db.rawQuery("SELECT * FROM TODOLIST WHERE _id = " + id + ";", null);
        while (cursor.moveToNext()) {
            data = new DataTodo(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getInt(8));
        }
        cursor.close();
        cursor = db.rawQuery("SELECT ACH, ACHMAX FROM SEMITODO WHERE _parentId = " + data.getId() + " ;", null);
        if (cursor.getCount() == 0) // Semi 데이터가 없을 경우 - 직접 설정한 퍼센트로 적용.
        {
            data.setAch_finish(0);
            data.setAch_max(0);
            data.setAch(0);
        } else { // Semi 데이터가 하나 이상 있을 경우 - Semi의 완수도에 따라 적용.
            int totalAch = 0, totalAchMax = 0;
            while (cursor.moveToNext()) {
                totalAch += cursor.getInt(0);
                totalAchMax += cursor.getInt(1);
            }
            data.setAch_finish(totalAch);
            data.setAch_max(totalAchMax);
            data.setAch((totalAch * 100) / totalAchMax);
        }
        cursor.close();
        return data;
    }

    public ArrayList<DataTodo> setPosition(ArrayList<DataTodo> list) {
        ArrayList<DataTodo> sortedList = new ArrayList<>();
        int i;
        if (DATA_SORTbyCATEGORY.equals("") || DATA_SORTbyCATEGORY.equals("DEFAULT")) {
            for (i = 0; i < list.size(); i++) {
                sortedList.add(list.get(i));
            }
        } else {
            for (i = 0; i < list.size(); i++) {
                if (list.get(i).getCategory().equals(DATA_SORTbyCATEGORY)) {
                    sortedList.add(list.get(i));
                }
            }
        }
        list = sortedList;
        sortedList = new ArrayList<>();
        if (DATA_SORTbyTYPE != 0) {
            for (i = 0; i < list.size(); i++) {
                if (list.get(i).getType() == DATA_SORTbyTYPE) {
                    sortedList.add(list.get(i));
                }
            }
            list = sortedList;
            sortedList = new ArrayList<>();
        }
        if (Manager.notViewPastData) {
            for (i = 0; i < list.size(); i++) {
                if (Manager.calculateDday(list.get(i).getDate()) >= 0) {
                    sortedList.add(list.get(i));
                }
            }
            list = sortedList;
        }

        // 날짜별로 정렬
        DataTodo pivot;
        int size = list.size();
        sortedList = new ArrayList<>();
        while (size != 0) {
            pivot = list.get(0);
            for (i = 1; i < list.size(); i++) {
                if (pivot.getDday() > list.get(i).getDday()) {
                    pivot = list.get(i);
                }
            }
            sortedList.add(pivot);
            list.remove(pivot);
            size--;
        }
        return sortedList;
    }

    public void insertSemi(DataSemi data){
        db = getWritableDatabase();
        db.execSQL(" INSERT INTO SEMITODO VALUES ( " +
                "null, " +
                "null, " +
                data.getParentId() + ", " +
                "'" + data.getTitle() + "', " +
                "0," +
                "100," +
                "'" + data.getMemo() + "'," +
                data.getNumber() + ");");
        db.close();
    }

    public void updateSemi(DataSemi data){
        db = getWritableDatabase();
        db.execSQL(" UPDATE SEMITODO SET " +
                "TITLE = '" + data.getTitle() + "', " +
                "ACH = '" + data.getAch() + "'," +
                "ACHMAX = '" + data.getAchMax() + "'," +
                "MEMO = '" + data.getMemo() + "'," +
                "NUMBER = " + data.getNumber() + " " +
                "WHERE _id = " + data.getId() + "; ");
    }

    public void deleteSemi(int id) {
        db = getWritableDatabase();
        db.execSQL("DELETE FROM SEMITODO WHERE _id = '" + id + "';");
        db.close();
    }

    public ArrayList<DataSemi> getSemiValueList(int parentId) {
        db = getReadableDatabase();
        ArrayList<DataSemi> list = new ArrayList<>();
        DataSemi data;
        cursor = db.rawQuery("SELECT * FROM SEMITODO WHERE _parentId = " + parentId + ";", null);
        while (cursor.moveToNext()) {
            data = new DataSemi(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(6),
                    cursor.getInt(7));
            try {
                data.setAch(cursor.getInt(4));
            } catch (Exception e) {
                data.setAch(0);
            }
            try {
                data.setAchMax(cursor.getInt(5));
            } catch (Exception e) {
                data.setAchMax(0);
            }
            list.add(data);
        }
        cursor.close();
        return list;
    }

    public List getSettingList(String type) {
        db = getReadableDatabase();
        List<String> result = new ArrayList<>();
        cursor = db.rawQuery("SELECT TITLE FROM SETTING WHERE TYPE = '" + type + "';", null);
        while (cursor.moveToNext()) {
            result.add(cursor.getString(0));
        }
        cursor.close();
        return result;
    }

    public boolean isAlreadyResCategory(String input) { // true: 이미 있는 카테고리 이름 , false : 새로운 카테고리 이름
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT TITLE FROM SETTING WHERE TYPE = 'category';", null);
        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(input)) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public void addSetting(String type, String content) {
        db = getWritableDatabase();
        db.execSQL(" INSERT INTO SETTING VALUES ( " +
                " null, " +
                "'" + type + "', " +
                "'" + content + "');");
        db.close();
    }

    public int getSettingSize(String type) {
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT _id FROM SETTING WHERE TYPE = '" + type + "';", null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    // TODO updateSetting, deleteSetting

    public void setDefault(SQLiteDatabase db) {
        db.execSQL(" INSERT INTO SETTING VALUES ( null, 'category', '미지정');");
        db.execSQL(" INSERT INTO SETTING VALUES ( null, 'date', '내일까지');");
        db.execSQL(" INSERT INTO SETTING VALUES ( null, 'date', '일주일 동안');");
        db.execSQL(" INSERT INTO SETTING VALUES ( null, 'date', '한달 동안');");
        db.execSQL(" INSERT INTO SETTING VALUES ( null, 'date', '6개월 동안');");
        db.execSQL(" INSERT INTO SETTING VALUES ( null, 'date', '생전에');");
        db.execSQL(" INSERT INTO SETTING VALUES ( null, 'default', '');");
    }

    // ALARM
    public void insertAlarm(int id, String time, String detail) {
        db = getWritableDatabase();
        db.execSQL(" INSERT INTO ALARM VALUES ( " +
                " null, " +
                id + ", " +
                "'" + time + "'," +
                "0, " +
                "'" + detail + "' );");
        db.close();
    }

    public void updateAlarm(int alarmId, int id, String time, int currentState, String detail) {
        db = getWritableDatabase();
        db.execSQL(" UPDATE ALARM SET " +
                "id = " + id + "," +
                "TIME = '" + time + "'," +
                "CURRENT = " + currentState + "," +
                "DETAIL = '" + detail + "'" +
                "WHERE _id = " + alarmId + " ; ");
        db.close();
    }

    public void updateAlarmCurrent(int alarmId, int currentState) {
        db = getWritableDatabase();
        db.execSQL(" UPDATE ALARM SET " +
                "CURRENT = " + currentState + "" +
                "WHERE _id = " + alarmId + " ; ");
        db.close();
    }

    public void deleteAlarm(int alarmId) {
        db = getWritableDatabase();
        db.execSQL("DELETE FROM ALARM WHERE _alarmId = " + alarmId + ";");
        db.close();
    }

    public ArrayList getAlarmValue() {
        db = getReadableDatabase();
        ArrayList<DataAlarm> result = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM ALARM;", null);
        while (cursor.moveToNext()) {
            result.add(new DataAlarm(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4)));
        }
        cursor.close();
        return result;
    }

    public int getAlarmSize(int id) {
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT _alarmId FROM ALARM WHERE id = " + id + ";", null);
        int result = cursor.getCount();
        return result;
    }

    public ArrayList<DataAlarm> getAlarmOnesValue(int id) {
        db = getReadableDatabase();
        ArrayList<DataAlarm> result = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM ALARM WHERE id = " + id + ";", null);
        while (cursor.moveToNext()) {
            result.add(new DataAlarm(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4)));
        }
        cursor.close();
        return result;
    }
}