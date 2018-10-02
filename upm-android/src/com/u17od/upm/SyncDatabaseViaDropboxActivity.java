package com.u17od.upm;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.SearchResult;
import com.dropbox.core.v2.files.WriteMode;
import com.u17od.upm.dropbox.DropboxClientFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.u17od.upm.DropboxConstants.DROPBOX_ACCESS_TOKEN;

public class SyncDatabaseViaDropboxActivity extends SyncDatabaseActivity {

    private static final String TAG = SyncDatabaseViaDropboxActivity.class.getName();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SyncDatabaseViaDropboxActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(Utilities.DROPBOX_PREFS, MODE_PRIVATE);
        String dropboxAccessToken = prefs.getString(DROPBOX_ACCESS_TOKEN, null);
        Log.i("onCreate", "dropboxAccessToken=" + dropboxAccessToken);

        if (dropboxAccessToken == null) {
            Auth.startOAuth2Authentication(SyncDatabaseViaDropboxActivity.this, DropboxConstants.APP_KEY);
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SyncDatabaseViaDropboxActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SyncDatabaseViaDropboxActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SyncDatabaseViaDropboxActivity.onResume()",this);try{super.onResume();

        String dropboxAccessToken = prefs.getString(DROPBOX_ACCESS_TOKEN, null);
        Log.i("onResume", "dropboxAccessToken=" + dropboxAccessToken);

        if (dropboxAccessToken == null) {
            dropboxAccessToken = Auth.getOAuth2Token();
            Log.i("onResume", "dropboxAccessToken after getOAuth2Token=" + dropboxAccessToken);
            if (dropboxAccessToken != null) {
                prefs.edit().putString(DROPBOX_ACCESS_TOKEN, dropboxAccessToken).commit();
                DropboxClientFactory.init(dropboxAccessToken);
                downloadDatabase();
            }
        } else {
            DropboxClientFactory.init(dropboxAccessToken);
            downloadDatabase();
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SyncDatabaseViaDropboxActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SyncDatabaseViaDropboxActivity.onResume()",this,throwable);throw throwable;}
    }

    @Override
    protected void uploadDatabase() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SyncDatabaseViaDropboxActivity.uploadDatabase()",this);try{new UploadDatabaseTask().execute();com.mijack.Xlog.logMethodExit("void com.u17od.upm.SyncDatabaseViaDropboxActivity.uploadDatabase()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SyncDatabaseViaDropboxActivity.uploadDatabase()",this,throwable);throw throwable;}
    }

