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

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import com.u17od.upm.database.PasswordDatabase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.backup.BackupManager;
import android.os.AsyncTask;
import android.util.Log;

public class SaveDatabaseAsyncTask extends AsyncTask<PasswordDatabase, Void, String> {

    private ProgressDialog progressDialog;
    private Activity activity;
    private Callback callback;

    public SaveDatabaseAsyncTask(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SaveDatabaseAsyncTask.onPreExecute()",this);try{progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.saving_database));com.mijack.Xlog.logMethodExit("void com.u17od.upm.SaveDatabaseAsyncTask.onPreExecute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SaveDatabaseAsyncTask.onPreExecute()",this,throwable);throw throwable;}
    }

    @Override
    protected String doInBackground(PasswordDatabase... params) {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.u17od.upm.SaveDatabaseAsyncTask.doInBackground([com.u17od.upm.database.PasswordDatabase)",this,params);try{String message = null;

        try {
            synchronized (UPMApplication.sDataLock) {
                params[0].save();
            }

            /*// Ask the BackupManager to backup the database using*/
            /*// Google's cloud backup service.*/
            Log.i("SaveDatabaseAsyncTask", "Calling BackupManager().dataChanged()");
            ((UPMApplication) activity.getApplication()).getBackupManager().dataChanged();
        } catch (IllegalBlockSizeException e) {
            Log.e("SaveDatabaseAsyncTask", e.getMessage(), e);
            message = String.format(activity.getString(R.string.problem_saving_db), e.getMessage());
        } catch (BadPaddingException e) {
            Log.e("SaveDatabaseAsyncTask", e.getMessage(), e);
            message = String.format(activity.getString(R.string.problem_saving_db), e.getMessage());
        } catch (IOException e) {
            Log.e("SaveDatabaseAsyncTask", e.getMessage(), e);
            message = String.format(activity.getString(R.string.problem_saving_db), e.getMessage());
        }

        {com.mijack.Xlog.logMethodExit("java.lang.String com.u17od.upm.SaveDatabaseAsyncTask.doInBackground([com.u17od.upm.database.PasswordDatabase)",this);return message;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.u17od.upm.SaveDatabaseAsyncTask.doInBackground([com.u17od.upm.database.PasswordDatabase)",this,throwable);throw throwable;}
    }

    @Override
    protected void onPostExecute(String result) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.SaveDatabaseAsyncTask.onPostExecute(java.lang.String)",this,result);try{if (result != null) {
            UIUtilities.showToast(activity, result, true);
        }

        progressDialog.dismiss();
        
        callback.execute();com.mijack.Xlog.logMethodExit("void com.u17od.upm.SaveDatabaseAsyncTask.onPostExecute(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.SaveDatabaseAsyncTask.onPostExecute(java.lang.String)",this,throwable);throw throwable;}
    }

}
