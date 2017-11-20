package com.pepg.todolist;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.pepg.todolist.DataBase.dbManager;
import com.pepg.todolist.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by pengu on 2017-08-14.
 */

public class Manager {
    public final static int RC_LIST_TO_UPDATE = 1000;
    public final static int RC_LIST_TO_DETAIL = 1001;
    public final static int RC_DETAIL_TO_UPDATE = 1002;
    public final static int RC_LIST_TO_ADDGUIDE = 1003;
    public final static int RC_DETAIL_TO_DETAILITEM = 1005;
    public static boolean modifyMode;

    public static String[] strings;
    public static Calendar todayCal, readCal;
    public static long todayTime, ddayTime;
    public static int resultDday, totalDay;

    public static int getDrawableResId(String resName) {
        try {
            Field idField = R.drawable.class.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return getDrawableResId("ic_error");
        }
    }

    public static void controlKeyboard(boolean show, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (show) {
            imm.showSoftInput(activity.getCurrentFocus(), 0);
        } else {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static String todayDate() {
        todayCal = Calendar.getInstance();
        return todayCal.get(Calendar.YEAR) + "-" + (todayCal.get(Calendar.MONTH) + 1) + "-" + todayCal.get(Calendar.DATE);
    }

    public static int calculateDday(String date) {
        todayCal = Calendar.getInstance();
        readCal = Calendar.getInstance();
        try {
            strings = date.split("\u002D");
            readCal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
        } catch (Exception e) {
            dbManager.DATA_DDAY = 9999;
            return 9999;
        }
        todayCal.set(todayCal.get(Calendar.YEAR), todayCal.get(Calendar.MONTH) + 1, todayCal.get(Calendar.DATE));
        todayTime = todayCal.getTimeInMillis() / 86400000;
        ddayTime = readCal.getTimeInMillis() / 86400000;
        resultDday = (int) (ddayTime - todayTime);
        dbManager.DATA_DDAY = resultDday;
        return resultDday;
    }

    public static String getDday(String date) {
        int result = calculateDday(date);
        if (result >= 1000) {
            return "D-\u2026";
        } else if (result <= -1000) {
            return "D+\u2026";
        }
        if (result > 0) {
            return "D-" + result;
        } else if (result < 0) {
            return "D+" + (result * (-1));
        } else {
            return "D-DAY";
        }
    }

    public static int convertPixelsToDp(float px, Context context) {
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.getResources().getDisplayMetrics());
        return value;
    }

    public static int convertDpToPixels(int dp, Context context) {
        float value = (float) (dp / context.getResources().getDisplayMetrics().density);
        return (int) value;
    }



    public static int getSuggestAch(String createDate, String goalDate) {
        todayCal = Calendar.getInstance();
        readCal = Calendar.getInstance();
        try {
            strings = createDate.split("\u002D");
            todayCal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
            strings = goalDate.split("\u002D");
            readCal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
        } catch (Exception e) {
            return 0;
        }
        todayTime = todayCal.getTimeInMillis() / 86400000;
        ddayTime = readCal.getTimeInMillis() / 86400000;
        totalDay = (int) (ddayTime - todayTime);
        resultDday = calculateDday(goalDate);
        return 100 * (1 - (resultDday / totalDay));
    }

}