    @Override
    protected void downloadDatabase() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SyncDatabaseViaDropboxActivity.downloadDatabase()",this);try{new DownloadDatabaseTask().execute();com.mijack.Xlog.logMethodExit("void com.u17od.upm.SyncDatabaseViaDropboxActivity.downloadDatabase()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SyncDatabaseViaDropboxActivity.downloadDatabase()",this,throwable);throw throwable;}
    }

    private class DownloadDatabaseTask extends AsyncTask<Void, Void, Integer> {

        private static final String TAG = "DownloadDatabaseTask";
        private static final int ERROR_IO = 1;
        private static final int ERROR_DROPBOX = 2;
        private static final int ERROR_DROPBOX_INVALID_TOKEN = 3;
        private static final int REMOTE_FILE_DOESNT_EXIST = 4;

        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.onPreExecute()",this);try{progressDialog = ProgressDialog.show(SyncDatabaseViaDropboxActivity.this,
                    "", getString(R.string.downloading_db));com.mijack.Xlog.logMethodExit("void com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.onPreExecute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.onPreExecute()",this,throwable);throw throwable;}
        }

        @Override
        protected Integer doInBackground(Void... params) {
            com.mijack.Xlog.logMethodEnter("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.Void)",this,params);try{FileOutputStream outputStream = null;
            try {
                /*// Download the file and save it to a temp file*/
                String remoteFileName = Utilities.getDatabaseFileName(SyncDatabaseViaDropboxActivity.this);
                downloadedDatabaseFile = new File(getCacheDir(), remoteFileName);
                outputStream = new FileOutputStream(downloadedDatabaseFile);

                SearchResult searchResults = DropboxClientFactory.getClient()
                        .files().search("", remoteFileName);
                if (searchResults.getMatches().size() == 0) {
                    {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.Void)",this);return REMOTE_FILE_DOESNT_EXIST;}
                }

                FileMetadata metadata = DropboxClientFactory.getClient()
                        .files()
                        .download("/" + remoteFileName)
                        .download(outputStream);

                /*// Store the db file rev for use in the UploadDatabaseTask*/
                /*// Prefs is used instead of the activity instance because the*/
                /*// activity could be recreate between now and then meaning the*/
                /*// instance variables are reset.*/
                Utilities.setConfig(SyncDatabaseViaDropboxActivity.this,
                        Utilities.DROPBOX_PREFS, Utilities.DROPBOX_DB_REV,
                        metadata.getRev());

                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.Void)",this);return 0;}
            } catch (IOException e) {
                Log.e(TAG, "IOException downloading database", e);
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.Void)",this);return ERROR_IO;}
            } catch (InvalidAccessTokenException e) {
                Log.e(TAG, "InvalidAccessTokenException downloading database", e);
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.Void)",this);return ERROR_DROPBOX_INVALID_TOKEN;}
            } catch (DbxException e) {
                Log.e(TAG, "DbxException downloading database", e);
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.Void)",this);return ERROR_DROPBOX;}
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException closing database file stream", e);
                        {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.Void)",this);return ERROR_IO;}
                    }
                }
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.Void)",this,throwable);throw throwable;}
        }

        @Override
        protected void onPostExecute(Integer result) {
            com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.onPostExecute(java.lang.Integer)",this,result);try{progressDialog.dismiss();

            switch (result) {
                case 0:
                    decryptDatabase();
                    break;
                case ERROR_IO:
                    UIUtilities.showToast(SyncDatabaseViaDropboxActivity.this,
                            R.string.problem_saving_db, true);
                    finish();
                    break;
                case ERROR_DROPBOX:
                    UIUtilities.showToast(SyncDatabaseViaDropboxActivity.this,
                            R.string.dropbox_problem, true);
                    finish();
                    break;
                case ERROR_DROPBOX_INVALID_TOKEN:
                    prefs.edit().remove(DROPBOX_ACCESS_TOKEN).commit();
                    UIUtilities.showToast(SyncDatabaseViaDropboxActivity.this,
                            R.string.dropbox_token_problem, true);
                    finish();
                    break;
                case REMOTE_FILE_DOESNT_EXIST:
                    syncDb(null);
                    break;
            }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.onPostExecute(java.lang.Integer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SyncDatabaseViaDropboxActivity$DownloadDatabaseTask.onPostExecute(java.lang.Integer)",this,throwable);throw throwable;}
        }
    }

    private class UploadDatabaseTask extends AsyncTask<Void, Void, Integer> {

        private static final String TAG = "UploadDatabaseTask";
        private static final int UPLOAD_OK = 0;
        private static final int ERROR_IO = 1;
        private static final int ERROR_DROPBOX = 2;

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SyncDatabaseViaDropboxActivity$UploadDatabaseTask.onPreExecute()",this);try{progressDialog = ProgressDialog.show(
                    SyncDatabaseViaDropboxActivity.this, "",
                    getString(R.string.uploading_database));com.mijack.Xlog.logMethodExit("void com.u17od.upm.SyncDatabaseViaDropboxActivity$UploadDatabaseTask.onPreExecute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SyncDatabaseViaDropboxActivity$UploadDatabaseTask.onPreExecute()",this,throwable);throw throwable;}
        }

        @Override
        protected Integer doInBackground(Void... params) {
            com.mijack.Xlog.logMethodEnter("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$UploadDatabaseTask.doInBackground([java.lang.Void)",this,params);try{int result = UPLOAD_OK;

            FileInputStream inputStream = null;
            try {
                File databaseFile = getPasswordDatabase().getDatabaseFile();
                inputStream = new FileInputStream(databaseFile);
                DropboxClientFactory.getClient().files()
                        .uploadBuilder("/" + databaseFile.getName())
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
            } catch (IOException e) {
                Log.e(TAG, "IOException during database upload", e);
                result = ERROR_IO;
            } catch (DbxException e) {
                Log.e(TAG, "DbxException downloading database", e);
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$UploadDatabaseTask.doInBackground([java.lang.Void)",this);return ERROR_DROPBOX;}
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException during database upload", e);
                        {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$UploadDatabaseTask.doInBackground([java.lang.Void)",this);return ERROR_IO;}
                    }
                }
            }

            {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$UploadDatabaseTask.doInBackground([java.lang.Void)",this);return result;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Integer com.u17od.upm.SyncDatabaseViaDropboxActivity$UploadDatabaseTask.doInBackground([java.lang.Void)",this,throwable);throw throwable;}
        }

        @Override
        protected void onPostExecute(Integer result) {
            com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SyncDatabaseViaDropboxActivity$UploadDatabaseTask.onPostExecute(java.lang.Integer)",this,result);try{progressDialog.dismiss();
            switch (result) {
            case ERROR_IO:
                UIUtilities.showToast(SyncDatabaseViaDropboxActivity.this, R.string.problem_reading_upm_db);
                break;
            case ERROR_DROPBOX:
                UIUtilities.showToast(SyncDatabaseViaDropboxActivity.this, R.string.dropbox_problem);
                break;
            default:
                UIUtilities.showToast(SyncDatabaseViaDropboxActivity.this, R.string.db_sync_complete);
                break;
            }
            finish();com.mijack.Xlog.logMethodExit("void com.u17od.upm.SyncDatabaseViaDropboxActivity$UploadDatabaseTask.onPostExecute(java.lang.Integer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SyncDatabaseViaDropboxActivity$UploadDatabaseTask.onPostExecute(java.lang.Integer)",this,throwable);throw throwable;}
        }

    }

}
