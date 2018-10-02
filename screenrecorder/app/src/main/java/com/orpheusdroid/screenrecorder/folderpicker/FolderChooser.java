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

package com.orpheusdroid.screenrecorder.folderpicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by vijai on 01-12-2016.
 */

public class FolderChooser extends DialogPreference implements View.OnClickListener,
        DirectoryRecyclerAdapter.OnDirectoryClickedListerner, AdapterView.OnItemSelectedListener {
    private static OnDirectorySelectedListerner onDirectorySelectedListerner;
    private RecyclerView rv;
    private TextView tv_currentDir;
    private TextView tv_empty;
    private File currentDir;
    private ArrayList<File> directories;
    private AlertDialog dialog;
    private DirectoryRecyclerAdapter adapter;
    private Spinner spinner;
    private List<Storages> storages = new ArrayList<>();
    private boolean isExternalStorageSelected = false;
    private SharedPreferences prefs;

    public FolderChooser(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(Const.TAG, "Constructor called");
        initialize();
    }

    private void initialize() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.initialize()",this);try{setPersistent(true);
        setDialogTitle(null);
        setDialogLayoutResource(R.layout.director_chooser);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        currentDir = new File(Environment.getExternalStorageDirectory() + File.separator + Const.APPDIR);
        setSummary(getPersistedString(currentDir.getPath()));
        Log.d(Const.TAG, "Persisted String is: " + getPersistedString(currentDir.getPath()));
        File[] SDCards = ContextCompat.getExternalFilesDirs(getContext().getApplicationContext(), null);
        storages.add(new Storages(Environment.getExternalStorageDirectory().getPath(), Storages.StorageType.Internal));
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (SDCards.length > 1)
            {storages.add(new Storages(SDCards[1].getPath(), Storages.StorageType.External));}
        /*//getRemovableSDPath(SDCards[1]);*/com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.initialize()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.initialize()",this,throwable);throw throwable;}
    }

    @Override
    protected void onBindDialogView(View view) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onBindDialogView(android.view.View)",this,view);try{super.onBindDialogView(view);
        generateFoldersList();
        initView(view);
        initRecyclerView();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onBindDialogView(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onBindDialogView(android.view.View)",this,throwable);throw throwable;}
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onDialogClosed(boolean)",this,positiveResult);try{super.onDialogClosed(positiveResult);
        if (positiveResult) {
            Log.d(Const.TAG, "Directory choosed! " + currentDir.getPath());
            if (!currentDir.canWrite()) {
                Toast.makeText(getContext(), "Cannot write to selected directory. Path will not be saved.", Toast.LENGTH_SHORT).show();
                {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onDialogClosed(boolean)",this);return;}
            }
            persistString(currentDir.getPath());
            onDirectorySelectedListerner.onDirectorySelected();
            setSummary(currentDir.getPath());
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onDialogClosed(boolean)",this,throwable);throw throwable;}
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        com.mijack.Xlog.logMethodEnter("android.os.Parcelable com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onSaveInstanceState()",this);try{Parcelable superState = super.onSaveInstanceState();
        if (currentDir == null) {{com.mijack.Xlog.logMethodExit("android.os.Parcelable com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onSaveInstanceState()",this);return superState;}}
        Bundle dialogState = dialog == null ? null : dialog.onSaveInstanceState();
        {com.mijack.Xlog.logMethodExit("android.os.Parcelable com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onSaveInstanceState()",this);return new SavedStateHandler(superState, currentDir.getPath(), dialogState);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.os.Parcelable com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onSaveInstanceState()",this,throwable);throw throwable;}
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onRestoreInstanceState(android.os.Parcelable)",this,state);try{if (state == null || !state.getClass().equals(SavedStateHandler.class)) {
            super.onRestoreInstanceState(state);
            {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onRestoreInstanceState(android.os.Parcelable)",this);return;}
        }

        SavedStateHandler myState = (SavedStateHandler) state;
        super.onRestoreInstanceState(myState.getSuperState());

        setCurrentDir(currentDir.getPath());
        if (myState.dialogState != null) {
            /*// recreate dialog*/
            newDirDialog(myState.dialogState);
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onRestoreInstanceState(android.os.Parcelable)",this,throwable);throw throwable;}
    }

    private void initRecyclerView() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.initRecyclerView()",this);try{rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
        if (!isDirectoryEmpty()) {
            adapter = new DirectoryRecyclerAdapter(getContext(), this, directories);
            rv.setAdapter(adapter);
        }
        tv_currentDir.setText(currentDir.getPath());com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.initRecyclerView()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.initRecyclerView()",this,throwable);throw throwable;}
    }

    private boolean isDirectoryEmpty() {
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.isDirectoryEmpty()",this);try{if (directories.isEmpty()) {
            rv.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
            {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.isDirectoryEmpty()",this);return true;}
        } else {
            rv.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
            {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.isDirectoryEmpty()",this);return false;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.isDirectoryEmpty()",this,throwable);throw throwable;}
    }

    private void generateFoldersList() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.generateFoldersList()",this);try{File[] dir = currentDir.listFiles(new DirectoryFilter());
        directories = new ArrayList<>(Arrays.asList(dir));
        Collections.sort(directories, new SortFileName());
        Log.d(Const.TAG, "Directory size " + directories.size());com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.generateFoldersList()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.generateFoldersList()",this,throwable);throw throwable;}
    }

    private void initView(View view) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.initView(android.view.View)",this,view);try{ImageButton up = view.findViewById(R.id.nav_up);
        ImageButton createDir = view.findViewById(R.id.create_dir);
        tv_currentDir = view.findViewById(R.id.tv_selected_dir);
        rv = view.findViewById(R.id.rv);
        tv_empty = view.findViewById(R.id.tv_empty);
        spinner = view.findViewById(R.id.storageSpinner);
        up.setOnClickListener(this);
        createDir.setOnClickListener(this);
        ArrayList<String> StorageStrings = new ArrayList<>();
        for (Storages storage : storages) {
            String storageType = storage.getType() == Storages.StorageType.Internal ? "Internal Storage" :
                    "Removable Storage";
            StorageStrings.add(storageType);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, StorageStrings);

        /*// Drop down layout style - list view with radio button*/
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /*// attaching data adapter to spinner*/
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.initView(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.initView(android.view.View)",this,throwable);throw throwable;}
    }

    private void changeDirectory(File file) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.changeDirectory(java.io.File)",this,file);try{currentDir = file;
        Log.d(Const.TAG, "Changed dir is: " + file.getPath());
        generateFoldersList();
        if (!isDirectoryEmpty()) {
            adapter = new DirectoryRecyclerAdapter(getContext(), this, directories);
            rv.swapAdapter(adapter, true);
        }
        tv_currentDir.setText(currentDir.getPath());com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.changeDirectory(java.io.File)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.changeDirectory(java.io.File)",this,throwable);throw throwable;}
    }

    public void setCurrentDir(String currentDir) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.setCurrentDir(java.lang.String)",this,currentDir);try{File dir = new File(currentDir);
        if (dir.exists() && dir.isDirectory()) {
            this.currentDir = dir;
            Log.d(Const.TAG, "Directory set");
        } else {
            createFolder(dir.getPath());
            Log.d(Const.TAG, "Directory created");
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.setCurrentDir(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.setCurrentDir(java.lang.String)",this,throwable);throw throwable;}
    }

    public void setOnDirectoryClickedListerner(OnDirectorySelectedListerner onDirectoryClickedListerner) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.setOnDirectoryClickedListerner(OnDirectorySelectedListerner)",this,onDirectoryClickedListerner);try{FolderChooser.onDirectorySelectedListerner = onDirectoryClickedListerner;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.setOnDirectoryClickedListerner(OnDirectorySelectedListerner)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.setOnDirectoryClickedListerner(OnDirectorySelectedListerner)",this,throwable);throw throwable;}
    }

    private void newDirDialog(Bundle savedState) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.newDirDialog(android.os.Bundle)",this,savedState);try{LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.directory_chooser_edit_text, null);
        final EditText input = view.findViewById(R.id.et_new_folder);
        input.addTextChangedListener(new TextWatcher() {
            {com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$1.beforeTextChanged(java.lang.CharSequence,int,int,int)",this,s,start,count,after);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$1.beforeTextChanged(java.lang.CharSequence,int,int,int)",this);}

            {com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$1.onTextChanged(java.lang.CharSequence,int,int,int)",this,s,start,before,count);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$1.onTextChanged(java.lang.CharSequence,int,int,int)",this);}

            @Override
            public void afterTextChanged(Editable s) {
                com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$1.afterTextChanged(android.text.Editable)",this,s);try{if (dialog != null) {
                    Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setEnabled(!s.toString().trim().isEmpty());
                }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$1.afterTextChanged(android.text.Editable)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$1.afterTextChanged(android.text.Editable)",this,throwable);throw throwable;}
            }
        });

        AlertDialog.Builder ab = new AlertDialog.Builder(getContext())
                .setTitle(R.string.alert_title_create_folder)
                .setMessage(R.string.alert_message_create_folder)
                .setView(view)
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$2.onClick(android.content.DialogInterface,int)",this,dialog,which);try{dialog.dismiss();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$2.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$2.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                            }
                        })
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$3.onClick(android.content.DialogInterface,int)",this,dialog,which);try{dialog.dismiss();
                                String dirName = input.getText().toString().trim();
                                if (!dirName.isEmpty()) {createFolder(dirName);}com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$3.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$3.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                            }
                        });

        dialog = ab.create();
        if (savedState != null) {dialog.onRestoreInstanceState(savedState);}
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!input.getText().toString().trim().isEmpty());com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.newDirDialog(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.newDirDialog(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private boolean createFolder(String dirName) {
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.createFolder(java.lang.String)",this,dirName);try{if (currentDir == null) {
            Toast.makeText(getContext(), "No directory selected", Toast.LENGTH_SHORT).show();
            {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.createFolder(java.lang.String)",this);return false;}
        }
        if (!currentDir.canWrite()) {
            Toast.makeText(getContext(), "No permission to write to directory", Toast.LENGTH_SHORT).show();
            {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.createFolder(java.lang.String)",this);return false;}
        }

        File newDir;
        if (dirName.contains(Environment.getExternalStorageDirectory().getPath()))
            {newDir = new File(dirName);}
        else
            {newDir = new File(currentDir, dirName);}
        if (newDir.exists()) {
            Toast.makeText(getContext(), "Directory already exists", Toast.LENGTH_SHORT).show();
            changeDirectory(new File(currentDir, dirName));
            {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.createFolder(java.lang.String)",this);return false;}
        }

        if (!newDir.mkdir()) {
            Toast.makeText(getContext(), "Error creating directory", Toast.LENGTH_SHORT).show();
            Log.d(Const.TAG, newDir.getPath());
            {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.createFolder(java.lang.String)",this);return false;}
        }

        changeDirectory(new File(currentDir, dirName));

        {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.createFolder(java.lang.String)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.createFolder(java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    public void onClick(View view) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onClick(android.view.View)",this,view);try{switch (view.getId()) {
            case R.id.nav_up:
                File parentDirectory = new File(currentDir.getParent());
                Log.d(Const.TAG, parentDirectory.getPath());
                if (!isExternalStorageSelected) {
                    if (parentDirectory.getPath().contains(storages.get(0).getPath()))
                        {changeDirectory(parentDirectory);}
                } else
                    {changeExternalDirectory(parentDirectory);}
                {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onClick(android.view.View)",this);return;}
            case R.id.create_dir:
                newDirDialog(null);
                {com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onClick(android.view.View)",this);return;}
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onClick(android.view.View)",this,throwable);throw throwable;}
    }

    private void changeExternalDirectory(File parentDirectory) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.changeExternalDirectory(java.io.File)",this,parentDirectory);try{String externalBaseDir = getRemovableSDPath(storages.get(1).getPath());
        if (parentDirectory.getPath().contains(externalBaseDir) && parentDirectory.canWrite())
            {changeDirectory(parentDirectory);}
        else if (parentDirectory.getPath().contains(externalBaseDir) && !parentDirectory.canWrite())
            {Toast.makeText(getContext(), R.string.external_storage_dir_not_writable, Toast.LENGTH_SHORT).show();}com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.changeExternalDirectory(java.io.File)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.changeExternalDirectory(java.io.File)",this,throwable);throw throwable;}
    }

    private String getRemovableSDPath(String pathSD) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.getRemovableSDPath(java.lang.String)",this,pathSD);try{/*//String pathSD = file.toString();*/
        int index = pathSD.indexOf("Android");
        Log.d(Const.TAG, "Short code is: " + pathSD.substring(0, index));
        String filename = pathSD.substring(0, index - 1);
        Log.d(Const.TAG, "External Base Dir " + filename);
        {com.mijack.Xlog.logMethodExit("java.lang.String com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.getRemovableSDPath(java.lang.String)",this);return filename;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.getRemovableSDPath(java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    public void OnDirectoryClicked(File directory) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.OnDirectoryClicked(java.io.File)",this,directory);try{changeDirectory(directory);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.OnDirectoryClicked(java.io.File)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.OnDirectoryClicked(java.io.File)",this,throwable);throw throwable;}
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onItemSelected(android.widget.AdapterView,android.view.View,int,long)",this,adapterView,view,i,l);try{Log.d(Const.TAG, "Selected storage is: " + storages.get(i));
        isExternalStorageSelected = (storages.get(i).getType() == Storages.StorageType.External);
        if (isExternalStorageSelected && !prefs.getBoolean(Const.ALERT_EXTR_STORAGE_CB_KEY, false)){
            showExtDirAlert();
        }
        changeDirectory(new File(storages.get(i).getPath()));com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onItemSelected(android.widget.AdapterView,android.view.View,int,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onItemSelected(android.widget.AdapterView,android.view.View,int,long)",this,throwable);throw throwable;}
    }

    private void showExtDirAlert() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.showExtDirAlert()",this);try{View checkBoxView = View.inflate(getContext(), R.layout.alert_checkbox, null);
        final CheckBox checkBox = checkBoxView.findViewById(R.id.donot_warn_cb);
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.alert_ext_dir_warning_title)
                .setMessage(R.string.alert_ext_dir_warning_message)
                .setView(checkBoxView)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$4.onClick(android.content.DialogInterface,int)",this,dialogInterface,i);try{if (checkBox.isChecked())
                            {prefs.edit().putBoolean(Const.ALERT_EXTR_STORAGE_CB_KEY, true).apply();}com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$4.onClick(android.content.DialogInterface,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$4.onClick(android.content.DialogInterface,int)",this,throwable);throw throwable;}
                    }
                })
                .create().show();com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.showExtDirAlert()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.showExtDirAlert()",this,throwable);throw throwable;}

    }

    {com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onNothingSelected(android.widget.AdapterView)",this,adapterView);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.folderpicker.FolderChooser.onNothingSelected(android.widget.AdapterView)",this);}

    private class DirectoryFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$DirectoryFilter.accept(java.io.File)",this,file);try{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$DirectoryFilter.accept(java.io.File)",this);return file.isDirectory() && !file.isHidden();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$DirectoryFilter.accept(java.io.File)",this,throwable);throw throwable;}
        }
    }

    /*//sorts based on the files name*/
    private class SortFileName implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$SortFileName.compare(java.io.File,java.io.File)",this,f1,f2);try{com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$SortFileName.compare(java.io.File,java.io.File)",this);return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.folderpicker.FolderChooser$SortFileName.compare(java.io.File,java.io.File)",this,throwable);throw throwable;}
        }
    }
}
