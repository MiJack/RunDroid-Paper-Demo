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

package com.orpheusdroid.screenrecorder.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.adapter.Apps;
import com.orpheusdroid.screenrecorder.adapter.AppsListFragmentAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by vijai on 08-12-2017.
 */

public class AppPickerPreference extends DialogPreference implements AppsListFragmentAdapter.OnItemClicked {
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<Apps> apps;

    public AppPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(true);

        /*//set custom dialog layout*/
        setDialogLayoutResource(R.layout.layout_apps_list_preference);

    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onPrepareDialogBuilder(AlertDialog.Builder)",this,builder);try{super.onPrepareDialogBuilder(builder);
        /*// Hide the positive "save" button*/
        builder.setPositiveButton(null, null);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onPrepareDialogBuilder(AlertDialog.Builder)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onPrepareDialogBuilder(AlertDialog.Builder)",this,throwable);throw throwable;}
    }

    @Override
    protected View onCreateDialogView() {
        com.mijack.Xlog.logMethodEnter("android.view.View com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onCreateDialogView()",this);try{com.mijack.Xlog.logMethodExit("android.view.View com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onCreateDialogView()",this);return super.onCreateDialogView();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onCreateDialogView()",this,throwable);throw throwable;}
    }

    @Override
    protected void onBindDialogView(View view) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onBindDialogView(android.view.View)",this,view);try{super.onBindDialogView(view);

        progressBar = view.findViewById(R.id.appsProgressBar);
        recyclerView = view.findViewById(R.id.appsRecyclerView);

        init();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onBindDialogView(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onBindDialogView(android.view.View)",this,throwable);throw throwable;}
    }

    private void init() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.init()",this);try{RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        /*// Generate list of installed apps and display in dialog*/
        new GetApps().execute();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.init()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.init()",this,throwable);throw throwable;}
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onSetInitialValue(boolean,java.lang.Object)",this,restorePersistedValue,defaultValue);try{super.onSetInitialValue(restorePersistedValue, defaultValue);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onSetInitialValue(boolean,java.lang.Object)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onSetInitialValue(boolean,java.lang.Object)",this,throwable);throw throwable;}
    }

    /*// On item click listener for recycler view*/
    @Override
    public void onItemClick(int position) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onItemClick(int)",this,position);try{Log.d(Const.TAG, "Closing dialog. received result. Pos:" + position);
        /*// save the selected app's package name to sharedpreference*/
        persistString(apps.get(position).getPackageName());
        /*//dismiss dialog after saving the value*/
        getDialog().dismiss();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onItemClick(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference.onItemClick(int)",this,throwable);throw throwable;}
    }

    class GetApps extends AsyncTask<Void, Void, ArrayList<Apps>> {

        @Override
        protected void onPreExecute() {
            com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference$GetApps.onPreExecute()",this);try{super.onPreExecute();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference$GetApps.onPreExecute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference$GetApps.onPreExecute()",this,throwable);throw throwable;}
        }

        @Override
        protected void onPostExecute(ArrayList<Apps> apps) {
            com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference$GetApps.onPostExecute(java.util.ArrayList)",this,apps);try{super.onPostExecute(apps);

            /*// Hide progress bar after the apps list has been loaded*/
            progressBar.setVisibility(View.GONE);
            AppsListFragmentAdapter recyclerViewAdapter = new AppsListFragmentAdapter(apps);

            /*// set custom adapter to recycler view*/
            recyclerView.setAdapter(recyclerViewAdapter);

            /*// Set recycler view item click listener*/
            recyclerViewAdapter.setOnClick(AppPickerPreference.this);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference$GetApps.onPostExecute(java.util.ArrayList)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.preferences.AppPickerPreference$GetApps.onPostExecute(java.util.ArrayList)",this,throwable);throw throwable;}
        }

        @Override
        protected ArrayList<Apps> doInBackground(Void... voids) {
            com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.orpheusdroid.screenrecorder.preferences.AppPickerPreference$GetApps.doInBackground([java.lang.Void)",this,voids);try{PackageManager pm = getContext().getPackageManager();
            apps = new ArrayList<>();

            /*// Get list of all installs apps including system apps and apps without any launcher activity*/
            List<PackageInfo> packages = pm.getInstalledPackages(0);

            for (PackageInfo packageInfo : packages) {

                /*// Check if the app has launcher intent set and exclude our own app*/
                if (!(getContext().getPackageName().equals(packageInfo.packageName))
                        && !(pm.getLaunchIntentForPackage(packageInfo.packageName) == null)) {

                    Apps app = new Apps(
                            packageInfo.applicationInfo.loadLabel(getContext().getPackageManager()).toString(),
                            packageInfo.packageName,
                            packageInfo.applicationInfo.loadIcon(getContext().getPackageManager())

                    );

                    /*// Identify the previously selected app*/
                    app.setSelectedApp(
                            AppPickerPreference.this.getPersistedString("none")
                                    .equals(packageInfo.packageName)
                    );
                    if (pm.getLaunchIntentForPackage(packageInfo.packageName) == null)
                        {Log.d(Const.TAG, packageInfo.packageName);}
                    apps.add(app);
                }
                Collections.sort(apps);
            }
            {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.orpheusdroid.screenrecorder.preferences.AppPickerPreference$GetApps.doInBackground([java.lang.Void)",this);return apps;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.orpheusdroid.screenrecorder.preferences.AppPickerPreference$GetApps.doInBackground([java.lang.Void)",this,throwable);throw throwable;}
        }
    }
}
