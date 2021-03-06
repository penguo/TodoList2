package com.pepg.todolist.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pepg.todolist.Manager;
import com.pepg.todolist.R;
import com.pepg.todolist.SettingsActivity;


/**
 * Created by pengu on 2017-11-25.
 */

public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_ANIMATION = "pref_animation";
    public static final String KEY_ISVIEWSUBTITLE = "pref_isviewsubtitle";
    public static final String KEY_FASTADD = "pref_fastadd";
    public static final String KEY_NOTVIEWPASTDATA = "pref_notviewpastdata";
    public static final String KEY_ADDTIMETYPE = "pref_addtimetype";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

}