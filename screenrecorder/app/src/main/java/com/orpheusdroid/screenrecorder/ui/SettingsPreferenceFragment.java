/*
 * Copyright (c) 2016-2018. Vijai Chandra Prasad R.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses
 */

package com.orpheusdroid.screenrecorder.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.folderpicker.FolderChooser;
import com.orpheusdroid.screenrecorder.folderpicker.OnDirectorySelectedListerner;
import com.orpheusdroid.screenrecorder.interfaces.PermissionResultListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import androidx.annotation.NonNull;

/**
 * <p>
 *     This fragment handles various settings of the recorder.
 * </p>
 *
 * @author Vijai Chandra Prasad .R
 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener
 * @see PermissionResultListener
 * @see OnDirectorySelectedListerner
 * @see MainActivity.AnalyticsSettingsListerner
 */
public class SettingsPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
        , PermissionResultListener, OnDirectorySelectedListerner, MainActivity.AnalyticsSettingsListerner {

    /**
     * SharedPreferences object to read the persisted settings
     */
    SharedPreferences prefs;

    /**
     * ListPreference to choose the recording resolution
     */
    private ListPreference res;

    /**
     * ListPreference to manage audio recording via mic setting
     */
    private ListPreference recaudio;

    /**
     * CheckBoxPreference to manage onscreen floating control setting
     */
    private CheckBoxPreference floatingControl;

    /**
     * CheckBoxPreference to manage crash reporting via countly setting
     */
    private CheckBoxPreference crashReporting;

    /**
     * CheckBoxPreference to manage full analytics via countly setting
     */
    private CheckBoxPreference usageStats;

    /**
     * FolderChooser object to choose the directory where the video has to be saved to.
     */
    private FolderChooser dirChooser;

    /**
     * SwitchPreference for camera to show overlay
     */
    private CheckBoxPreference cameraOverlay;

    /**
     * MainActivity object
     */
    private MainActivity activity;

    {com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onRequestPermissionsResult(int,[@NonNullString,[@NonNullint)",this,requestCode,permissions,grantResults);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onRequestPermissionsResult(int,[@NonNullString,[@NonNullint)",this);}

    /**
     * Initialize various listeners and settings preferences.
     *
     * @param savedInstanceState default savedInstance bundle sent by Android runtime
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        /*//init permission listener callback*/
        setPermissionListener();

        setAnalyticsPermissionListerner();

        /*//Get Default save location from shared preference*/
        String defaultSaveLoc = (new File(Environment
                .getExternalStorageDirectory() + File.separator + Const.APPDIR)).getPath();

        /*//Get instances of all preferences*/
        prefs = getPreferenceScreen().getSharedPreferences();
        res = (ListPreference) findPreference(getString(R.string.res_key));
        ListPreference fps = (ListPreference) findPreference(getString(R.string.fps_key));
        ListPreference bitrate = (ListPreference) findPreference(getString(R.string.bitrate_key));
        recaudio = (ListPreference) findPreference(getString(R.string.audiorec_key));
        ListPreference filenameFormat = (ListPreference) findPreference(getString(R.string.filename_key));
        EditTextPreference filenamePrefix = (EditTextPreference) findPreference(getString(R.string.fileprefix_key));
        dirChooser = (FolderChooser) findPreference(getString(R.string.savelocation_key));
        floatingControl = (CheckBoxPreference) findPreference(getString(R.string.preference_floating_control_key));
        CheckBoxPreference touchPointer = (CheckBoxPreference) findPreference("touch_pointer");
        crashReporting = (CheckBoxPreference) findPreference(getString(R.string.preference_crash_reporting_key));
        usageStats = (CheckBoxPreference) findPreference(getString(R.string.preference_anonymous_statistics_key));
        /*//Set previously chosen directory as initial directory*/
        dirChooser.setCurrentDir(getValue(getString(R.string.savelocation_key), defaultSaveLoc));
        cameraOverlay = (CheckBoxPreference) findPreference(getString(R.string.preference_camera_overlay_key));

        ListPreference orientation = (ListPreference) findPreference(getString(R.string.orientation_key));
        orientation.setSummary(orientation.getEntry());

        ListPreference theme = (ListPreference) findPreference(getString(R.string.preference_theme_key));
        theme.setSummary(theme.getEntry());

        /*//Set the summary of preferences dynamically with user choice or default if no user choice is made*/
        updateResolution(res);
        updateScreenAspectRatio();
        fps.setSummary(getValue(getString(R.string.fps_key), "30"));
        float bps = bitsToMb(Integer.parseInt(getValue(getString(R.string.bitrate_key), "7130317")));
        bitrate.setSummary(bps + " Mbps");
        dirChooser.setSummary(getValue(getString(R.string.savelocation_key), defaultSaveLoc));
        filenameFormat.setSummary(getFileSaveFormat());
        filenamePrefix.setSummary(getValue(getString(R.string.fileprefix_key), "recording"));

        checkAudioRecPermission();

        /*//If floating controls is checked, check for system windows permission*/
        if (floatingControl.isChecked())
            {requestSystemWindowsPermission(Const.FLOATING_CONTROLS_SYSTEM_WINDOWS_CODE);}

        if (cameraOverlay.isChecked()) {
            requestCameraPermission();
            requestSystemWindowsPermission(Const.CAMERA_SYSTEM_WINDOWS_CODE);
        }

        if(touchPointer.isChecked()){
            if (!hasPluginInstalled())
                {touchPointer.setChecked(false);}
        }

        /*//set callback for directory change*/
        dirChooser.setOnDirectoryClickedListerner(this);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void checkAudioRecPermission() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.checkAudioRecPermission()",this);try{String value = recaudio.getValue();
        switch (value) {
            case "1":
                requestAudioPermission(Const.AUDIO_REQUEST_CODE);
                break;
            case "2":
                requestAudioPermission(Const.INTERNAL_AUDIO_REQUEST_CODE);
                break;
        }
        recaudio.setSummary(recaudio.getEntry());com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.checkAudioRecPermission()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.checkAudioRecPermission()",this,throwable);throw throwable;}
    }

    /**
     * Updates the summary of resolution settings preference
     *
     * @param pref object of the resolution ListPreference
     */
    private void updateResolution(ListPreference pref) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.updateResolution(android.preference.ListPreference)",this,pref);try{String resolution = getValue(getString(R.string.res_key), getNativeRes());
        if (resolution.toLowerCase().contains("x")) {
            resolution = getNativeRes();
            pref.setValue(resolution);
        }
        pref.setSummary(resolution + "P");com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.updateResolution(android.preference.ListPreference)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.updateResolution(android.preference.ListPreference)",this,throwable);throw throwable;}
    }

    /**
     * Method to get the device's native resolution
     *
     * @return device resolution
     */
    private String getNativeRes() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getNativeRes()",this);try{DisplayMetrics metrics = getRealDisplayMetrics();
        {com.mijack.Xlog.logMethodExit("java.lang.String com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getNativeRes()",this);return String.valueOf(getScreenWidth(metrics));}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getNativeRes()",this,throwable);throw throwable;}
    }

    /**
     * Updates the available resolution based on aspect ratio
     */
    private void updateScreenAspectRatio() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.updateScreenAspectRatio()",this);try{CharSequence[] entriesValues = getResolutionEntriesValues();
        res.setEntries(getResolutionEntries(entriesValues));
        /*//res.setEntries(entriesValues);*/
        res.setEntryValues(entriesValues);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.updateScreenAspectRatio()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.updateScreenAspectRatio()",this,throwable);throw throwable;}
    }

    /**
     * Get resolutions based on the device's aspect ratio
     *
     * @return entries for the resolution
     */
    private CharSequence[] getResolutionEntriesValues() {

        com.mijack.Xlog.logMethodEnter("[java.lang.CharSequence com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getResolutionEntriesValues()",this);try{ArrayList<String> entrieValues = buildEntries(R.array.resolutionValues);

        String[] entriesArray = new String[entrieValues.size()];
        {com.mijack.Xlog.logMethodExit("[java.lang.CharSequence com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getResolutionEntriesValues()",this);return entrieValues.toArray(entriesArray);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.lang.CharSequence com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getResolutionEntriesValues()",this,throwable);throw throwable;}
    }

    private CharSequence[] getResolutionEntries(CharSequence[] entriesValues) {
        com.mijack.Xlog.logMethodEnter("[java.lang.CharSequence com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getResolutionEntries([java.lang.CharSequence)",this,entriesValues);try{ArrayList<String> entries = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.resolutionsArray)));
        ArrayList<String> newEntries = new ArrayList<>();
        for (CharSequence values : entriesValues) {
            Log.d(Const.TAG, "res entries:" + values.toString());
            for (String entry : entries) {
                if (entry.contains(values))
                    {newEntries.add(entry);}
            }
            Log.d(Const.TAG, "res entries: split " + values.toString().split("P")[0] + " val: ");
        }
        Log.d(Const.TAG, "res entries" + newEntries.toString());
        String[] entriesArray = new String[newEntries.size()];
        {com.mijack.Xlog.logMethodExit("[java.lang.CharSequence com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getResolutionEntries([java.lang.CharSequence)",this);return newEntries.toArray(entriesArray);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[java.lang.CharSequence com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getResolutionEntries([java.lang.CharSequence)",this,throwable);throw throwable;}
    }

    /**
     * Build resolutions from the arrays.
     *
     * @param resID resource ID for the resolution array
     * @return ArrayList of available resolutions
     */
    private ArrayList<String> buildEntries(int resID) {
        com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.buildEntries(int)",this,resID);try{DisplayMetrics metrics = getRealDisplayMetrics();
        int deviceWidth = getScreenWidth(metrics);
        ArrayList<String> entries = new ArrayList<>(Arrays.asList(getResources().getStringArray(resID)));
        Iterator<String> entriesIterator = entries.iterator();
        while (entriesIterator.hasNext()) {
            String width = entriesIterator.next();
            if (deviceWidth < Integer.parseInt(width)) {
                entriesIterator.remove();
            }
        }
        if (!entries.contains("" + deviceWidth))
            {entries.add("" + deviceWidth);}
        {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.buildEntries(int)",this);return entries;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.buildEntries(int)",this,throwable);throw throwable;}
    }


    /**
     * Returns object of DisplayMetrics
     *
     * @return DisplayMetrics
     */
    private DisplayMetrics getRealDisplayMetrics(){
        com.mijack.Xlog.logMethodEnter("android.util.DisplayMetrics com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getRealDisplayMetrics()",this);try{DisplayMetrics metrics = new DisplayMetrics();
        WindowManager window = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        window.getDefaultDisplay().getRealMetrics(metrics);
        {com.mijack.Xlog.logMethodExit("android.util.DisplayMetrics com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getRealDisplayMetrics()",this);return metrics;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.util.DisplayMetrics com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getRealDisplayMetrics()",this,throwable);throw throwable;}
    }


    /**
     * Get width of screen in pixels
     *
     * @return screen width
     */
    private int getScreenWidth(DisplayMetrics metrics) {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getScreenWidth(android.util.DisplayMetrics)",this,metrics);try{com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getScreenWidth(android.util.DisplayMetrics)",this);return metrics.widthPixels;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getScreenWidth(android.util.DisplayMetrics)",this,throwable);throw throwable;}
    }

    /**
     * Get height of screen in pixels
     *
     * @return Screen height
     */
    private int getScreenHeight(DisplayMetrics metrics) {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getScreenHeight(android.util.DisplayMetrics)",this,metrics);try{com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getScreenHeight(android.util.DisplayMetrics)",this);return metrics.heightPixels;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getScreenHeight(android.util.DisplayMetrics)",this,throwable);throw throwable;}
    }


    /**
     * Get aspect ratio of the screen
     */
    @Deprecated
    private Const.ASPECT_RATIO getAspectRatio() {
        com.mijack.Xlog.logMethodEnter("Const.ASPECT_RATIO com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getAspectRatio()",this);try{float screen_width = getScreenWidth(getRealDisplayMetrics());
        float screen_height = getScreenHeight(getRealDisplayMetrics());
        float aspectRatio;
        if (screen_width > screen_height) {
            aspectRatio = screen_width / screen_height;
        } else {
            aspectRatio = screen_height / screen_width;
        }
        {com.mijack.Xlog.logMethodExit("Const.ASPECT_RATIO com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getAspectRatio()",this);return Const.ASPECT_RATIO.valueOf(aspectRatio);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("Const.ASPECT_RATIO com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getAspectRatio()",this,throwable);throw throwable;}
    }

    /**
     * Set permission listener in the {@link MainActivity} to handle permission results
     */
    private void setPermissionListener() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.setPermissionListener()",this);try{if (getActivity() != null && getActivity() instanceof MainActivity) {
            activity = (MainActivity) getActivity();
            activity.setPermissionResultListener(this);
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.setPermissionListener()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.setPermissionListener()",this,throwable);throw throwable;}
    }

    /**
     * Set Analytics permission listener in {@link MainActivity} to listen to analytics permission changes
     */
    private void setAnalyticsPermissionListerner(){
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.setAnalyticsPermissionListerner()",this);try{if (getActivity() != null && getActivity() instanceof MainActivity) {
            activity = (MainActivity) getActivity();
            activity.setAnalyticsSettingsListerner(this);
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.setAnalyticsPermissionListerner()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.setAnalyticsPermissionListerner()",this,throwable);throw throwable;}
    }

    /**
     * Get the persisted value for the preference from default sharedPreference
     *
     * @param key String represnting the sharedpreference key to fetch
     * @param defVal String Default value if the preference does not exist
     * @return String the persisted preference value or default if not found
     */
    private String getValue(String key, String defVal) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getValue(java.lang.String,java.lang.String)",this,key,defVal);try{com.mijack.Xlog.logMethodExit("java.lang.String com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getValue(java.lang.String,java.lang.String)",this);return prefs.getString(key, defVal);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getValue(java.lang.String,java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * Method to convert bits per second to MB/s
     *
     * @param bps float bitsPerSecond
     * @return float
     */
    private float bitsToMb(float bps) {
        com.mijack.Xlog.logMethodEnter("float com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.bitsToMb(float)",this,bps);try{com.mijack.Xlog.logMethodExit("float com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.bitsToMb(float)",this);return bps / (1024 * 1024);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("float com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.bitsToMb(float)",this,throwable);throw throwable;}
    }

    /*//Register for OnSharedPreferenceChangeListener when the fragment resumes*/
    @Override
    public void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onResume()",this);try{super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onResume()",this,throwable);throw throwable;}
    }

    /*//Unregister for OnSharedPreferenceChangeListener when the fragment pauses*/
    @Override
    public void onPause() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onPause()",this);try{super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onPause()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onPause()",this,throwable);throw throwable;}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onActivityResult(int,int,android.content.Intent)",this,requestCode,resultCode,data);try{super.onActivityResult(requestCode, resultCode, data);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onActivityResult(int,int,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onActivityResult(int,int,android.content.Intent)",this,throwable);throw throwable;}
    }

    /*//When user changes preferences, update the summary accordingly*/
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onSharedPreferenceChanged(android.content.SharedPreferences,java.lang.String)",this,sharedPreferences,s);try{Preference pref = findPreference(s);
        if (pref == null) {{com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onSharedPreferenceChanged(android.content.SharedPreferences,java.lang.String)",this);return;}}
        switch (pref.getTitleRes()) {
            case R.string.preference_resolution_title:
                updateResolution((ListPreference) pref);
                break;
            case R.string.preference_fps_title:
                String fps = String.valueOf(getValue(getString(R.string.fps_key), "30"));
                pref.setSummary(fps);
                break;
            case R.string.preference_bit_title:
                float bps = bitsToMb(Integer.parseInt(getValue(getString(R.string.bitrate_key), "7130317")));
                pref.setSummary(bps + " Mbps");
                if (bps > 12)
                    {Toast.makeText(getActivity(), R.string.toast_message_bitrate_high_warning, Toast.LENGTH_SHORT).show();}
                break;
            case R.string.preference_filename_format_title:
                pref.setSummary(getFileSaveFormat());
                break;
            case R.string.preference_audio_record_title:
                switch (recaudio.getValue()) {
                    case "1":
                        requestAudioPermission(Const.AUDIO_REQUEST_CODE);
                        break;
                    case "2":
                        if (!prefs.getBoolean(Const.PREFS_INTERNAL_AUDIO_DIALOG_KEY, false))
                            {showInternalAudioWarning(false);}
                        else
                            {requestAudioPermission(Const.INTERNAL_AUDIO_REQUEST_CODE);}
                        break;
                    case "3":
                        if (!prefs.getBoolean(Const.PREFS_INTERNAL_AUDIO_DIALOG_KEY, false))
                            {showInternalAudioWarning(true);}
                        else
                            {requestAudioPermission(Const.INTERNAL_R_SUBMIX_AUDIO_REQUEST_CODE);}
                        break;
                    default:
                        recaudio.setValue("0");
                        break;
                }
                pref.setSummary(((ListPreference) pref).getEntry());
                break;
            case R.string.preference_filename_prefix_title:
                EditTextPreference etp = (EditTextPreference) pref;
                etp.setSummary(etp.getText());
                ListPreference filename = (ListPreference) findPreference(getString(R.string.filename_key));
                filename.setSummary(getFileSaveFormat());
                break;
            case R.string.preference_floating_control_title:
                requestSystemWindowsPermission(Const.FLOATING_CONTROLS_SYSTEM_WINDOWS_CODE);
                break;
            case R.string.preference_show_touch_title:
                CheckBoxPreference showTouchCB = (CheckBoxPreference)pref;
                if (showTouchCB.isChecked() && !hasPluginInstalled()){
                    showTouchCB.setChecked(false);
                    showDownloadAlert();
                }
                break;
            case R.string.preference_crash_reporting_title:
                CheckBoxPreference crashReporting = (CheckBoxPreference)pref;
                CheckBoxPreference anonymousStats = (CheckBoxPreference) findPreference(getString(R.string.preference_anonymous_statistics_key));
                if(!crashReporting.isChecked())
                    {anonymousStats.setChecked(false);}
            case R.string.preference_anonymous_statistics_title:
                Toast.makeText(getActivity(), R.string.toast_message_countly_activity_restart, Toast.LENGTH_SHORT).show();
                activity.recreate();
                break;
            case R.string.preference_theme_title:
                activity.recreate();
                break;
            case R.string.preference_orientation_title:
                pref.setSummary(((ListPreference) pref).getEntry());
                break;
            case R.string.preference_camera_overlay_title:
                requestCameraPermission();
                break;
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onSharedPreferenceChanged(android.content.SharedPreferences,java.lang.String)",this,throwable);throw throwable;}
    }

    /**
     * show an alert to download the plugin when the plugin is not found
     */
    private void showDownloadAlert() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showDownloadAlert()",this);try{new AlertDialog.Builder(getActivity())
                .setTitle(R.string.alert_plugin_not_found_title)
                .setMessage(R.string.alert_plugin_not_found_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$1.onClick(android.content.DialogInterface,int)",this,dialogInterface,i);try{try {
                            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.orpheusdroid.screencamplugin")));
                        } catch (android.content.ActivityNotFoundException e) { /*// if there is no Google Play on device*/
                            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.orpheusdroid.screencamplugin")));
                        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$1.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$1.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })
                .setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    {com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$2.onClick(android.content.DialogInterface,int)",this,dialogInterface,i);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$2.onClick(android.content.DialogInterface,int)",this);}
                })
                .create().show();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showDownloadAlert()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showDownloadAlert()",this,throwable);throw throwable;}
    }

    private void showInternalAudioWarning(boolean isR_submix) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showInternalAudioWarning(boolean)",this,isR_submix);try{int message;
        final int requestCode;
        if (isR_submix) {
            message = R.string.alert_dialog_r_submix_audio_warning_message;
            requestCode = Const.INTERNAL_R_SUBMIX_AUDIO_REQUEST_CODE;
        } else {
            message = R.string.alert_dialog_internal_audio_warning_message;
            requestCode = Const.INTERNAL_AUDIO_REQUEST_CODE;
        }
        new AlertDialog.Builder(activity)
                .setTitle(R.string.alert_dialog_internal_audio_warning_title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$3.onClick(android.content.DialogInterface,int)",this,dialogInterface,i);try{requestAudioPermission(requestCode);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$3.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$3.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}

                    }
                })
                .setNegativeButton(R.string.alert_dialog_internal_audio_warning_negative_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$4.onClick(android.content.DialogInterface,int)",this,dialogInterface,i);try{prefs.edit().putBoolean(Const.PREFS_INTERNAL_AUDIO_DIALOG_KEY, true)
                                .apply();
                        requestAudioPermission(Const.INTERNAL_AUDIO_REQUEST_CODE);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$4.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$4.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })
                .create()
                .show();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showInternalAudioWarning(boolean)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showInternalAudioWarning(boolean)",this,throwable);throw throwable;}
    }
    /**
     * Check if "show touches" plugin is installed.
     *
     * @return boolean
     */
    private boolean hasPluginInstalled(){
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.hasPluginInstalled()",this);try{PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo("com.orpheusdroid.screencamplugin",PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(Const.TAG, "Plugin not installed");
            {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.hasPluginInstalled()",this);return false;}
        }
        {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.hasPluginInstalled()",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.hasPluginInstalled()",this,throwable);throw throwable;}
    }

    /**
     * Method to concat file prefix with dateTime format
     */
    public String getFileSaveFormat() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getFileSaveFormat()",this);try{String filename = prefs.getString(getString(R.string.filename_key), "yyyyMMdd_hhmmss");
        String prefix = prefs.getString(getString(R.string.fileprefix_key), "recording");
        {com.mijack.Xlog.logMethodExit("java.lang.String com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getFileSaveFormat()",this);return prefix + "_" + filename;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.getFileSaveFormat()",this,throwable);throw throwable;}
    }

    /**
     * Method to request android permission to record audio
     */
    public void requestAudioPermission(int requestCode) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.requestAudioPermission(int)",this,requestCode);try{if (activity != null) {
            activity.requestPermissionAudio(requestCode);
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.requestAudioPermission(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.requestAudioPermission(int)",this,throwable);throw throwable;}
    }

    /**
     * Method to request Camera permission
     */
    public void requestCameraPermission() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.requestCameraPermission()",this);try{if (activity != null)
            {activity.requestPermissionCamera();}com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.requestCameraPermission()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.requestCameraPermission()",this,throwable);throw throwable;}
    }

    /**
     * Method to request android system windows permission to show floating controls
     * <p>
     *     Shown only on devices above api 23 (Marshmallow)
     * </p>
     */
    private void requestSystemWindowsPermission(int code) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.requestSystemWindowsPermission(int)",this,code);try{if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestSystemWindowsPermission(code);
        } else {
            Log.d(Const.TAG, "API is < 23");
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.requestSystemWindowsPermission(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.requestSystemWindowsPermission(int)",this,throwable);throw throwable;}
    }

    /**
     * Show snackbar with permission Intent when the user rejects write storage permission
     */
    private void showSnackbar() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showSnackbar()",this);try{Snackbar.make(getActivity().findViewById(R.id.fab), R.string.snackbar_storage_permission_message,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.snackbar_storage_permission_action_enable,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$5.onClick(android.view.View)",this,v);try{if (activity != null){
                            activity.requestPermissionStorage();
                        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$5.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$5.onClick(android.view.View)",this,throwable);throw throwable;}
                    }
                }).show();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showSnackbar()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showSnackbar()",this,throwable);throw throwable;}
    }

    /**
     * Show a dialog when the permission to storage is denied by the user during startup
     */
    private void showPermissionDeniedDialog(){
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showPermissionDeniedDialog()",this);try{new AlertDialog.Builder(activity)
                .setTitle(R.string.alert_permission_denied_title)
                .setMessage(R.string.alert_permission_denied_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$6.onClick(android.content.DialogInterface,int)",this,dialogInterface,i);try{if (activity != null){
                            activity.requestPermissionStorage();
                        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$6.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$6.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$7.onClick(android.content.DialogInterface,int)",this,dialogInterface,i);try{showSnackbar();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$7.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment$7.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setCancelable(false)
                .create().show();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showPermissionDeniedDialog()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.showPermissionDeniedDialog()",this,throwable);throw throwable;}
    }

    /*//Permission result callback to process the result of Marshmallow style permission request*/
    @Override
    public void onPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onPermissionResult(int,[java.lang.String,[int)",this,requestCode,permissions,grantResults);try{switch (requestCode) {
            case Const.EXTDIR_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    Log.d(Const.TAG, "Storage permission denied. Requesting again");
                    dirChooser.setEnabled(false);
                    showPermissionDeniedDialog();
                } else if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    dirChooser.setEnabled(true);
                }
                {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onPermissionResult(int,[java.lang.String,[int)",this);return;}
            case Const.AUDIO_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(Const.TAG, "Record audio permission granted.");
                    recaudio.setValue("1");
                } else {
                    Log.d(Const.TAG, "Record audio permission denied");
                    recaudio.setValue("0");
                }
                recaudio.setSummary(recaudio.getEntry());
                {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onPermissionResult(int,[java.lang.String,[int)",this);return;}
            case Const.INTERNAL_AUDIO_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(Const.TAG, "Record audio permission granted.");
                    recaudio.setValue("2");
                } else {
                    Log.d(Const.TAG, "Record audio permission denied");
                    recaudio.setValue("0");
                }
                recaudio.setSummary(recaudio.getEntry());
                {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onPermissionResult(int,[java.lang.String,[int)",this);return;}
            case Const.INTERNAL_R_SUBMIX_AUDIO_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(Const.TAG, "Record audio permission granted.");
                    recaudio.setValue("3");
                } else {
                    Log.d(Const.TAG, "Record audio permission denied");
                    recaudio.setValue("0");
                }
                recaudio.setSummary(recaudio.getEntry());
                {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onPermissionResult(int,[java.lang.String,[int)",this);return;}
            case Const.FLOATING_CONTROLS_SYSTEM_WINDOWS_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(Const.TAG, "System Windows permission granted");
                    floatingControl.setChecked(true);
                } else {
                    Log.d(Const.TAG, "System Windows permission denied");
                    floatingControl.setChecked(false);
                }
                {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onPermissionResult(int,[java.lang.String,[int)",this);return;}
            case Const.CAMERA_SYSTEM_WINDOWS_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(Const.TAG, "System Windows permission granted");
                    cameraOverlay.setChecked(true);
                } else {
                    Log.d(Const.TAG, "System Windows permission denied");
                    cameraOverlay.setChecked(false);
                }
                {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onPermissionResult(int,[java.lang.String,[int)",this);return;}
            case Const.CAMERA_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(Const.TAG, "System Windows permission granted");
                    requestSystemWindowsPermission(Const.CAMERA_SYSTEM_WINDOWS_CODE);
                } else {
                    Log.d(Const.TAG, "System Windows permission denied");
                    cameraOverlay.setChecked(false);
                }
            default:
                Log.d(Const.TAG, "Unknown permission request with request code: " + requestCode);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onPermissionResult(int,[java.lang.String,[int)",this,throwable);throw throwable;}
    }

    @Override
    public void onDirectorySelected() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onDirectorySelected()",this);try{Log.d(Const.TAG, "In settings fragment");
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onDirectoryChanged();
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onDirectorySelected()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.onDirectorySelected()",this,throwable);throw throwable;}
    }

    @Override
    public void updateAnalyticsSettings(Const.analytics analytics) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.updateAnalyticsSettings(Const.analytics)",this,analytics);try{switch (analytics){
            case CRASHREPORTING:
                crashReporting.setChecked(true);
                break;
            case USAGESTATS:
                usageStats.setChecked(true);
                break;
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.updateAnalyticsSettings(Const.analytics)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.updateAnalyticsSettings(Const.analytics)",this,throwable);throw throwable;}
    }

    /**
     * Start analytics service with user chosen config
     */
    private void startAnalytics(){
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.startAnalytics()",this);try{if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setupAnalytics();
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.startAnalytics()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.ui.SettingsPreferenceFragment.startAnalytics()",this,throwable);throw throwable;}
    }
}
