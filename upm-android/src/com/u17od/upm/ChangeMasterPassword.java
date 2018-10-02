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
import java.security.GeneralSecurityException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.u17od.upm.crypto.InvalidPasswordException;
import com.u17od.upm.database.PasswordDatabase;
import com.u17od.upm.database.ProblemReadingDatabaseFile;

public class ChangeMasterPassword extends Activity implements OnClickListener {

    private EditText existingPassword;
    private EditText newPassword;
    private EditText newPasswordConfirmation;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.ChangeMasterPassword.onCreate(android.os.Bundle)",this,savedInstanceState);try{super.onCreate(savedInstanceState);
        setContentView(R.layout.change_master_password);
    
        existingPassword = (EditText) findViewById(R.id.existing_master_password);
        newPassword = (EditText) findViewById(R.id.new_master_password);
        newPasswordConfirmation = (EditText) findViewById(R.id.new_master_password_confirm);
    
        /*// Make this class the listener for the click event on the OK button*/
        Button okButton = (Button) findViewById(R.id.change_master_password_ok_button);
        okButton.setOnClickListener(this);com.mijack.Xlog.logMethodExit("void com.u17od.upm.ChangeMasterPassword.onCreate(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.ChangeMasterPassword.onCreate(android.os.Bundle)",this,throwable);throw throwable;}
    }

    @Override
    public void onClick(View v) {
        com.mijack.Xlog.logMethodEnter("void com.u17od.upm.ChangeMasterPassword.onClick(android.view.View)",this,v);try{switch (v.getId()) {
        case R.id.change_master_password_ok_button:
            /*// Check the two new password match*/
            if (existingPassword.getText().length() == 0) {
                Toast.makeText(this, R.string.request_master_password, Toast.LENGTH_SHORT).show();
            } else if (!newPassword.getText().toString().equals(newPasswordConfirmation.getText().toString())) {
                Toast.makeText(this, R.string.new_passwords_dont_match, Toast.LENGTH_SHORT).show();
            } else if (newPassword.getText().length() < CreateNewDatabase.MIN_PASSWORD_LENGTH) {
                String passwordTooShortResStr = getString(R.string.password_too_short);
                String resultsText = String.format(passwordTooShortResStr, CreateNewDatabase.MIN_PASSWORD_LENGTH);
                Toast.makeText(this, resultsText, Toast.LENGTH_SHORT).show();
            } else {
                new DecryptAndSaveDatabaseAsyncTask().execute();
            }
            break;
        }com.mijack.Xlog.logMethodExit("void com.u17od.upm.ChangeMasterPassword.onClick(android.view.View)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.ChangeMasterPassword.onClick(android.view.View)",this,throwable);throw throwable;}
    }

    private UPMApplication getUPMApplication() {
        com.mijack.Xlog.logMethodEnter("com.u17od.upm.UPMApplication com.u17od.upm.ChangeMasterPassword.getUPMApplication()",this);try{com.mijack.Xlog.logMethodExit("com.u17od.upm.UPMApplication com.u17od.upm.ChangeMasterPassword.getUPMApplication()",this);return (UPMApplication) getApplication();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.u17od.upm.UPMApplication com.u17od.upm.ChangeMasterPassword.getUPMApplication()",this,throwable);throw throwable;}
    }

    private PasswordDatabase getPasswordDatabase() {
        com.mijack.Xlog.logMethodEnter("com.u17od.upm.database.PasswordDatabase com.u17od.upm.ChangeMasterPassword.getPasswordDatabase()",this);try{com.mijack.Xlog.logMethodExit("com.u17od.upm.database.PasswordDatabase com.u17od.upm.ChangeMasterPassword.getPasswordDatabase()",this);return getUPMApplication().getPasswordDatabase();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.u17od.upm.database.PasswordDatabase com.u17od.upm.ChangeMasterPassword.getPasswordDatabase()",this,throwable);throw throwable;}
    }

    public class DecryptAndSaveDatabaseAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            com.mijack.Xlog.logMethodEnter("void com.u17od.upm.ChangeMasterPassword$DecryptAndSaveDatabaseAsyncTask.onPreExecute()",this);try{progressDialog = ProgressDialog.show(ChangeMasterPassword.this, "", getString(R.string.saving_database));com.mijack.Xlog.logMethodExit("void com.u17od.upm.ChangeMasterPassword$DecryptAndSaveDatabaseAsyncTask.onPreExecute()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.ChangeMasterPassword$DecryptAndSaveDatabaseAsyncTask.onPreExecute()",this,throwable);throw throwable;}
        }
        
        @Override
        protected Integer doInBackground(Void... params) {
            com.mijack.Xlog.logMethodEnter("java.lang.Integer com.u17od.upm.ChangeMasterPassword$DecryptAndSaveDatabaseAsyncTask.doInBackground([java.lang.Void)",this,params);try{Integer messageCode = null;
            try {
                /*// Attempt to decrypt the database so-as to test the password*/
                char[] password = existingPassword.getText().toString().toCharArray();
                new PasswordDatabase(Utilities.getDatabaseFile(ChangeMasterPassword.this), password);

                /*// Re-encrypt the database*/
                getPasswordDatabase().changePassword(newPassword.getText().toString().toCharArray());
                synchronized (UPMApplication.sDataLock) {
                    getPasswordDatabase().save();
                }

                /*// Ask the BackupManager to backup the database using*/
                /*// Google's cloud backup service.*/
                Log.i("ChangeMasterPassword", "Calling BackupManager().dataChanged()");
                getUPMApplication().getBackupManager().dataChanged();

                /*// We're finished with this activity so take it off the stack*/
                finish();
            } catch (InvalidPasswordException e) {
                Log.e("ChangeMasterPassword", e.getMessage(), e);
                messageCode = R.string.invalid_password;
            } catch (IOException e) {
                Log.e("ChangeMasterPassword", e.getMessage(), e);
                messageCode = R.string.generic_error;
            } catch (GeneralSecurityException e) {
                Log.e("ChangeMasterPassword", e.getMessage(), e);
                messageCode = R.string.generic_error;
            } catch (ProblemReadingDatabaseFile e) {
                Log.e("ChangeMasterPassword", e.getMessage(), e);
                messageCode = R.string.generic_error;
            }
            
            {com.mijack.Xlog.logMethodExit("java.lang.Integer com.u17od.upm.ChangeMasterPassword$DecryptAndSaveDatabaseAsyncTask.doInBackground([java.lang.Void)",this);return messageCode;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Integer com.u17od.upm.ChangeMasterPassword$DecryptAndSaveDatabaseAsyncTask.doInBackground([java.lang.Void)",this,throwable);throw throwable;}
        }
        
        protected void onPostExecute(Integer messageCode) {
            com.mijack.Xlog.logMethodEnter("void com.u17od.upm.ChangeMasterPassword$DecryptAndSaveDatabaseAsyncTask.onPostExecute(java.lang.Integer)",this,messageCode);try{progressDialog.dismiss();

            if (messageCode != null) {
                Toast.makeText(ChangeMasterPassword.this, messageCode, Toast.LENGTH_SHORT).show();
                if (messageCode == R.string.invalid_password) {
                    /*// Set focus back to the password and select all characters*/
                    existingPassword.requestFocus();
                    existingPassword.selectAll();
                }
            }com.mijack.Xlog.logMethodExit("void com.u17od.upm.ChangeMasterPassword$DecryptAndSaveDatabaseAsyncTask.onPostExecute(java.lang.Integer)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.u17od.upm.ChangeMasterPassword$DecryptAndSaveDatabaseAsyncTask.onPostExecute(java.lang.Integer)",this,throwable);throw throwable;}
            
        }

    }

}
