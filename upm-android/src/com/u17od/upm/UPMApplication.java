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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

import android.app.Activity;
import android.app.Application;
import android.app.backup.BackupManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.u17od.upm.database.PasswordDatabase;

/**
 * This class replaces the regular Application class in the application and
 * allows us to store data at the application level.
 */
public class UPMApplication extends Application {

    private PasswordDatabase passwordDatabase;
    private Date timeOfLastSync;
    private BackupManager backupManager;
    public static final Object[] sDataLock = new Object[0];

    @Override
    public void onCreate() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.UPMApplication.onCreate()",this);try{super.onCreate();
        backupManager = new BackupManager(this);com.mijack.Xlog.logMethodExit("void com.u17od.upm.UPMApplication.onCreate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.UPMApplication.onCreate()",this,throwable);throw throwable;}
    }

    public BackupManager getBackupManager() {
        com.mijack.Xlog.logMethodEnter("android.app.backup.BackupManager com.u17od.upm.UPMApplication.getBackupManager()",this);try{com.mijack.Xlog.logMethodExit("android.app.backup.BackupManager com.u17od.upm.UPMApplication.getBackupManager()",this);return backupManager;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.app.backup.BackupManager com.u17od.upm.UPMApplication.getBackupManager()",this,throwable);throw throwable;}
    }

    public Date getTimeOfLastSync() {
        com.mijack.Xlog.logMethodEnter("java.util.Date com.u17od.upm.UPMApplication.getTimeOfLastSync()",this);try{com.mijack.Xlog.logMethodExit("java.util.Date com.u17od.upm.UPMApplication.getTimeOfLastSync()",this);return timeOfLastSync;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.Date com.u17od.upm.UPMApplication.getTimeOfLastSync()",this,throwable);throw throwable;}
    }

    public void setTimeOfLastSync(Date timeOfLastSync) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.UPMApplication.setTimeOfLastSync(java.util.Date)",this,timeOfLastSync);try{this.timeOfLastSync = timeOfLastSync;com.mijack.Xlog.logMethodExit("void com.u17od.upm.UPMApplication.setTimeOfLastSync(java.util.Date)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.UPMApplication.setTimeOfLastSync(java.util.Date)",this,throwable);throw throwable;}
    }

    public void setPasswordDatabase(PasswordDatabase passwordDatabase) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.UPMApplication.setPasswordDatabase(com.u17od.upm.database.PasswordDatabase)",this,passwordDatabase);try{this.passwordDatabase = passwordDatabase;com.mijack.Xlog.logMethodExit("void com.u17od.upm.UPMApplication.setPasswordDatabase(com.u17od.upm.database.PasswordDatabase)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.UPMApplication.setPasswordDatabase(com.u17od.upm.database.PasswordDatabase)",this,throwable);throw throwable;}
    }

    public PasswordDatabase getPasswordDatabase() {
        com.mijack.Xlog.logMethodEnter("com.u17od.upm.database.PasswordDatabase com.u17od.upm.UPMApplication.getPasswordDatabase()",this);try{com.mijack.Xlog.logMethodExit("com.u17od.upm.database.PasswordDatabase com.u17od.upm.UPMApplication.getPasswordDatabase()",this);return passwordDatabase;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.u17od.upm.database.PasswordDatabase com.u17od.upm.UPMApplication.getPasswordDatabase()",this,throwable);throw throwable;}
    }

    protected boolean copyFile(File source, File dest, Activity activity) {
        com.mijack.Xlog.logMethodEnter("boolean com.u17od.upm.UPMApplication.copyFile(java.io.File,java.io.File,android.app.Activity)",this,source,dest,activity);try{boolean successful = false;

        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(source);
            sourceChannel = is.getChannel();

            File destFile = null;
            if (dest.isDirectory()) {
                destFile = new File(dest, source.getName());
            } else {
                destFile = dest;
            }

            os = new FileOutputStream(destFile);
            destinationChannel = os.getChannel();
            destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

            successful=true;
        } catch (IOException e) {
            Log.e(activity.getClass().getName(), getString(R.string.file_problem), e);
            Toast.makeText(activity, R.string.file_problem, Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (sourceChannel != null) {
                    sourceChannel.close();
                }
                if (is != null) {
                    is.close();
                }
                if (destinationChannel != null) {
                    destinationChannel.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                Log.e(activity.getClass().getName(), getString(R.string.file_problem), e);
                Toast.makeText(activity, R.string.file_problem, Toast.LENGTH_LONG).show();
            }
        }

        {com.mijack.Xlog.logMethodExit("boolean com.u17od.upm.UPMApplication.copyFile(java.io.File,java.io.File,android.app.Activity)",this);return successful;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.u17od.upm.UPMApplication.copyFile(java.io.File,java.io.File,android.app.Activity)",this,throwable);throw throwable;}
    }

    protected void restoreDatabase(Activity activity) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.UPMApplication.restoreDatabase(android.app.Activity)",this,activity);try{deleteDatabase(activity);
        File fileOnSDCard = new File(Environment.getExternalStorageDirectory(), Utilities.DEFAULT_DATABASE_FILE);
        File databaseFile = Utilities.getDatabaseFile(activity);
        ((UPMApplication) activity.getApplication()).copyFile(fileOnSDCard, databaseFile, activity);com.mijack.Xlog.logMethodExit("void com.u17od.upm.UPMApplication.restoreDatabase(android.app.Activity)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.UPMApplication.restoreDatabase(android.app.Activity)",this,throwable);throw throwable;}
    }

    protected void deleteDatabase(Activity activity) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.UPMApplication.deleteDatabase(android.app.Activity)",this,activity);try{Utilities.getDatabaseFile(activity).delete();
        Utilities.setDatabaseFileName(null, activity);com.mijack.Xlog.logMethodExit("void com.u17od.upm.UPMApplication.deleteDatabase(android.app.Activity)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.UPMApplication.deleteDatabase(android.app.Activity)",this,throwable);throw throwable;}
    }

}
