package com.pepg.todolist;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.transition.ChangeBounds;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.util.Calendar;

import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.Fragment.SettingsFragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by pengu on 2017-08-14.
 */

public class Manager {
    public final static int RC_LIST_TO_UPDATE = 1000;
    public final static int RC_LIST_TO_INFO = 1001;
    public final static int RC_DETAIL_TO_UPDATE = 1002;
    public final static int RC_LIST_TO_ADDGUIDE = 1003;
    public final static int RC_LIST_TO_SETTINGS = 1006;
    public static boolean editMode;
    public static int viewState;

    // setting option
    public static boolean isAnimationActive;
    public static int sorttype;
    public static boolean isViewSubTitle;

    public static String[] strings;
    public static Calendar todayCal, readCal, startCal;
    public static long todayTime, startTime, ddayTime;
    public static int resultDday, totalDay;

    public static void setSetting(Activity activity) {
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        isAnimationActive = mySharedPreferences.getBoolean(SettingsFragment.KEY_ANIMATION, true);
//        sorttype = mySharedPreferences.getInt(SettingsFragment.KEY_SORTLIST, 1);
        isViewSubTitle = mySharedPreferences.getBoolean(SettingsFragment.KEY_ISVIEWSUBTITLE, true);
    }

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
            readCal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
        } catch (Exception e) {
            DBManager.DATA_DDAY = 9999;
            return 9999;
        }
        todayCal.set(todayCal.get(Calendar.YEAR), todayCal.get(Calendar.MONTH), todayCal.get(Calendar.DATE));
        todayTime = todayCal.getTimeInMillis() / 86400000;
        ddayTime = readCal.getTimeInMillis() / 86400000;

        resultDday = (int) (ddayTime - todayTime);
        DBManager.DATA_DDAY = resultDday;
        return resultDday;
    }

    public static boolean calculateisStart(String startDate) {
        startCal = Calendar.getInstance();
        todayCal = Calendar.getInstance();
        try {
            strings = startDate.split("\u002D");
            startCal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]));
        } catch (Exception e) {
            DBManager.DATA_DDAY = 9999;
            return false;
        }
        todayCal.set(todayCal.get(Calendar.YEAR), todayCal.get(Calendar.MONTH), todayCal.get(Calendar.DATE));
        todayTime = todayCal.getTimeInMillis() / 86400000;
        startTime = startCal.getTimeInMillis() / 86400000;

        if (((int) (startTime - todayTime)) > 0) {
            return false;
        } else {
            return true;
        }
    }

    public static String getDdayString(int result) {
        if (result == 9999) {
            return "\u221E";
        }
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
        startTime = todayCal.getTimeInMillis() / 86400000;
        ddayTime = readCal.getTimeInMillis() / 86400000;
        totalDay = (int) (ddayTime - startTime);
        resultDday = calculateDday(goalDate);
        int result;
        try {
            result = (100 - ((100 * resultDday) / (1 * totalDay)));
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    public static ChangeBounds getChangeBounds() {
        ChangeBounds changeBoundsTransition = new ChangeBounds();
        changeBoundsTransition.setDuration(250);
        return changeBoundsTransition;
    }


    public static void notificationSomethings(Context context, Resources res, int id) {

        Intent notificationIntent = new Intent(context, InfoActivity.class);
        notificationIntent.putExtra("notificationId", id); //전달할 값
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentTitle("상태바 드래그시 보이는 타이틀")
                .setContentText("상태바 드래그시 보이는 서브타이틀")
                .setTicker("상태바 한줄 메시지")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }

    public static void notificationInfo(Context context, Resources res, int id, DBManager dbManager) {

        dbManager.getValue("_id", id);

        Intent notificationIntent = new Intent(context, InfoActivity.class);
        notificationIntent.putExtra("notificationId", DBManager.DATA_id); //전달할 값
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentTitle(DBManager.DATA_CATEGORY + " - " + DBManager.DATA_TITLE)
                .setContentText(getDdayString(DBManager.DATA_DDAY) + ", " + DBManager.DATA_ACH + "% 완료.")
                .setTicker("TODO: " + DBManager.DATA_TITLE + " 알림")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher_todo))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
        style.setSummaryText("자세한 내용 보기 +");
        style.setBigContentTitle(DBManager.DATA_CATEGORY + " - " + DBManager.DATA_TITLE);
        style.bigText(calculateDday(DBManager.DATA_DATE) + "일 남았습니다. 현재 " + DBManager.DATA_ACH + "% 완료하셨습니다.");

        builder.setStyle(style);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }
}
