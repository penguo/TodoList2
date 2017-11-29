package com.pepg.todolist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.transition.ChangeBounds;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.pepg.todolist.Adapter.SemiListRcvAdapter;
import com.pepg.todolist.DataBase.AlarmData;
import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.Fragment.SettingsFragment;

import static android.content.Context.ALARM_SERVICE;
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
    public static boolean isViewSubTitle;
    public static boolean isOnFastAdd;
    public static boolean notViewPastData;
    public static String addTimeType;


    public static String[] strings;
    public static Calendar todayCal, readCal, startCal;
    public static long todayTime, startTime, ddayTime;
    public static int resultDday, totalDay;

    public static void setSetting(Activity activity) {
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        isAnimationActive = mySharedPreferences.getBoolean(SettingsFragment.KEY_ANIMATION, true);
        isViewSubTitle = mySharedPreferences.getBoolean(SettingsFragment.KEY_ISVIEWSUBTITLE, true);
        isOnFastAdd = mySharedPreferences.getBoolean(SettingsFragment.KEY_FASTADD, true);
        notViewPastData = mySharedPreferences.getBoolean(SettingsFragment.KEY_NOTVIEWPASTDATA, false);
        addTimeType = mySharedPreferences.getString(SettingsFragment.KEY_ADDTIMETYPE, "");
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


    public static void alarmSet(Activity activity, DBManager dbManager) {
        ArrayList<AlarmData> list = dbManager.getAlarmValue();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCurrentState() == 0) {
                strings = list.get(i).getDate().split("\u002D");
                readCal.set(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]) - 1, Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), 0);

                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
                Intent intent = new Intent("com.pepg.alarm.ALARM_START");
                intent.putExtra("_id", list.get(i).getId());
                PendingIntent pIntent = PendingIntent.getBroadcast(activity, list.get(i).getAlarmId(), intent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, readCal.getTimeInMillis(), pIntent);
                list.get(i).setCurrentState(1);
            }
        }
    }

    public static void callSetTitleLayout(Activity activity, DBManager dbManager, int id, final TextView tvTitle) {
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout updateLayout = (LinearLayout) li.inflate(R.layout.update_edittext1, null);
        final EditText title = (EditText) updateLayout.findViewById(R.id.upitem_et);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog dialog;

        builder.setTitle("제목을 입력해주세요.");
        if (id == -1) {
            title.setText("");
        } else {
            dbManager.getValue("_id", id);
            title.setText(DBManager.DATA_TITLE);
        }
        builder.setView(updateLayout);

        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBManager.DATA_TITLE = title.getText().toString();
                tvTitle.setText(DBManager.DATA_TITLE);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Dialog
        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public static void callSemiLibraryAddLayout(Activity activity, final DBManager dbManager, final int parentId, final SemiListRcvAdapter semiListRcvAdapter) {
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout updateLayout = (LinearLayout) li.inflate(R.layout.update_edittext2, null);
        final EditText title = (EditText) updateLayout.findViewById(R.id.upitem2_et);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog dialog;

        builder.setTitle("세부적으로 할 일을 입력해주세요.");
        builder.setMessage("행바꿈으로 항목들이 분리됩니다.");
        title.setText("");
        builder.setView(updateLayout);

        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strings = title.getText().toString().split("\n");
                for (int i = 0; i < strings.length; i++) {
                    dbManager.semiInsert(parentId, strings[i], "");
                }
                semiListRcvAdapter.refresh();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Dialog
        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public static void callMemoLayout(Activity activity, final TextView tvMemo){
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout updateLayout = (LinearLayout) li.inflate(R.layout.update_edittext2, null);
        final EditText editText = (EditText) updateLayout.findViewById(R.id.upitem2_et);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog dialog;

        builder.setTitle("메모");
        editText.setText("");
        builder.setView(updateLayout);

        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBManager.DATA_MEMO = editText.getText().toString();
                tvMemo.setText(DBManager.DATA_MEMO);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Dialog
        dialog = builder.create(); //builder.show()를 create하여 dialog에 저장하는 방식.
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

}
