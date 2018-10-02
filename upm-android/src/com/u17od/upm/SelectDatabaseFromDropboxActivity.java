package com.u17od.upm;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.core.DbxException;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.u17od.upm.database.PasswordDatabase;
import com.u17od.upm.dropbox.DropboxClientFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.u17od.upm.DropboxConstants.DROPBOX_ACCESS_TOKEN;

public class SelectDatabaseFromDropboxActivity extends ListActivity {

    private static final String TAG = SelectDatabaseFromDropboxActivity.class.getName();
    private SharedPreferences prefs;

    private static final int ENTER_PW_REQUEST_CODE = 111;

    private DropboxAPI<AndroidAuthSession> mDBApi;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(Utilities.DROPBOX_PREFS, MODE_PRIVATE);
        String dropboxAccessToken = prefs.getString(DROPBOX_ACCESS_TOKEN, null);
        Log.i("onCreate", "dropboxAccessToken=" + dropboxAccessToken);

        if (dropboxAccessToken == null) {
            Auth.startOAuth2Authentication(SelectDatabaseFromDropboxActivity.this, DropboxConstants.APP_KEY);
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    protected void onResume() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onResume()",this);try{super.onResume();

        String dropboxAccessToken = prefs.getString(DROPBOX_ACCESS_TOKEN, null);
        Log.i("onResume", "dropboxAccessToken=" + dropboxAccessToken);

        if (dropboxAccessToken == null) {
            dropboxAccessToken = Auth.getOAuth2Token();
            Log.i("onResume", "dropboxAccessToken after getOAuth2Token=" + dropboxAccessToken);
            if (dropboxAccessToken != null) {
                prefs.edit().putString(DROPBOX_ACCESS_TOKEN, dropboxAccessToken).commit();
                DropboxClientFactory.init(dropboxAccessToken);
                /*// Launch the async task where we'll download database filenames from*/
                /*// Dropbox and populate the ListView*/
                new DownloadListOfFilesTask().execute();
            }
        } else {
            DropboxClientFactory.init(dropboxAccessToken);
            /*// Launch the async task where we'll download database filenames from*/
            /*// Dropbox and populate the ListView*/
            new DownloadListOfFilesTask().execute();
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onResume()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onResume()",this,throwable);throw throwable;}
    }

    @Override
    protected void onStop() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onStop()",this);try{super.onStop();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onStop()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onStop()",this,throwable);throw throwable;}
    }

    /**
     * The only way this method can be called is if we're returning from
     * EnterMasterPassword after retrieving a database.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onActivityResult(int,int,android.content.Intent)",this,requestCode,resultCode,intent);try{if (resultCode == Activity.RESULT_CANCELED) {
            UIUtilities.showToast(this, R.string.enter_password_cancalled);
        } else {
            if (requestCode == ENTER_PW_REQUEST_CODE) {
                /*// Setting the DatabaseFileName preference effectively says*/
                /*// that this is the db to open when the app starts*/
                Utilities.setSyncMethod(Prefs.SyncMethod.DROPBOX, this);
                String selectedDropboxFilename =
                        Utilities.getConfig(this, Utilities.DROPBOX_PREFS,
                                Utilities.DROPBOX_SELECTED_FILENAME);
                Utilities.setDatabaseFileName(selectedDropboxFilename,
                        SelectDatabaseFromDropboxActivity.this);

                /*// Put a reference to the decrypted database on the Application object*/
                UPMApplication app = (UPMApplication) getApplication();
                app.setPasswordDatabase(EnterMasterPassword.decryptedPasswordDatabase);
                app.setTimeOfLastSync(new Date());

                setResult(RESULT_OK);
                finish();
            }
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onActivityResult(int,int,android.content.Intent)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onActivityResult(int,int,android.content.Intent)",this,throwable);throw throwable;}
    }

    /**
     * Called when an file from the listview is selected
     */
    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onListItemClick(android.widget.ListView,android.view.View,int,long)",this,lv,v,position,id);try{String selectedFileName = (String) lv.getItemAtPosition(position);
        Utilities.setConfig(this, Utilities.DROPBOX_PREFS,
                Utilities.DROPBOX_SELECTED_FILENAME, selectedFileName);
        new DownloadDatabaseTask().execute(selectedFileName);com.mijack.Xlog.logMethodExit("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onListItemClick(android.widget.ListView,android.view.View,int,long)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SelectDatabaseFromDropboxActivity.onListItemClick(android.widget.ListView,android.view.View,int,long)",this,throwable);throw throwable;}
    }

    private class DownloadListOfFilesTask extends AsyncTask<Void, Void, Integer> {

        private static final int ERROR_DROPBOX = 1;
        private static final int ERROR_DROPBOX_INVALID_TOKEN = 2;

        private List<Metadata> dropBoxEntries;

        protected void onPreExecute() {
            com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.onPreExecute()",this);try{progressDialog = ProgressDialog.show(SelectDatabaseFromDropboxActivity.this,
                    "", getString(R.string.dropbox_get_file_list));com.mijack.Xlog.logMethodExit("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.onPreExecute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.onPreExecute()",this,throwable);throw throwable;}
        }

        @Override
        protected Integer doInBackground(Void... params) {
            com.mijack.Xlog.logMethodEnter("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.doInBackground([java.lang.Void)",this,params);try{try {
                ListFolderResult filderContents =
                        DropboxClientFactory.getClient().files().listFolder("");
                dropBoxEntries = filderContents.getEntries();
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.doInBackground([java.lang.Void)",this);return 0;}
            } catch (InvalidAccessTokenException e) {
                Log.e(TAG, "InvalidAccessTokenException downloading database", e);
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.doInBackground([java.lang.Void)",this);return ERROR_DROPBOX_INVALID_TOKEN;}
            } catch (DbxException e) {
                Log.e(TAG, "DbxException downloading database", e);
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.doInBackground([java.lang.Void)",this);return ERROR_DROPBOX;}
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.doInBackground([java.lang.Void)",this,throwable);throw throwable;}
        }

        @Override
        protected void onPostExecute(Integer result) {
            com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.onPostExecute(java.lang.Integer)",this,result);try{progressDialog.dismiss();

            switch (result) {
                case 0:
                    setListAdapter(new ArrayAdapter<String>(
                            SelectDatabaseFromDropboxActivity.this,
                            android.R.layout.simple_list_item_1,
                            dropboxFiles(dropBoxEntries)));
                    break;
                case ERROR_DROPBOX_INVALID_TOKEN:
                    prefs.edit().remove(DROPBOX_ACCESS_TOKEN).commit();
                    UIUtilities.showToast(SelectDatabaseFromDropboxActivity.this,
                            R.string.dropbox_token_problem, true);
                    finish();
                    break;
                case ERROR_DROPBOX:
                    UIUtilities.showToast(SelectDatabaseFromDropboxActivity.this,
                            R.string.dropbox_problem, true);
                    finish();
                    break;
            }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.onPostExecute(java.lang.Integer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.onPostExecute(java.lang.Integer)",this,throwable);throw throwable;}
        }


        /*
         * Extract the filenames from the given list of Dropbox Entries and return
         * a simple String array.
         */
        private List<String> dropboxFiles(List<Metadata> dpEntries) {
            com.mijack.Xlog.logMethodEnter("java.util.ArrayList com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.dropboxFiles(java.util.ArrayList)",this,dpEntries);try{List<String> fileNames = new ArrayList<String>();
            for (Metadata entry : dpEntries) {
                fileNames.add(entry.getName());
            }
            {com.mijack.Xlog.logMethodExit("java.util.ArrayList com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.dropboxFiles(java.util.ArrayList)",this);return fileNames;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.ArrayList com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadListOfFilesTask.dropboxFiles(java.util.ArrayList)",this,throwable);throw throwable;}
        }

    }


    private class DownloadDatabaseTask extends AsyncTask<String, Void, Integer> {

        private static final String TAG = "DownloadDatabaseTask";
        private static final int ERROR_IO = 1;
        private static final int ERROR_DROPBOX = 2;
        private static final int ERROR_DROPBOX_INVALID_TOKEN = 3;
        private static final int NOT_UPM_DB = 4;

        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.onPreExecute()",this);try{progressDialog = ProgressDialog.show(SelectDatabaseFromDropboxActivity.this,
                    "", getString(R.string.downloading_db));com.mijack.Xlog.logMethodExit("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.onPreExecute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.onPreExecute()",this,throwable);throw throwable;}
        }

        @Override
        protected Integer doInBackground(String... fileName) {
            com.mijack.Xlog.logMethodEnter("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.String)",this,fileName);try{FileOutputStream outputStream = null;
            try {
                /*// Download the file and save it to UPM's internal files area*/
                File file = new File(getFilesDir(), fileName[0]);
                outputStream = new FileOutputStream(file);
                DropboxClientFactory.getClient()
                        .files()
                        .download("/" + fileName[0])
                        .download(outputStream);

                /*// Check this is a UPM database file*/
                if (!PasswordDatabase.isPasswordDatabase(file)) {
                    {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.String)",this);return NOT_UPM_DB;}
                }
                EnterMasterPassword.databaseFileToDecrypt = file;

                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.String)",this);return 0;}
            } catch (IOException e) {
                Log.e(TAG, "IOException downloading database", e);
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.String)",this);return ERROR_IO;}
            } catch (InvalidAccessTokenException e) {
                Log.e(TAG, "InvalidAccessTokenException downloading database", e);
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.String)",this);return ERROR_DROPBOX_INVALID_TOKEN;}
            } catch (DbxException e) {
                Log.e(TAG, "DbxException downloading database", e);
                {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.String)",this);return ERROR_DROPBOX;}
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException closing database file stream", e);
                        {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.String)",this);return ERROR_IO;}
                    }
                }
            }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Integer com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.doInBackground([java.lang.String)",this,throwable);throw throwable;}
        }

        @Override
        protected void onPostExecute(Integer result) {
            com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.onPostExecute(java.lang.Integer)",this,result);try{progressDialog.dismiss();

            Intent i = null;
            switch (result) {
                case 0:
                    /*// Call up the EnterMasterPassword activity*/
                    /*// When it returns we'll pick up in the method onActivityResult*/
                    i = new Intent(SelectDatabaseFromDropboxActivity.this, EnterMasterPassword.class);
                    startActivityForResult(i, ENTER_PW_REQUEST_CODE);
                    break;
                case ERROR_IO:
                    UIUtilities.showToast(SelectDatabaseFromDropboxActivity.this,
                            R.string.problem_saving_db, true);
                    finish();
                    break;
                case ERROR_DROPBOX:
                    UIUtilities.showToast(SelectDatabaseFromDropboxActivity.this,
                            R.string.dropbox_problem, true);
                    finish();
                    break;
                case ERROR_DROPBOX_INVALID_TOKEN:
                    prefs.edit().remove(DROPBOX_ACCESS_TOKEN).commit();
                    UIUtilities.showToast(SelectDatabaseFromDropboxActivity.this,
                            R.string.dropbox_token_problem, true);
                    finish();
                    break;
                case NOT_UPM_DB:
                    UIUtilities.showToast(SelectDatabaseFromDropboxActivity.this,
                            R.string.not_password_database, true);
                    finish();
                    break;
            }com.mijack.Xlog.logMethodExit("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.onPostExecute(java.lang.Integer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SelectDatabaseFromDropboxActivity$DownloadDatabaseTask.onPostExecute(java.lang.Integer)",this,throwable);throw throwable;}
        }
    }

}
