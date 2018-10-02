/*
 * Universal Password Manager
 * Copyright (c) 2010-2011 Adrian Smith
 *
 * This file is part of Universal Password Manager.
 *   
 * Universal Password Manager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Universal Password Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Universal Password Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.u17od.upm;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;
import android.view.KeyEvent;

import com.u17od.upm.database.PasswordDatabase;

public class Prefs extends PreferenceActivity implements OnPreferenceChangeListener {

    /*// Name of the preferences file*/
    public static final String PREFS_NAME = "UPMPrefs";

    /*// Configuration setting constants*/
    public static final String PREF_TRUSTED_HOSTNAME = "trustedHostname";
    public static final String SYNC_METHOD = "sync.method";

    public static interface SyncMethod {
        public static final String DISABLED = "disabled";
        public static final String DROPBOX = "dropbox";
        public static final String HTTP = "http";
    }

    /*// Reference to the various preference objects*/
    private ListPreference syncMethodPreference;
    private PreferenceCategory httpServerSettingsCategory;
    private ListPreference sharedURLAuthPref;
    private EditTextPreference sharedURLPref;
    private EditTextPreference trustedHostnamePref;

    private PasswordDatabase db;
    private String originalSyncMethod;
    private boolean saveRequired;

    private String[] syncMethodValues = {
            SyncMethod.DISABLED, SyncMethod.DROPBOX, SyncMethod.HTTP
    };
    private String[] syncMethodHuman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.Prefs.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);

        saveRequired = false;

        /*// Create the menu items*/
        addPreferencesFromResource(R.xml.settings);

        /*// Load the preferences*/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        httpServerSettingsCategory = (PreferenceCategory) findPreference("http_server_settings");

        /*// Get a handle to the preference items*/
        sharedURLAuthPref = (ListPreference) findPreference("shared_url_auth");
        sharedURLPref = (EditTextPreference) findPreference("shared_url");
        trustedHostnamePref = (EditTextPreference) findPreference("trusted_hostname");

        sharedURLAuthPref.setOnPreferenceChangeListener(this);
        sharedURLPref.setOnPreferenceChangeListener(this);
        trustedHostnamePref.setOnPreferenceChangeListener(this);

        /*// Populate the preferences*/
        db = ((UPMApplication) getApplication()).getPasswordDatabase();
        String sharedURL = db.getDbOptions().getRemoteLocation();
        if (sharedURL.equals("")) {
            sharedURL = null;
        }
        sharedURLPref.setText(sharedURL);

        ArrayList<String> accountNamesAL = db.getAccountNames();
        String[] accountNames = new String[accountNamesAL.size() + 1];
        accountNames[0] = "";
        System.arraycopy(accountNamesAL.toArray(), 0, accountNames, 1, accountNamesAL.size());
        sharedURLAuthPref.setEntryValues(accountNames);
        sharedURLAuthPref.setEntries(accountNames);
        sharedURLAuthPref.setValue(db.getDbOptions().getAuthDBEntry());

        /*// Some preferences are stored using Android's SharedPreferences*/
        String trustedHostname = settings.getString(PREF_TRUSTED_HOSTNAME, "");
        trustedHostnamePref.setText(trustedHostname);

        Resources res = getResources();
        syncMethodHuman= res.getStringArray(R.array.sync_methods_human);

        syncMethodPreference = (ListPreference) findPreference("sync_method");
        syncMethodPreference.setEntryValues(syncMethodValues);

        /*// Figure out what the sync method really is*/
        originalSyncMethod = Utilities.getSyncMethod(settings, sharedURL);

        /*// Populate the syncMethodPreference with what we've determined from*/
        /*// the stored preferences*/
        syncMethodPreference.setValue(originalSyncMethod);

        /*// Initialize the on-screen text based on the sync method*/
        initialiseFields(originalSyncMethod);

        syncMethodPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                com.mijack.Xlog.logMethodEnter("boolean com.u17od.upm.Prefs$1.onPreferenceChange(android.preference.EditTextPreference,java.lang.Object)",this,preference,newValue);try{initialiseFields((String) newValue);

                if (!newValue.equals(originalSyncMethod) &&
                        (newValue.equals(SyncMethod.HTTP) || originalSyncMethod.equals(SyncMethod.HTTP))) {
                    saveRequired = true;
                }

                {com.mijack.Xlog.logMethodExit("void com.u17od.upm.Prefs.onCreate(android.os.Bundle)",this);{com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.Prefs$1.onPreferenceChange(android.preference.EditTextPreference,java.lang.Object)",this);return true;}}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.u17od.upm.Prefs$1.onPreferenceChange(android.preference.EditTextPreference,java.lang.Object)",this,throwable);throw throwable;}
            }
        });}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.Prefs.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    private void initialiseFields(String syncMethod) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.Prefs.initialiseFields(java.lang.String)",this,syncMethod);try{/*// Set the SyncMethod summary to the value selected in the List*/
        for (int i=0; i<syncMethodValues.length; i++) {
            if (syncMethod.equals(syncMethodValues[i])) {
                syncMethodPreference.setSummary(syncMethodHuman[i]);
                break;
            }
        }

        /*// Only enable the HTTP Server Settings category if the user*/
        /*// selected HTTP as their method of syncing*/
        if (syncMethod.equals(SyncMethod.HTTP)) {
            httpServerSettingsCategory.setEnabled(true);
        } else {
            httpServerSettingsCategory.setEnabled(false);
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.Prefs.initialiseFields(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.Prefs.initialiseFields(java.lang.String)",this,throwable);throw throwable;}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        com.mijack.Xlog.logMethodEnter("boolean com.u17od.upm.Prefs.onKeyDown(int,android.view.KeyEvent)",this,keyCode,event);try{if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (saveRequired) {
                if (syncMethodPreference.getValue().equals(SyncMethod.HTTP)) {
                    db.getDbOptions().setRemoteLocation(sharedURLPref.getText());
                    db.getDbOptions().setAuthDBEntry(sharedURLAuthPref.getValue());
                } else {
                    db.getDbOptions().setRemoteLocation(null);
                    db.getDbOptions().setAuthDBEntry(null);
                }
                new SaveDatabaseAsyncTask(this, new Callback() {
                    @Override
                    public void execute() {
                        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.Prefs$2.execute()",this);try{Prefs.this.finish();com.mijack.Xlog.logMethodExit("void com.u17od.upm.Prefs$2.execute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.Prefs$2.execute()",this,throwable);throw throwable;}
                    }
                }).execute(db);
                {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.Prefs.onKeyDown(int,android.view.KeyEvent)",this);return true;}
            }
        }
        {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.Prefs.onKeyDown(int,android.view.KeyEvent)",this);return super.onKeyDown(keyCode, event);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.u17od.upm.Prefs.onKeyDown(int,android.view.KeyEvent)",this,throwable);throw throwable;}
    } 

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        com.mijack.Xlog.logMethodEnter("boolean com.u17od.upm.Prefs.onPreferenceChange(android.preference.EditTextPreference,java.lang.Object)",this,preference,newValue);try{if (preference == sharedURLAuthPref) {
            if (!sharedURLAuthPref.getValue().equals(newValue)) {
                saveRequired = true;
            }
        } else if (preference == sharedURLPref) {
            if (sharedURLPref.getText() == null && newValue != null || 
                    !sharedURLPref.getText().equals(newValue)) {
                saveRequired = true;
            }
        }
        
        {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.Prefs.onPreferenceChange(android.preference.EditTextPreference,java.lang.Object)",this);return true;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.u17od.upm.Prefs.onPreferenceChange(android.preference.EditTextPreference,java.lang.Object)",this,throwable);throw throwable;}
    }

    private UPMApplication getUPMApplication() {
        com.mijack.Xlog.logMethodEnter("com.u17od.upm.UPMApplication com.u17od.upm.Prefs.getUPMApplication()",this);try{com.mijack.Xlog.logMethodExit("com.u17od.upm.UPMApplication com.u17od.upm.Prefs.getUPMApplication()",this);return (UPMApplication) getApplication();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.u17od.upm.UPMApplication com.u17od.upm.Prefs.getUPMApplication()",this,throwable);throw throwable;}
    }

    @Override
    protected void onStop(){
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.Prefs.onStop()",this);try{super.onStop();

       /*// We need an Editor object to make preference changes.*/
       /*// All objects are from android.context.Context*/
       SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
       SharedPreferences.Editor editor = settings.edit();
       editor.putString(PREF_TRUSTED_HOSTNAME, trustedHostnamePref.getText());
       editor.putString(SYNC_METHOD, syncMethodPreference.getValue());

       /*// Commit the edits!*/
       editor.commit();

       /*// Ask the BackupManager to backup the database using*/
       /*// Google's cloud backup service.*/
       Log.i("Prefs", "Calling BackupManager().dataChanged()");
       getUPMApplication().getBackupManager().dataChanged();com.mijack.Xlog.logMethodExit("void com.u17od.upm.Prefs.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.Prefs.onStop()",this,throwable);throw throwable;}
     }

}
