package com.pepg.todolist.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.pepg.todolist.Manager;
import com.pepg.todolist.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengu on 2017-08-10.
 */

public class DBManager extends SQLiteOpenHelper {

    SQLiteDatabase db, dbr;
    Cursor cursor;
    DBSortManager dbSortManager;
    Context context;

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public static int DATA_id, DATA_position, DATA_ACH, DATA_DDAY, DATA_ACH_FINISH, DATA_ACH_MAX;
    public static String DATA_TITLE, DATA_CATEGORY, DATA_DATE, DATA_CREATEDATE, DATA_MEMO;
    public static int DATA_semi_id, DATA_semi_position, DATA_semi_parentId, DATA_semi_ACH, DATA_semi_ACHMAX;
    public static String DATA_semi_TITLE, DATA_semi_MEMO;
    public static String DATA_SORTTYPE = "DEFAULT", DATA_SORTTYPEEQUAL = "";

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
                " CREATEDATE TEXT );");
        db.execSQL(" CREATE TABLE SEMITODO ( " +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " _position INTEGER DEFAULT -1, " +
                " _parentId INTEGER, " +
                " TITLE TEXT, " +
                " ACH INTEGER DEFAULT 0," +
                " ACHMAX INTEGER DEFAULT 100," +
                " MEMO TEXT );");
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
        db.execSQL(" DROP TABLE TODOLIST ");
        db.execSQL(" DROP TABLE SEMITODO ");
        db.execSQL(" DROP TABLE SETTING ");
        db.execSQL(" DROP TABLE ALARM ");
        onCreate(db);
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

    public void insert(String title, String category, String date, int ach, String memo) {
        if (title.equals("")) {
            title = context.getString(R.string.empty_data);
        }
        String createDate = Manager.todayDate();
        db = getWritableDatabase();
        db.execSQL(" INSERT INTO TODOLIST VALUES ( " +
                " null, " +
                " 0, " +
                "'" + title + "', " +
                "'" + category + "', " +
                "'" + date + "', " +
                ach + "," +
                "'" + memo + "'," +
                "'" + createDate + "');");
        setDummyData_Semi(title);
        db.close();
    }

    public void setDummyData_Semi(String title) {
        // _parentId == 0 인 더미데이터들을 해당 _parentId에 배분합니다.
        db = getWritableDatabase();
        dbr = getReadableDatabase();
        // getId
        cursor = dbr.rawQuery("SELECT _id FROM TODOLIST WHERE title = '" + title + "';", null);
        while (cursor.moveToNext()) {
            DATA_semi_parentId = cursor.getInt(0);
        }
        cursor = dbr.rawQuery("SELECT _id FROM SEMITODO WHERE _parentId = 0;", null);
        while (cursor.moveToNext()) {
            DATA_semi_id = cursor.getInt(0);
            db.execSQL(" UPDATE SEMITODO SET " +
                    "_parentId = '" + DATA_semi_parentId + "' " +
                    "WHERE _id = '" + DATA_semi_id + "'; ");
        }
    }

    public void deleteDummyData_Semi() {
        // _parentId == 0 인 더미데이터들을 삭제합니다.
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT _id FROM SEMITODO WHERE _parentId = 0;", null);
        while (cursor.moveToNext()) {
            semiDelete(cursor.getInt(0));
        }
    }

    public void insertSimply() {
        insert(DATA_TITLE, DATA_CATEGORY, DATA_DATE, DATA_ACH, DATA_MEMO);
    }

    public void update(int id, String title, String category, String date, String createDate, int ach, String memo) {
        if (title.equals("")) {
            title = context.getString(R.string.empty_data);
        }
        db = getWritableDatabase();
        db.execSQL(" UPDATE TODOLIST SET " +
                "TITLE = '" + title + "', " +
                "CATEGORY = '" + category + "', " +
                "DATE = '" + date + "', " +
                "ACH = " + ach + "," +
                "MEMO = '" + memo + "'," +
                "CREATEDATE = '" + createDate + "'" +
                "WHERE _id = " + id + " ; ");
        db.close();
    }

    public void updateSimply() {
        update(DATA_id, DATA_TITLE, DATA_CATEGORY, DATA_DATE, DATA_CREATEDATE, DATA_ACH, DATA_MEMO);
    }

    public void delete(int id) {
        db = getWritableDatabase();
        db.execSQL("DELETE FROM TODOLIST WHERE _id = '" + id + "';");
        db.execSQL("DELETE FROM SEMITODO WHERE _parentId = '" + id + "';");
        db.close();
    }

    public void getValue(String where, int equal) {
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM TODOLIST WHERE " + where + " = '" + equal + "';", null);
        while (cursor.moveToNext()) {
            DATA_id = cursor.getInt(0);
            DATA_position = cursor.getInt(1);
            DATA_TITLE = cursor.getString(2);
            DATA_CATEGORY = cursor.getString(3);
            DATA_DATE = cursor.getString(4);
            DATA_MEMO = cursor.getString(6);
            DATA_CREATEDATE = cursor.getString(7);
        }
        DATA_DDAY = Manager.calculateDday(DBManager.DATA_DATE);
        cursor = db.rawQuery("SELECT ACH, ACHMAX FROM SEMITODO WHERE _parentId = " + DATA_id + " ;", null);
        if (cursor.getCount() == 0) // Semi 데이터가 없을 경우 - 직접 설정한 퍼센트로 적용.
        {
            try {
                DATA_ACH_FINISH = 0;
                DATA_ACH_MAX = 0;
                DATA_ACH = cursor.getInt(5);
            } catch (Exception e) {
                DATA_ACH_FINISH = 0;
                DATA_ACH_MAX = 0;
                DATA_ACH = 0;
            }
        } else { // Semi 데이터가 하나 이상 있을 경우 - Semi의 완수도에 따라 적용.
            int totalAch = 0, totalAchMax = 0;
            while (cursor.moveToNext()) {
                totalAch += cursor.getInt(0);
                totalAchMax += cursor.getInt(1);
            }
            DATA_ACH_FINISH = totalAch;
            DATA_ACH_MAX = totalAchMax;
            DATA_ACH = (totalAch * 100) / totalAchMax;
        }
        cursor.close();
    }

    public void setPosition() {
        db = getWritableDatabase();
        dbr = getReadableDatabase();
        db.execSQL("UPDATE TODOLIST SET _position = -1;");
        dbSortManager = new DBSortManager(this, context, db, dbr);
        switch (DATA_SORTTYPE) {
            case ("DEFAULT"):
                dbSortManager.sort();
                break;
            case ("CATEGORY"):
                dbSortManager.sortByCategory(DATA_SORTTYPEEQUAL);
                break;
        }
        dbSortManager.sortByDate();
        if (Manager.isViewSubTitle) {
            dbSortManager.setSubtitlePosition();
        }
    }

    public void setSemiPosition(int parentId) {
        db = getWritableDatabase();
        dbr = getReadableDatabase();
        db.execSQL("UPDATE SEMITODO SET _position = -1;");
        dbSortManager = new DBSortManager(this, context, db, dbr);
        dbSortManager.sortSemiByAch(parentId);
    }

    public void semiInsert(int parentId, String title, String memo) {
        if (title.equals("")) {
            title = context.getString(R.string.empty_data);
        }
        db = getWritableDatabase();
        db.execSQL(" INSERT INTO SEMITODO VALUES ( " +
                "null, " +
                "null, " +
                parentId + ", " +
                "'" + title + "', " +
                "0," +
                "100," +
                "'" + memo + "');");
        setSemiPosition(parentId);
        db.close();
    }

    public void semiUpdate(int id, String title, int ach, int achmax, String memo) {
        if (title.equals("")) {
            title = context.getString(R.string.empty_data);
        }
        db = getWritableDatabase();
        db.execSQL(" UPDATE SEMITODO SET " +
                "TITLE = '" + title + "', " +
                "ACH = '" + ach + "'," +
                "ACHMAX = '" + achmax + "'," +
                "MEMO = '" + memo + "'" +
                "WHERE _id = " + id + "; ");
    }

    public void semiUpdate(int id, String title, String memo) {
        if (title.equals("")) {
            title = context.getString(R.string.empty_data);
        }
        db = getWritableDatabase();
        db.execSQL(" UPDATE SEMITODO SET " +
                "TITLE = '" + title + "', " +
                "MEMO = '" + memo + "'" +
                "WHERE _id = " + id + "; ");
    }

    public void semiUpdateSimply() {
        semiUpdate(DATA_semi_id, DATA_semi_TITLE, DATA_semi_ACH, DATA_semi_ACHMAX, DATA_semi_MEMO);
    }

    public void semiDelete(int id) {
        db = getWritableDatabase();
        db.execSQL("DELETE FROM SEMITODO WHERE _id = '" + id + "';");
        db.close();
    }

    public void getSemiValue(String where, int equal) {
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM SEMITODO WHERE " + where + " = '" + equal + "';", null);
        while (cursor.moveToNext()) {
            DATA_semi_id = cursor.getInt(0);
            DATA_semi_position = cursor.getInt(1);
            DATA_semi_parentId = cursor.getInt(2);
            DATA_semi_TITLE = cursor.getString(3);
            DATA_semi_MEMO = cursor.getString(6);
            try {
                DATA_semi_ACH = cursor.getInt(4);
            } catch (Exception e) {
                DATA_semi_ACH = 0;
            }
            try {
                DATA_semi_ACHMAX = cursor.getInt(5);
            } catch (Exception e) {
                DATA_semi_ACHMAX = 0;
            }
        }
        cursor.close();
    }

    public void resetPublicData() {
        DATA_TITLE = "";
        DATA_CATEGORY = "";
        DATA_DATE = "";
        DATA_MEMO = "";
        DATA_semi_TITLE = "";
        DATA_semi_MEMO = "";
    }

    public int getSize() {
        db = getReadableDatabase();
        switch (DATA_SORTTYPE) {
            case ("DEFAULT"):
                cursor = db.rawQuery("SELECT _id FROM TODOLIST;", null);
                break;
            case ("CATEGORY"):
                cursor = db.rawQuery("SELECT _id FROM TODOLIST WHERE CATEGORY = '" + DATA_SORTTYPEEQUAL + "';", null);
                break;
        }
        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    public int getSemiSize(int parentId) {
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT _id FROM SEMITODO WHERE _parentId = " + parentId + " ;", null);
        int result = cursor.getCount();
        cursor.close();
        return result;
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
                "CURRENT = " + currentState +"," +
                "DETAIL = '" + detail + "'" +
                "WHERE _id = " + alarmId + " ; ");
        db.close();
    }

    public void updateAlarmCurrent(int alarmId, int currentState){
        db = getWritableDatabase();
        db.execSQL(" UPDATE ALARM SET " +
                "CURRENT = " + currentState +"" +
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
        ArrayList<AlarmData> result = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM ALARM;", null);
        while (cursor.moveToNext()) {
            result.add(new AlarmData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4)));
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

    public ArrayList<AlarmData> getAlarmOnesValue(int id) {
        db = getReadableDatabase();
        ArrayList<AlarmData> result = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM ALARM WHERE id = " + id + ";", null);
        while (cursor.moveToNext()) {
            result.add(new AlarmData(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4)));
        }
        cursor.close();
        return result;
    }
}