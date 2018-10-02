package com.u17od.upm;

import java.io.IOException;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class UPMBackupAgent extends BackupAgentHelper {

    private static final String PREFS_BACKUP_KEY = "prefs";
    private static final String DBFILE_BACKUP_KEY = "dbFile";

    @Override
    public void onCreate() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.UPMBackupAgent.onCreate()",this);try{SharedPreferencesBackupHelper prefBackupHelper =
                new SharedPreferencesBackupHelper(this, Prefs.PREFS_NAME);
        addHelper(PREFS_BACKUP_KEY, prefBackupHelper);

        String dbFileName = Utilities.getDatabaseFileName(this);
        Log.i(getClass().getName(),
                String.format("UPM database file to backup: %s", dbFileName));
        FileBackupHelper dbFileBackupHelper =
                new FileBackupHelper(this, dbFileName);
        addHelper(DBFILE_BACKUP_KEY, dbFileBackupHelper);com.mijack.Xlog.logMethodExit("void com.u17od.upm.UPMBackupAgent.onCreate()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.UPMBackupAgent.onCreate()",this,throwable);throw throwable;}

    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
             ParcelFileDescriptor newState) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.UPMBackupAgent.onBackup(android.os.ParcelFileDescriptor,android.app.backup.BackupDataOutput,android.os.ParcelFileDescriptor)",this,oldState,data,newState);try{synchronized (UPMApplication.sDataLock) {
            super.onBackup(oldState, data, newState);
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.UPMBackupAgent.onBackup(android.os.ParcelFileDescriptor,android.app.backup.BackupDataOutput,android.os.ParcelFileDescriptor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.UPMBackupAgent.onBackup(android.os.ParcelFileDescriptor,android.app.backup.BackupDataOutput,android.os.ParcelFileDescriptor)",this,throwable);throw throwable;}
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState)
            throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.UPMBackupAgent.onRestore(android.app.backup.BackupDataInput,int,android.os.ParcelFileDescriptor)",this,data,appVersionCode,newState);try{synchronized (UPMApplication.sDataLock) {
            super.onRestore(data, appVersionCode, newState);
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.UPMBackupAgent.onRestore(android.app.backup.BackupDataInput,int,android.os.ParcelFileDescriptor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.UPMBackupAgent.onRestore(android.app.backup.BackupDataInput,int,android.os.ParcelFileDescriptor)",this,throwable);throw throwable;}
    }

}
