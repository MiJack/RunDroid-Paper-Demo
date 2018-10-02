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

import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.dropbox.client2.session.AccessTokenPair;
import com.u17od.upm.database.PasswordDatabase;

public class Utilities {

    public static final String DEFAULT_DATABASE_FILE = "upm.db";
    public static final String PREFS_DB_FILE_NAME = "DB_FILE_NAME";

    public static final String DROPBOX_PREFS = "DROPBOX_PREFS";
    public static final String DROPBOX_KEY = "DROPBOX_KEY";
    public static final String DROPBOX_SECRET = "DROPBOX_SECRET";
    public static final String DROPBOX_DB_REV = "DROPBOX_DB_REV";
    public static final String DROPBOX_SELECTED_FILENAME = "DROPBOX_SELECTED_FILENAME";

    public static class VERSION_CODES {
        public static final int HONEYCOMB = 11;
    }

    public static class VERSION {
        /**
         * The user-visible SDK version of the framework; its possible
         * values are defined in {@link Build.VERSION_CODES}.
         */
        public static final int SDK_INT = Integer.parseInt(System.getProperty(
                "ro.build.version.sdk", "0"));
    }

    public static File getDatabaseFile(Activity activity) {
        com.mijack.Xlog.logStaticMethodEnter("java.io.File com.u17od.upm.Utilities.getDatabaseFile(android.app.Activity)",activity);try{String dbFileName = getDatabaseFileName(activity);
        if (dbFileName == null || dbFileName.equals("")) {
            {com.mijack.Xlog.logStaticMethodExit("java.io.File com.u17od.upm.Utilities.getDatabaseFile(android.app.Activity)");return new File(activity.getFilesDir(), DEFAULT_DATABASE_FILE);}
        } else {
            {com.mijack.Xlog.logStaticMethodExit("java.io.File com.u17od.upm.Utilities.getDatabaseFile(android.app.Activity)");return new File(activity.getFilesDir(), dbFileName);}
        }}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.io.File com.u17od.upm.Utilities.getDatabaseFile(android.app.Activity)",throwable);throw throwable;}
    }

