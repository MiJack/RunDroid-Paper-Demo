package com.u17od.upm;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

import javax.crypto.SecretKey;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.u17od.upm.crypto.InvalidPasswordException;
import com.u17od.upm.database.PasswordDatabase;
import com.u17od.upm.database.ProblemReadingDatabaseFile;


public abstract class SyncDatabaseActivity extends Activity {

    private static final int ENTER_PW_REQUEST_CODE = 222;
    public static final int SYNC_DB_REQUEST_CODE = 226;

    public static final int RESULT_REFRESH = 1;

    public static interface SyncResult {
        public static final int IN_SYNC = 0;
        public static final int UPLOAD_LOCAL = 1;
        public static final int KEEP_REMOTE = 2;
    }

    protected File downloadedDatabaseFile;

    protected abstract void uploadDatabase();
    protected abstract void downloadDatabase();

    protected void decryptDatabase() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SyncDatabaseActivity.decryptDatabase()",this);try{SecretKey existingDBSecretKey = getPasswordDatabase().getEncryptionService().getSecretKey();
        try {
            PasswordDatabase passwordDatabase = null;
            if (downloadedDatabaseFile != null) {
                passwordDatabase = new PasswordDatabase(downloadedDatabaseFile, existingDBSecretKey);
            }
            syncDb(passwordDatabase);
        } catch (IOException e) {
            Log.e("SyncDatabaseActivity.onCreate()", "Problem reading database", e);
            UIUtilities.showToast(SyncDatabaseActivity.this, R.string.problem_reading_upm_db, true);
            finish();
        } catch (GeneralSecurityException e) {
            Log.e("SyncDatabaseActivity.onCreate()", "Problem decrypting database", e);
            UIUtilities.showToast(SyncDatabaseActivity.this, R.string.problem_decrypying_db, true);
            finish();
        } catch (ProblemReadingDatabaseFile e) {
            Log.e("SyncDatabaseActivity.onCreate()", "Not a password database", e);
            UIUtilities.showToast(SyncDatabaseActivity.this, R.string.not_password_database);
            finish();
        } catch (InvalidPasswordException e) {
            EnterMasterPassword.databaseFileToDecrypt = downloadedDatabaseFile;
            Intent i = new Intent(SyncDatabaseActivity.this, EnterMasterPassword.class);
            startActivityForResult(i, ENTER_PW_REQUEST_CODE);
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SyncDatabaseActivity.decryptDatabase()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SyncDatabaseActivity.decryptDatabase()",this,throwable);throw throwable;}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SyncDatabaseActivity.onActivityResult(int,int,android.content.Intent)",this,requestCode,resultCode,intent);try{switch(requestCode) {
            case ENTER_PW_REQUEST_CODE:
                if (resultCode == Activity.RESULT_CANCELED) {
                    UIUtilities.showToast(this, R.string.enter_password_cancalled);
                    finish();
                } else {
                    syncDb(EnterMasterPassword.decryptedPasswordDatabase);
                }
                break;
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SyncDatabaseActivity.onActivityResult(int,int,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SyncDatabaseActivity.onActivityResult(int,int,android.content.Intent)",this,throwable);throw throwable;}
    }

    /**
     * Check if the downloaded DB is more recent than the current db.
     * If it is the replace the current DB with the downloaded one and reload
     * the accounts listview
     */
    protected int syncDb(PasswordDatabase dbDownloadedOnSync) {
        com.mijack.Xlog.logMethodEnter("int com.u17od.upm.SyncDatabaseActivity.syncDb(com.u17od.upm.database.PasswordDatabase)",this,dbDownloadedOnSync);try{int syncResult = SyncResult.IN_SYNC;
        UPMApplication app = (UPMApplication) getApplication();
        if (dbDownloadedOnSync == null || dbDownloadedOnSync.getRevision() < app.getPasswordDatabase().getRevision()) {
            uploadDatabase();
            syncResult = SyncResult.UPLOAD_LOCAL;
        } else if (dbDownloadedOnSync.getRevision() > app.getPasswordDatabase().getRevision()) {
            app.copyFile(downloadedDatabaseFile, Utilities.getDatabaseFile(this), this);
            app.setPasswordDatabase(dbDownloadedOnSync);
            dbDownloadedOnSync.setDatabaseFile(Utilities.getDatabaseFile(this));
            setResult(RESULT_REFRESH);
            syncResult = SyncResult.KEEP_REMOTE;
            UIUtilities.showToast(this, R.string.new_db_downloaded);

            /*// Ask the BackupManager to backup the database using*/
            /*// Google's cloud backup service.*/
            Log.i("SyncDatabaseActivity", "Calling BackupManager().dataChanged()");
            app.getBackupManager().dataChanged();

            finish();
        } else if (dbDownloadedOnSync.getRevision() == app.getPasswordDatabase().getRevision()) {
            UIUtilities.showToast(this, R.string.db_uptodate);
            finish();
        }
        app.setTimeOfLastSync(new Date());
        if (downloadedDatabaseFile != null) {
            downloadedDatabaseFile.delete();
        }
        {com.mijack.Xlog.logMethodExit("int com.u17od.upm.SyncDatabaseActivity.syncDb(com.u17od.upm.database.PasswordDatabase)",this);return syncResult;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.u17od.upm.SyncDatabaseActivity.syncDb(com.u17od.upm.database.PasswordDatabase)",this,throwable);throw throwable;}
    }

    protected PasswordDatabase getPasswordDatabase() {
        com.mijack.Xlog.logMethodEnter("com.u17od.upm.database.PasswordDatabase com.u17od.upm.SyncDatabaseActivity.getPasswordDatabase()",this);try{com.mijack.Xlog.logMethodExit("com.u17od.upm.database.PasswordDatabase com.u17od.upm.SyncDatabaseActivity.getPasswordDatabase()",this);return ((UPMApplication) getApplication()).getPasswordDatabase();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.u17od.upm.database.PasswordDatabase com.u17od.upm.SyncDatabaseActivity.getPasswordDatabase()",this,throwable);throw throwable;}
    }

}
