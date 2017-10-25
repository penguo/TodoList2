package com.pepg.todolist;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

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
    public final static int RC_DETAIL = 1004;

    public static int getDrawableResId(String resName) {
        try {
            Field idField = R.drawable.class.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return getDrawableResId("ic_error");
        }
    }

    public static void controlKeyboard(boolean show, Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if(show){
            imm.showSoftInput(activity.getCurrentFocus(), 0);
        }else{
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