    public static String getDatabaseFileName(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.u17od.upm.Utilities.getDatabaseFileName(android.content.Context)",context);try{SharedPreferences settings = context.getSharedPreferences(Prefs.PREFS_NAME, Activity.MODE_PRIVATE);
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.u17od.upm.Utilities.getDatabaseFileName(android.content.Context)");return settings.getString(PREFS_DB_FILE_NAME, DEFAULT_DATABASE_FILE);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.u17od.upm.Utilities.getDatabaseFileName(android.content.Context)",throwable);throw throwable;}
    }

    public static String getSyncMethod(Activity activity) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.u17od.upm.Utilities.getSyncMethod(android.app.Activity)",activity);try{UPMApplication app = (UPMApplication) activity.getApplication();
        String remoteHTTPLocation = app.getPasswordDatabase().getDbOptions().getRemoteLocation();
        SharedPreferences settings = activity.getSharedPreferences(Prefs.PREFS_NAME, Activity.MODE_PRIVATE);
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.u17od.upm.Utilities.getSyncMethod(android.app.Activity)");return getSyncMethod(settings, remoteHTTPLocation);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.u17od.upm.Utilities.getSyncMethod(android.app.Activity)",throwable);throw throwable;}
    }

    /**
     * If we've upgraded from an older version of UPM the preference
     * 'sync.method' may not exist. In this case we should check if the
     * database has a value for sharedURL. If it does it means the database
     * has been configured to use "http" as the sync method
     * @param settings
     * @param remoteHTTPLocation
     * @return
     */
    public static String getSyncMethod(SharedPreferences settings, String remoteHTTPLocation) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.u17od.upm.Utilities.getSyncMethod(android.content.SharedPreferences,java.lang.String)",settings,remoteHTTPLocation);try{String syncMethod = settings.getString(Prefs.SYNC_METHOD, null);

        if (syncMethod == null) {
            if (remoteHTTPLocation != null) {
                syncMethod = Prefs.SyncMethod.HTTP;
            } else {
                syncMethod = Prefs.SyncMethod.DISABLED;
            }
        }

        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.u17od.upm.Utilities.getSyncMethod(android.content.SharedPreferences,java.lang.String)");return syncMethod;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.u17od.upm.Utilities.getSyncMethod(android.content.SharedPreferences,java.lang.String)",throwable);throw throwable;}
    }

    public static void setDatabaseFileName(String dbFileName, Activity activity) {
        com.mijack.Xlog.logStaticMethodEnter("void com.u17od.upm.Utilities.setDatabaseFileName(java.lang.String,android.app.Activity)",dbFileName,activity);try{SharedPreferences settings = activity.getSharedPreferences(Prefs.PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREFS_DB_FILE_NAME, dbFileName);
        editor.commit();com.mijack.Xlog.logStaticMethodExit("void com.u17od.upm.Utilities.setDatabaseFileName(java.lang.String,android.app.Activity)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.u17od.upm.Utilities.setDatabaseFileName(java.lang.String,android.app.Activity)",throwable);throw throwable;}
    }

    public static void setSyncMethod(String syncMethod, Activity activity) {
        com.mijack.Xlog.logStaticMethodEnter("void com.u17od.upm.Utilities.setSyncMethod(java.lang.String,android.app.Activity)",syncMethod,activity);try{SharedPreferences settings = activity.getSharedPreferences(Prefs.PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Prefs.SYNC_METHOD, syncMethod);
        editor.commit();com.mijack.Xlog.logStaticMethodExit("void com.u17od.upm.Utilities.setSyncMethod(java.lang.String,android.app.Activity)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.u17od.upm.Utilities.setSyncMethod(java.lang.String,android.app.Activity)",throwable);throw throwable;}
    }

    public static boolean isSyncRequired(Activity activity) {
        com.mijack.Xlog.logStaticMethodEnter("boolean com.u17od.upm.Utilities.isSyncRequired(android.app.Activity)",activity);try{UPMApplication app = (UPMApplication) activity.getApplication();
        PasswordDatabase db = app.getPasswordDatabase();
        Date timeOfLastSync = app.getTimeOfLastSync();

        boolean syncRequired = false;

        if (db.getDbOptions().getRemoteLocation() != null && !db.getDbOptions().getRemoteLocation().equals("")) {
            if (timeOfLastSync == null || System.currentTimeMillis() - timeOfLastSync.getTime() > (5 * 60 * 1000)) {
                syncRequired = true;
            }
        }

        {com.mijack.Xlog.logStaticMethodExit("boolean com.u17od.upm.Utilities.isSyncRequired(android.app.Activity)");return syncRequired;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("boolean com.u17od.upm.Utilities.isSyncRequired(android.app.Activity)",throwable);throw throwable;}
    }

    public static AccessTokenPair getDropboxAccessTokenPair(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("com.dropbox.client2.session.AccessTokenPair com.u17od.upm.Utilities.getDropboxAccessTokenPair(android.content.Context)",context);try{SharedPreferences settings =
            context.getSharedPreferences(DROPBOX_PREFS, Context.MODE_PRIVATE);
        String dropboxKey = settings.getString(DROPBOX_KEY, null);
        String dropboxSecret = settings.getString(DROPBOX_SECRET, null);
        AccessTokenPair accessTokenPair = null;
        if (dropboxKey != null && dropboxSecret != null) {
            accessTokenPair = new AccessTokenPair(dropboxKey, dropboxSecret);
        }
        {com.mijack.Xlog.logStaticMethodExit("com.dropbox.client2.session.AccessTokenPair com.u17od.upm.Utilities.getDropboxAccessTokenPair(android.content.Context)");return accessTokenPair;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.dropbox.client2.session.AccessTokenPair com.u17od.upm.Utilities.getDropboxAccessTokenPair(android.content.Context)",throwable);throw throwable;}
    }

    public static void setDropboxAccessTokenPair(Context context, AccessTokenPair accessTokenPair) {
       com.mijack.Xlog.logStaticMethodEnter("void com.u17od.upm.Utilities.setDropboxAccessTokenPair(android.content.Context,com.dropbox.client2.session.AccessTokenPair)",context,accessTokenPair);try{SharedPreferences settings = context.getSharedPreferences(DROPBOX_PREFS, Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = settings.edit();
       editor.putString(DROPBOX_KEY, accessTokenPair.key);
       editor.putString(DROPBOX_SECRET, accessTokenPair.secret);
       editor.commit();com.mijack.Xlog.logStaticMethodExit("void com.u17od.upm.Utilities.setDropboxAccessTokenPair(android.content.Context,com.dropbox.client2.session.AccessTokenPair)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.u17od.upm.Utilities.setDropboxAccessTokenPair(android.content.Context,com.dropbox.client2.session.AccessTokenPair)",throwable);throw throwable;}
     }

    public static void clearDropboxAccessTokenPair(Context context) {
        com.mijack.Xlog.logStaticMethodEnter("void com.u17od.upm.Utilities.clearDropboxAccessTokenPair(android.content.Context)",context);try{SharedPreferences settings = context.getSharedPreferences(DROPBOX_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(DROPBOX_KEY);
        editor.remove(DROPBOX_SECRET);
        editor.commit();com.mijack.Xlog.logStaticMethodExit("void com.u17od.upm.Utilities.clearDropboxAccessTokenPair(android.content.Context)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.u17od.upm.Utilities.clearDropboxAccessTokenPair(android.content.Context)",throwable);throw throwable;}
      }

    public static void setConfig(Context context, String fileName, String keyName, String value) {
        com.mijack.Xlog.logStaticMethodEnter("void com.u17od.upm.Utilities.setConfig(android.content.Context,java.lang.String,java.lang.String,java.lang.String)",context,fileName,keyName,value);try{SharedPreferences settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(keyName, value);
        editor.commit();com.mijack.Xlog.logStaticMethodExit("void com.u17od.upm.Utilities.setConfig(android.content.Context,java.lang.String,java.lang.String,java.lang.String)");}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("void com.u17od.upm.Utilities.setConfig(android.content.Context,java.lang.String,java.lang.String,java.lang.String)",throwable);throw throwable;}
    }

    public static String getConfig(Context context, String fileName, String keyName) {
        com.mijack.Xlog.logStaticMethodEnter("java.lang.String com.u17od.upm.Utilities.getConfig(android.content.Context,java.lang.String,java.lang.String)",context,fileName,keyName);try{SharedPreferences settings =
            context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        {com.mijack.Xlog.logStaticMethodExit("java.lang.String com.u17od.upm.Utilities.getConfig(android.content.Context,java.lang.String,java.lang.String)");return settings.getString(keyName, null);}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("java.lang.String com.u17od.upm.Utilities.getConfig(android.content.Context,java.lang.String,java.lang.String)",throwable);throw throwable;}
    }

}
